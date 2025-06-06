import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {TripOffer} from '../../../api/sep_drive';
import {TripSimulationComponent} from '../trip-simulation/trip-simulation.component';

@Component({
  selector: 'app-trip-offer',
  imports: [
    TripSimulationComponent
  ],
  templateUrl: './trip-offer.component.html',
  styleUrl: './trip-offer.component.css'
})
export class TripOfferComponent implements OnInit {

  tripOffer!: TripOffer

  constructor(private activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({tripOffer}) => {
      console.log("Trip offer comp offer:", tripOffer)
      this.tripOffer = tripOffer
    })
  }
}
