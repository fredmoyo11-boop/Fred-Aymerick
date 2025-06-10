import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TripOfferService, TripOfferResponse } from '../../../api/sep_drive';

@Component({
  selector: 'app-angebote-tabelle-component',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './angebot.component.html',
  styleUrl: './angebot.component.css',
})
export class AngebotComponentComponent implements OnInit {
  offers: TripOfferResponse[] = [];

  private offerService = inject(TripOfferService);

  ngOnInit(): void {
    this.refresh();
  }

  acceptOffer(username: string) {
    this.offerService.acceptOffer(username).subscribe(() => this.refresh());
  }

  declineOffer(username: string) {
    this.offerService.declineOffer(username).subscribe(() => this.refresh());
  }

  refresh() {
    this.offerService.getTripOfferList().subscribe(data => {
      this.offers = data;
    });
  }

  sortBy(field: 'rating' | 'totalDriveCount' | 'driveDistance', direction: 'asc' | 'desc') {
    this.offers.sort((a, b) => {
      const aValue = a[field] ?? 0;
      const bValue = b[field] ?? 0;

      if (direction === 'asc') {
        return aValue - bValue;
      } else {
        return bValue - aValue;
      }
    });
  }
}
