import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { MatCard, MatCardActions, MatCardContent } from '@angular/material/card';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { RideRequestService, RideRequest } from '../../services/ride-request.service';
import { NgIf } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { DeleteRideDialogComponent } from '../delete-ride-dialog/delete-ride-dialog.component';
import { AngularAuthService } from "../../services/angular-auth.service";
import { TripRequestService } from '../../../api/sep_drive';
import { TripRequestDTO } from '../../../api/sep_drive';

@Component({
  selector: 'app-aktive-fahranfrage',
  standalone: true,
  imports: [
    MatButton,
    RouterLink,
    MatSlideToggle,
    MatCardContent,
    MatCard,
    MatCardActions,
    FormsModule,
    NgIf
  ],
  templateUrl: './aktive-fahranfrage.component.html',
  styleUrl: './aktive-fahranfrage.component.css'
})
export class AktiveFahranfrageComponent {
  tripData: TripRequestDTO | null = null;
  activeRide: RideRequest | null = null;

  constructor(
    private rideRequestService: RideRequestService,
    private dialogRef: MatDialog,
    private auth: AngularAuthService,
    private tripService: TripRequestService
  ) {}

  ngOnInit() {
    this.activeRide = this.rideRequestService.getRideRequest();
    const email = this.getCurrentUserEmail();
    if (!email) {
      console.error('Benutzer nicht eingeloggt');
      alert('Bitte melden Sie sich an, um Ihre Fahranfrage zu sehen.');
      return;
    }

    this.tripService.view(email).subscribe({
      next: (data) => this.tripData = data,
      error: (err) => {
        console.error('Fehler beim Laden der Fahranfrage:', err);
        alert('Ihre Fahranfrage konnte nicht geladen werden. Bitte versuchen Sie es später erneut.');
      }
    });
  }

  deleteRequest() {
    const dialogRef = this.dialogRef.open(DeleteRideDialogComponent);
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const email = this.getCurrentUserEmail();
        if (!email) {
          alert('Bitte melden Sie sich an, um Ihre Fahranfrage zu löschen.');
          return;
        }

        this.tripService.deleteRequest(email).subscribe({
          next: () => {
            this.rideRequestService.clearRideRequest();
            this.tripData = null;
            this.activeRide = null;
            alert('Fahranfrage wurde erfolgreich gelöscht.');
          },
          error: (err) => {
            console.error('Fehler beim Löschen:', err);
            alert('Löschen fehlgeschlagen. Bitte versuchen Sie es später erneut.');
          }
        });
      }
    });
  }

  getCurrentUserEmail(): string | null {
    const email = this.auth.getEmailFromAccessToken();
    if (!email) {
      console.error('Keine E-Mail-Adresse im Token gefunden');
    }
    return email;
  }
}
