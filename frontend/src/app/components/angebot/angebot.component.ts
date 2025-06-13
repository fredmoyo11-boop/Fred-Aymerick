import { Component, OnInit, ViewChild, inject } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { CommonModule } from '@angular/common';
import { TripOfferService, TripOfferResponse } from '../../../api/sep_drive';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-angebote-tabelle-component',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './angebot.component.html',
  styleUrls: ['./angebot.component.css'],
})
export class AngebotComponentComponent implements OnInit {
  displayedColumns: string[] = ['firstName', 'lastName', 'username', 'rating', 'totalDriveCount', 'driveDistance', 'actions'];
  dataSource = new MatTableDataSource<TripOfferResponse>();

  private offerService = inject(TripOfferService);
  @ViewChild(MatSort) sort!: MatSort;

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
