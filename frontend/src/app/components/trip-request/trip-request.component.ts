import {Component, inject, OnInit} from '@angular/core';

import {
  Location,
  ORSFeatureCollection,
  RouteDTO,
  RouteService,
  TripRequestBody,
  TripRequestDTO,
  TripRequestService
} from '../../../api/sep_drive';
import {FormControl, FormGroup} from '@angular/forms';

@Component({
  selector: 'app-trip-request',
  imports: [],
  templateUrl: './trip-request.component.html',
  styleUrl: './trip-request.component.css'
})
export class TripRequestComponent implements OnInit{
  tripRequestService = inject(TripRequestService)
  routeService = inject(RouteService)

  tripRequestDTO: TripRequestDTO | null = null

  routeDTO: RouteDTO | null = null
  orsFeatureCollection: ORSFeatureCollection | null = null

  stops: Location[] = []

  distance: number = 0
  duration: number = 0
  note: string = ""

  form = new FormGroup({
    carType: new FormControl('SMALL')
  })



  ngOnInit(): void {

  }


  createTripRequest(): void {
    const tripRequestBody : TripRequestBody = {
      carType: this.form.value.carType!,
      geojson: this.orsFeatureCollection!,
      locations: this.stops,
      note: this.note
    }
    this.tripRequestService.createCurrentActiveTripRequest(tripRequestBody).subscribe({
      next: tripRequest => {

      }
    })
  }
}
