import {Component, OnInit} from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatCard, MatCardActions, MatCardContent } from '@angular/material/card';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { NgIf } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
//import { DeleteRideDialogComponent } from '../delete-ride-dialog/delete-ride-dialog.component';
import { AngularAuthService } from "../../services/angular-auth.service";
import { TripRequestService } from '../../../api/sep_drive';
import { TripRequestDTO } from '../../../api/sep_drive';

@Component({
  selector: 'app-aktive-fahranfrage',
  standalone: true,
  imports: [
    MatButton,
    RouterLink,
    MatCardContent,
    MatCard,
    MatCardActions,
    FormsModule,
    NgIf
  ],
  templateUrl: './aktive-fahranfrage.component.html',
  styleUrl: './aktive-fahranfrage.component.css'
})
export class AktiveFahranfrageComponent implements OnInit{
  tripData: TripRequestDTO | null = null;

  constructor(
    private dialogRef: MatDialog,
    private auth: AngularAuthService,
    private tripService: TripRequestService
  ) {}

  ngOnInit() {
    this.tripService.getCurrentTripRequest().subscribe({
      next: (response) => {
        console.log('Backend-Antwort:', response); // Debug-Ausgabe
        this.tripData = response;
      },
      error: (error) => {
        console.error('Fehler beim Laden der Fahranfrage:', error);
      }
    });
  }
  deleteRequest() {
    // const dialogRef = this.dialogRef.open(DeleteRideDialogComponent);
    // dialogRef.afterClosed().subscribe(result => {
    //   if (result) {
    //     this.tripService.deleteRequest(email).subscribe({
    //       next: () => {
    //         this.tripData = null;
    //         alert('Fahranfrage wurde erfolgreich gelöscht.');
    //       },
    //       error: (err) => {
    //         console.error('Fehler beim Löschen:', err);
    //         alert('Löschen fehlgeschlagen. Bitte versuchen Sie es später erneut.');
    //       }
    //     });
    //   }
    // });
  }
}
