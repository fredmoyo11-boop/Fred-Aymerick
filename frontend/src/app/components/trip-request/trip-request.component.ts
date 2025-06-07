import {Component, inject, OnInit} from '@angular/core';

import {
  Location, NominatimService,
  ORSFeatureCollection,
  RouteDTO,
  TripRequestBody,
  TripRequestDTO,
  TripRequestService
} from '../../../api/sep_drive';
import {FormControl, FormGroup} from '@angular/forms';
import {GeolocationService} from '../../services/geolocation.service';
import {MAT_DIALOG_DATA} from '@angular/material/dialog';
import {debounceTime, distinctUntilChanged} from 'rxjs';

@Component({
  selector: 'app-trip-request',
  imports: [],
  templateUrl: './trip-request.component.html',
  styleUrl: './trip-request.component.css'
})
export class TripRequestComponent implements OnInit{
  tripRequestService = inject(TripRequestService)
  nominatimService = inject(NominatimService)
  geolocationService = inject(GeolocationService)

  data = inject<{index: number, stops: string[]}>(MAT_DIALOG_DATA)

  tripRequestDTO: TripRequestDTO | null = null

  routeDTO: RouteDTO | null = null
  orsFeatureCollection: ORSFeatureCollection | null = null

  suggestedLocations: Location[] = []
  selecetedIndex = 0

  stops: Location[] = []

  distance: number = 0
  duration: number = 0
  note: string = ""

  searchForm = new FormGroup({
    query: new FormControl("")
  })

  form = new FormGroup({
    carType: new FormControl('SMALL')
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
    this.searchForm.get('query')!.valueChanges.pipe(
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
    this.nominatimService.search(this.searchForm.value.query!).subscribe({
      next: response => {
        this.suggestedLocations = response
      },
      error: err => {
        console.error(err)
      }
    })
  }

  clickCard(index: number): void {
    console.log("Chosen card: ", index)
    this.selecetedIndex = index
  }

  clickCurrentLocation(): void {
    this.geolocationService.getCurrentLocation()
      .then((position) => {
        console.log(position)
        this.searchForm.setValue({
          query: `${position.coords.latitude}, ${position.coords.longitude}`
        })
        this.search()
      }).catch((err) => {
    })
  }
//---------------------------------Start of trip request creation
  createTripRequest(): void {
    const tripRequestBody : TripRequestBody = {
      carType: this.form.value.carType!,
      geojson: this.orsFeatureCollection!,
      locations: this.stops,
      note: this.note
    }
    this.tripRequestService.createCurrentActiveTripRequest(tripRequestBody).subscribe({
      next: tripRequest => {
        this.consumeTripRequestDTO(tripRequest) //TODO finish
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
}
