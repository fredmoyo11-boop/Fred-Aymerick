import {Component, inject, OnInit} from '@angular/core';

import {
  Coordinate,
  Location, NominatimService,
  ORSFeatureCollection, ORSService,
  RouteDTO, RouteService,
  TripRequestBody,
  TripRequestDTO,
  TripRequestService
} from '../../../api/sep_drive';
import {FormControl, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {GeolocationService} from '../../services/geolocation.service';

import {debounceTime, distinctUntilChanged} from 'rxjs';
import {MatCard, MatCardContent, MatCardTitle} from '@angular/material/card';
import {MeterToKmPipe} from '../../pipes/meter-to-km.pipe';
import {SecondsToTimePipe} from '../../pipes/seconds-to-time.pipe';
import {EuroPipe} from '../../pipes/euro.pipe';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatDivider} from '@angular/material/divider';
import {MatList, MatListItem} from '@angular/material/list';
import {NgForOf, NgIf} from '@angular/common';
import {MatFormField, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import {CdkDrag, CdkDragDrop, CdkDragHandle, CdkDropList, moveItemInArray} from '@angular/cdk/drag-drop';
import {MatRadioButton, MatRadioGroup} from '@angular/material/radio';
import {MatIcon} from '@angular/material/icon';

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
    MatList,
    NgForOf,
    MatListItem,
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
    NgIf
  ],
  templateUrl: './trip-request.component.html',
  styleUrl: './trip-request.component.css'
})
export class TripRequestComponent implements OnInit{
  tripRequestService = inject(TripRequestService)
  nominatimService = inject(NominatimService)
  geolocationService = inject(GeolocationService)
  routeService = inject(RouteService)
  orsService =inject(ORSService)

  tripRequestDTO: TripRequestDTO | null = null

  routeDTO: RouteDTO | null = null
  orsFeatureCollection: ORSFeatureCollection | null = null

  suggestedLocations: Location[] = []
  selectedIndex = 0
  showCard: boolean = false

  stops: Location[] = []

  distance: number = 0
  duration: number = 0
  note: string = ''

  // searchForm = new FormGroup({
  //  query: new FormControl("")
  // })

  form = new FormGroup({ //Separate FormGroups?
    carType: new FormControl('SMALL'),
    query: new FormControl("")
  })


  ngOnInit(): void {
    //If active trip request exist, then get information to display
    this.tripRequestService.getCurrentActiveTripRequest().subscribe({
      next: tripRequest => {
        this.consumeTripRequestDTO(tripRequest)
      },
      error: err => {
        console.error(err)
        if (err.status === 404) {
          this.tripRequestDTO = null
        }
      }
    })

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

//---------------------------------Start of location suggestion
  search(): void {
    this.nominatimService.search(this.form.value.query!).subscribe({
      next: response => {
        const query = this.form.value.query
        this.suggestedLocations = response
        this.showCard = response.length > 0 &&  !!query && query.trim().length > 0
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
          carType: 'SMALL' //<---- Keep watch
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
          this.routeDTO = {
            id: -1,
            stops: this.stops,
            geojson: value
          }
        },
        error: err => {
          console.log(err)
        }
      })
    }
  }
//---------------------------------Start of trip request creation
  createTripRequest(): void {
    const tripRequestBody : TripRequestBody = {
      carType: this.form.value.carType!,
      geojson: this.orsFeatureCollection!,
      locations: this.stops,
      note: this.note
    }
    this.tripRequestService.createCurrentTripRequest(tripRequestBody).subscribe({
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
    this.routeDTO = tripRequestDTO.route
    const summary = this.routeDTO.geojson.features[0].properties.summary
    this.distance = summary?.distance
    this.duration = summary?.duration
  }
//-------------------------------Address list

  onInputChange() {
    const query = this.form.get('query')?.value
    this.showCard = query !== null && query!.trim().length > 0
  }

  remove(index: number) {
    this.stops.splice(index, 1)
    if (this.stops.length < 2) {
      this.routeDTO = null
    } else {
      this.updateRoute()
    }
  }

  drop(event: CdkDragDrop<string[]>) {
    moveItemInArray(this.stops, event.previousIndex, event.currentIndex)
    this.updateRoute()
  }

  onConfirm() {
    const selected = this.suggestedLocations[this.selectedIndex]
    this.stops.push(selected)
    this.form.get('query')?.setValue('')
    this.suggestedLocations = []
    this.showCard = false
    this.updateRoute()
  }
}
