import {Component, inject, OnInit} from '@angular/core';

import {
  Coordinate,
  Location, NominatimService,
  ORSFeatureCollection, ORSService,
  Route, TripOffer, TripOfferService,
  TripRequestBody,
  TripRequestDTO,
  TripRequestService
} from '../../../api/sep_drive';
import {FormControl, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {GeolocationService} from '../../services/geolocation.service';

import {debounceTime, distinctUntilChanged, firstValueFrom} from 'rxjs';
import {MatCard, MatCardActions, MatCardContent, MatCardTitle} from '@angular/material/card';
import {MeterToKmPipe} from '../../pipes/meter-to-km.pipe';
import {SecondsToTimePipe} from '../../pipes/seconds-to-time.pipe';
import {EuroPipe} from '../../pipes/euro.pipe';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatDivider} from '@angular/material/divider';
import {NgIf} from '@angular/common';
import {MatFormField, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import {CdkDrag, CdkDragDrop, CdkDragHandle, CdkDropList, moveItemInArray} from '@angular/cdk/drag-drop';
import {MatRadioButton, MatRadioGroup} from '@angular/material/radio';
import {MatIcon} from '@angular/material/icon';
import {TripVisualizerComponent} from '../trip-visualizer/trip-visualizer.component';
import {CarTypePipe} from '../../pipes/car-type.pipe';
import {MatSnackBar} from '@angular/material/snack-bar';
import {TripOffersComponent} from '../trip-offers/trip-offers.component';
import {Router} from '@angular/router';
import {AngularNotificationService} from '../../services/angular-notification.service';

@Component({
  selector: 'app-trip-request',
  imports: [
    MatCard,
    MatCardContent,
    MeterToKmPipe,
    SecondsToTimePipe,
    EuroPipe,
    MatButton,
    MatDivider,
    ReactiveFormsModule,
    MatCardTitle,
    MatFormField,
    MatLabel,
    MatInput,
    CdkDropList,
    CdkDragHandle,
    CdkDrag,
    MatIconButton,
    MatIcon,
    MatRadioGroup,
    MatRadioButton,
    MatSuffix,
    NgIf,
    TripVisualizerComponent,
    CarTypePipe,
    TripOffersComponent
  ],
  templateUrl: './trip-request.component.html',
  styleUrl: './trip-request.component.css'
})
export class TripRequestComponent implements OnInit {
  angularNotificationService = inject(AngularNotificationService)

  tripOfferService = inject(TripOfferService)
  tripRequestService = inject(TripRequestService)
  nominatimService = inject(NominatimService)
  geolocationService = inject(GeolocationService)
  orsService = inject(ORSService)
  snackBar = inject(MatSnackBar)

  router = inject(Router)

  tripRequestDTO: TripRequestDTO | null = null

  route: Route | null = null
  orsFeatureCollection: ORSFeatureCollection | null = null

  suggestedLocations: Location[] = []
  selectedIndex = 0
  showCard: boolean = false
  showOffers: boolean = false

  stops: Location[] = []

  distance: number = 0
  duration: number = 0
  note: string = ''
  estimatedPrice: number = 0.0;

  form = new FormGroup({
    carType: new FormControl('SMALL'),
    query: new FormControl("")
  })

  acceptedTripOffer: TripOffer | null = null


  ngOnInit(): void {
    this.angularNotificationService.latestNotification$.subscribe({
      next: notification => {
        if (notification) {
          void this.getCurrentActiveTripRequest()
        }
      }
    })


    //If active trip request exist, then get information to display
    // this.tripRequestService.getCurrentActiveTripRequest().subscribe({
    //   next: tripRequest => {
    //     this.consumeTripRequestDTO(tripRequest)
    //   },
    //   error: err => {
    //     console.error(err)
    //     if (err.status === 404) {
    //       this.tripRequestDTO = null
    //     }
    //   }
    // })
    void this.getCurrentActiveTripRequest()

    //On changes in search bar, get new suggestions list
    this.form.get('query')!.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe({
      next: query => {
        this.nominatimService.search(query!).subscribe({
          next: response => {
            console.log(response)
            this.suggestedLocations = response
          },
          error: err => {
            console.error(err)
          }
        })
      },
      error: err => {
        console.error(err)
      }
    })
  }

  async getCurrentActiveTripRequest() {
    try {
      const tripRequest = await firstValueFrom(this.tripRequestService.getCurrentActiveTripRequest())
      this.consumeTripRequestDTO(tripRequest)
    } catch (error) {
      console.error(error)
      this.tripRequestDTO = null
    }
  }

  async getAcceptedTripOffer() {
    try {
      if (this.tripRequestDTO) {
        this.acceptedTripOffer = await firstValueFrom(this.tripOfferService.getAcceptedTripOffer(this.tripRequestDTO.tripRequestId))
      } else {
        this.acceptedTripOffer = null
      }
    } catch (error) {
      console.error(error)
      this.acceptedTripOffer = null
    }
  }

//---------------------------------Start of location suggestion
  search(): void {
    this.nominatimService.search(this.form.value.query!).subscribe({
      next: response => {
        const query = this.form.value.query
        this.suggestedLocations = response
        this.showCard = response.length > 0 && !!query && query.trim().length > 0
      },
      error: err => {
        console.error(err)
        this.showCard = false
      }
    })
  }

  clickCard(index: number): void {
    console.log("Chosen card: ", index)
    this.selectedIndex = index
  }

  clickCurrentLocation(): void {
    this.geolocationService.getCurrentLocation()
      .then((position) => {
        console.log(position)
        this.form.setValue({
          query: `${position.coords.latitude}, ${position.coords.longitude}`,
          carType: 'SMALL'
        })
        this.search()
      }).catch((err) => {
      console.log(err)
    })
  }

//---------------------------------Route creation

  updateRoute() {
    if (this.stops.length >= 2) {
      this.orsService.getRoute(this.stops.map(stop => {
        let coordinate: Coordinate = {
          latitude: stop.coordinate.latitude,
          longitude: stop.coordinate.longitude
        }
        return coordinate
      })).subscribe({
        next: value => {
          this.orsFeatureCollection = value
          this.route = {
            routeId: -1,
            stops: this.stops,
            geoJson: value
          }
        },
        error: err => {
          console.log(err)
        }
      })
    }
  }

//---------------------------------Trip request creation
  createTripRequest(): void {
    const tripRequestBody: TripRequestBody = {
      carType: this.form.value.carType!,
      geojson: this.orsFeatureCollection!,
      locations: this.stops,
      note: this.note
    }
    this.tripRequestService.createCurrentActiveTripRequest(tripRequestBody).subscribe({
      next: tripRequest => {
        this.consumeTripRequestDTO(tripRequest)
      },
      error: err => {
        console.log(err)
      }
    })
  }

  deleteTripRequest(): void {
    this.tripRequestService.deleteCurrentActiveTripRequest().subscribe({
      next: () => {
        this.tripRequestDTO = null
      },
      error: err => {
        console.log(err)
      }
    })
  }

  consumeTripRequestDTO(tripRequestDTO: TripRequestDTO): void {
    this.tripRequestDTO = tripRequestDTO
    this.route = tripRequestDTO.route
    const summary = this.route.geoJson.features[0].properties.summary
    this.distance = summary?.distance
    this.duration = summary?.duration

    void this.getAcceptedTripOffer()
  }

  carPrice(carType: String): number {
    switch (carType) {
      case 'SMALL':
        return 1
      case 'MEDIUM':
        return 2
      case 'DELUXE':
        return 10
      default:
        return 0
    }
  }

//-------------------------------Address list

  newLocationIsSame(newLocation: Location) {
    if (this.stops.length === 0) return false
    const lastLocation = this.stops[this.stops.length - 1]
    return lastLocation.coordinate.latitude === newLocation.coordinate.latitude &&
      lastLocation.coordinate.longitude === newLocation.coordinate.longitude && lastLocation.displayName === newLocation.displayName;
  }

  dragDropIsSameLocation(location1: Location, location2: Location) {
    return location1.coordinate.latitude === location2.coordinate.latitude && location1.coordinate.longitude === location2.coordinate.longitude &&
      location1.displayName === location2.displayName
  }

  onInputChange() {
    const query = this.form.get('query')?.value
    this.showCard = query !== null && query!.trim().length > 0
  }

  remove(index: number) {
    this.stops.splice(index, 1)
    if (this.stops.length < 2) {
      this.route = null
    } else {
      this.updateRoute()
    }
  }

  drop(event: CdkDragDrop<string[]>) {
    moveItemInArray(this.stops, event.previousIndex, event.currentIndex)

    for (let i = 0; i < this.stops.length - 1; i++) {
      if (this.dragDropIsSameLocation(this.stops[i], this.stops[i + 1])) {
        console.warn("Zwei gleiche Adressen können nicht hintereinander angefahren werden")
        this.snackBar.open("Zwei gleiche Adressen können nicht hintereinander angefahren werden", "OK")
        moveItemInArray(this.stops, event.currentIndex, event.previousIndex);
        return;
      }
    }
    this.updateRoute();
  }

  onConfirm() {
    const selected = this.suggestedLocations[this.selectedIndex]
    if (this.newLocationIsSame(selected)) {
      console.warn("Diese Adressen wurde bereits als Zieladresse hinzugefügt.")
      this.snackBar.open("Diese Adressen wurde bereits als Zieladresse hinzugefügt.", "OK")
      return
    }
    this.stops.push(selected)
    this.form.get('query')?.setValue('')
    this.suggestedLocations = []
    this.showCard = false
    this.updateRoute()
  }
//----------------------------------Navigation

  navigateToSimulation(): void {
    this.router.navigate(["/offer", this.acceptedTripOffer!.id])
  }

  toggleOffersCard() {
    this.showOffers = !this.showOffers
  }

  onOfferAccepted() {
    this.showOffers = false
  }
}
