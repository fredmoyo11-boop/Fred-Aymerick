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
import {AngularAuthService} from "../../services/angular-auth.service";
import {TripRequestService} from '../../../api/sep_drive';
import {TripRequestDTO} from '../../../api/sep_drive';

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
  tripData: TripRequestDTO | null = null;
  activeRide : RideRequest | null = null;
  constructor(private rideRequestService: RideRequestService,
              private dialogRef: MatDialog,
              private auth:AngularAuthService,
              private tripService: TripRequestService) {
  }
  ngOnInit() {
    this.activeRide = this.rideRequestService.getRideRequest();
    const email = this.getCurrentUserEmail();
    if (!email) {
      alert('Benutzer nicht eingeloggt');
      return;
    }

    this.tripService.view(email).subscribe({
      next: (data) => this.tripData = data,
      error: err => console.error('Fehler beim Laden der Fahranfrage:', err)
    });
  }

  deleteRequest() {
    const dialogRef = this.dialogRef.open(DeleteRideDialogComponent);
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const email = this.getCurrentUserEmail();
        // if user is not logged in
        if(!email){
          return;
        }
        this.tripService.deleteRequest(email).subscribe({
          next: () => {
            this.rideRequestService.clearRideRequest();
            this.tripData = null;
            this.activeRide = null;
            console.log('Fahranfrage gelöscht');
          },
          error: err => {
            console.error('Fehler beim Löschen:', err);
            alert('Löschen fehlgeschlagen.');
          }
        });
      }
    });
  }

  getCurrentUserEmail():string | null {
    return this.auth.getEmailFromAccessToken();

  }
}
