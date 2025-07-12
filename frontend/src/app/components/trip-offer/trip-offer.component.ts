import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {TripOffer, TripOfferService} from '../../../api/sep_drive';
import {TripSimulationComponent} from '../trip-simulation/trip-simulation.component';
import {ChatComponent} from '../chat/chat.component';
import {AngularNotificationService} from '../../services/angular-notification.service';
import {firstValueFrom} from 'rxjs';

@Component({
  selector: 'app-trip-offer',
  imports: [
    TripSimulationComponent,
    ChatComponent
  ],
  templateUrl: './trip-offer.component.html',
  styleUrl: './trip-offer.component.css'
})
export class TripOfferComponent implements OnInit {

  tripOffer!: TripOffer

  constructor(private readonly activatedRoute: ActivatedRoute, private readonly angularNotificationService: AngularNotificationService, private readonly tripOfferService: TripOfferService) {
  }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({tripOffer}) => {
      console.log("Trip offer comp offer:", tripOffer)
      this.tripOffer = tripOffer
    })

    this.angularNotificationService.latestNotification$.subscribe({
      next: value => {
        if (value) {
          console.log("TO receive notification", value)
          void this.refreshTripOffer()
        }
      }
    })
  }

  async refreshTripOffer() {
    try {
      const tripOffer = await firstValueFrom(this.tripOfferService.getTripOffer(this.tripOffer.id))
      this.tripOffer = tripOffer
      console.log("TO Refreshed trip offer", this.tripOffer, tripOffer)
    } catch (e) {
      console.error(e)
    }
  }
}
