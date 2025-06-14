import {Component, OnInit, ViewChild, inject, Input, OnChanges, SimpleChanges} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';
import {CommonModule} from '@angular/common';
import {TripOffer, TripOfferService} from '../../../api/sep_drive';
import {MatTableModule} from '@angular/material/table';
import {MatSortModule} from '@angular/material/sort';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MeterToKmPipe} from '../../pipes/meter-to-km.pipe';
import {AngularNotificationService} from '../../services/angular-notification.service';

@Component({
  selector: 'app-trip-offers',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule,
    MeterToKmPipe
  ],
  templateUrl: './trip-offers.component.html',
  styleUrls: ['./trip-offers.component.css'],
})
export class TripOffersComponent implements OnInit {
  displayedColumns: string[] = ['firstName', 'lastName', 'username', 'rating', 'totalDriveCount', 'driveDistance', 'actions'];
  dataSource = new MatTableDataSource<TripOffer>();

  private tripOfferService = inject(TripOfferService);
  private angularNotificationService = inject(AngularNotificationService)
  @ViewChild(MatSort) sort!: MatSort;

  ngOnInit(): void {
    this.angularNotificationService.latestNotification$.subscribe({
      next: notification => {
        if (notification) {
          if (notification.notificationType.startsWith("TRIP_OFFER")) {
            this.refresh()
          }
        }
      }
    })

    this.refresh();
  }


  acceptOffer(tripOfferId: number) {
    this.tripOfferService.acceptTripOffer(tripOfferId).subscribe(() => this.refresh())
  }

  declineOffer(tripOfferId: number) {
    this.tripOfferService.rejectTripOffer(tripOfferId).subscribe(() => this.refresh())
  }

  refresh() {
    this.tripOfferService.getCurrentTripOffers().subscribe(data => {
      this.dataSource.data = data;
      this.dataSource.sort = this.sort;
    });
  }
}

/*
variabeleln :
hasActiveOffer = false;



 Im ngOnInit():
this.checkActiveOffer();


 Methoden:
checkActiveOffer() {
  this.tripOfferService.hasActiveOffer().subscribe(res => {
    this.hasActiveOffer = res.value === 'HAS_ACTIVE_OFFER';
  });
}
createOffer(tripRequestId: number) {
  this.tripOfferService.createNewTripOffer(tripRequestId).subscribe(() => {
    this.checkActiveOffer(); // Aktualisieren
  });
}

withdrawOffer() {
  this.tripOfferService.withdrawOffer().subscribe(() => {
    this.checkActiveOffer();
    this.loadAvailableRequests();
  });
}



Im HTML:
<div *ngIf="hasActiveOffer">
  <p>Du hast bereits ein aktives Angebot.</p>
  <button (click)="withdrawOffer()">Angebot zurückziehen</button>
</div>

<div *ngIf="!hasActiveOffer">
  <table>
    <tr *ngFor="let request of availableRequests">
      <td>{{ request.startOrt }} → {{ request.zielOrt }}</td>
      <td><button (click)="createOffer(request.id)">Angebot abgeben</button></td>
    </tr>
  </table>
</div>





*/
