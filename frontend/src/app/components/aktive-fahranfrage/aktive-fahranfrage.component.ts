import { Component } from '@angular/core';
import {RouterLink} from '@angular/router';
import {MatSlideToggle} from '@angular/material/slide-toggle';
import {MatCard, MatCardActions, MatCardContent} from '@angular/material/card';
import {FormsModule} from '@angular/forms';
import {MatButton} from '@angular/material/button';
import {RideRequestService,RideRequest} from '../../services/ride-request.service';
import {NgIf} from '@angular/common';
import {MatDialog} from '@angular/material/dialog';
import {DeleteRideDialogComponent} from '../delete-ride-dialog/delete-ride-dialog.component';

@Component({
  selector: 'app-aktive-fahranfrage',
  imports: [
    MatButton,
    RouterLink,
    MatSlideToggle,
    MatCardContent,
    MatCard,
    MatCardActions,
    FormsModule,
    MatButton,
    NgIf
  ],
  templateUrl: './aktive-fahranfrage.component.html',
  styleUrl: './aktive-fahranfrage.component.css'
})
export class AktiveFahranfrageComponent {
  activeRide : RideRequest | null = null;
  constructor(private rideRequestService: RideRequestService,
              private dialogRef: MatDialog) {
  }
  ngOnInit() {
    this.activeRide = this.rideRequestService.getRideRequest();
  }

  deleteRequest() {
    const dialogRef = this.dialogRef.open(DeleteRideDialogComponent);
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.rideRequestService.clearRideRequest();
        this.activeRide = null;
        console.log('Fahranfrage gelöscht');
      }
    });


  }

}
