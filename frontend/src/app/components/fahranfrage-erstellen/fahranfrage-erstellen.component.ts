import {Component, OnInit} from '@angular/core';
import {
  ReactiveFormsModule,
  FormGroup,
  FormControl,
  FormsModule,
  Validators,
  AbstractControl,
  ValidationErrors
} from '@angular/forms';
import {MatRadioButton, MatRadioGroup} from '@angular/material/radio';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatFormField, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import {MatOption} from '@angular/material/autocomplete';
import {NgIf} from '@angular/common';
import {debounceTime, distinctUntilChanged, Observable, tap} from 'rxjs';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatIcon} from '@angular/material/icon';
import {RouterLink} from '@angular/router';
import {Router} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {ActiveRideDialogComponent} from '../active-ride-dialog/active-ride-dialog.component';
import {TripRequestBody, TripRequestService} from '../../../api/sep_drive';
import {LocationDTO,TripRequestDTO} from '../../../api/sep_drive';
import {MatSelect, MatSelectChange} from '@angular/material/select';


@Component({
  selector: 'app-fahranfrage-erstellen',
  imports: [
    ReactiveFormsModule,
    MatRadioGroup,
    MatRadioButton,
    MatButton,
    MatInput,
    MatLabel,
    FormsModule,
    MatFormField,
    MatOption,
    MatTooltipModule,
    MatIconButton,
    MatSuffix,
    MatIcon,
    RouterLink,
    NgIf,
    MatSelect,
  ],
  templateUrl: './fahranfrage-erstellen.component.html',
  styleUrl: './fahranfrage-erstellen.component.css'
})
export class FahranfrageErstellenComponent implements OnInit {
  tripRequestForm: FormGroup = new FormGroup({
    startQuery: new FormControl("",[Validators.required]),
    endQuery: new FormControl("", [Validators.required]),
    carType: new FormControl('',[Validators.required]),
    note: new FormControl()
  },{validators: this.noSameStartEndValidator.bind(this)}
  );

  lat: number | null = null;
  lon: number | null = null;
  error: string | null = null;
  private activeRequest: TripRequestDTO |null = null;

  constructor(private router: Router,
              private dialog: MatDialog,
              private tripService: TripRequestService) {

  }

  start!:LocationDTO;
  startLocations: LocationDTO[] = []

  end!:LocationDTO;
  endLocations: LocationDTO[] = []

  onStartChange(event: MatSelectChange) {
    this.start = event.value
  }

  onEndChange(event: MatSelectChange) {
    this.end = event.value
  }

  noSameStartEndValidator(group: AbstractControl): ValidationErrors | null {
    const start = group.get('startQuery')?.value;
    const end = group.get('endQuery')?.value;
    if (start === end) {
        return {sameStartEndLocation: true}
      }
    return null;
  }


  ngOnInit(): void {
    this.tripRequestForm.get("startQuery")!.valueChanges.pipe(
      tap(value => console.log('Eingabewert:', value)),
      debounceTime(300),
      distinctUntilChanged(),
    ).subscribe({
      next: query => {
        return this.tripService.searchLocations(query).subscribe({
          next: locations => {
            console.log(locations)
            this.startLocations = locations
          },
          error: err => {
            console.error(err)
          }
        });
      },
      error: err => {
        console.error(err)}
    });

    this.tripRequestForm.get("endQuery")!.valueChanges.pipe(
      tap(value => console.log('Eingabewert:', value)),
      debounceTime(300),
      distinctUntilChanged(),
    ).subscribe({
      next: query => {
        return this.tripService.searchLocations(query).subscribe({
          next: locations => {
            console.log(locations)
            this.endLocations = locations
          },
          error: err => {
            console.error(err)
          }
        });
      },
      error: err => {
        console.error(err)}
    });

  }

  currentLocation() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition((position) => {
          this.lat = position.coords.latitude;
          this.lon = position.coords.longitude;
          console.log(this.lat);
          console.log(this.lon);
          this.tripRequestForm.get("startQuery")!.setValue(`${this.lat}, ${this.lon}`)
        },
        (err) => {
          this.error = 'Error getting location' + err.message;
        });
      // send lng and lat to backend
    } else {
      this.error = 'Geolocation is not supported by this browser.';
    }

  }

  checkActiveRide(): void {
    if (this.tripRequestForm.invalid) {
      this.tripRequestForm.markAllAsTouched();
      return;
    }

    this.tripService.getCurrentActiveTripRequest().subscribe({
      next: response => {
        console.log('Aktive Fahranfrage gefunden:', response);
        if (!response) {
          this.submitRideRequest();
          return;
        }

        // Aktive Fahrt existiert, zeige Dialog und KEIN submit
        this.activeRequest = response;
        this.dialog.open(ActiveRideDialogComponent, {
          width: '350px',
          data: { message: 'Du hast bereits eine aktive Fahranfrage. Bitte beende sie zuerst.' }
        });
      },
      error: error => {
        // Wenn keine aktive Fahrt existiert, dann erstelle neue
        if (error.status === 404) {
          this.submitRideRequest();
        } else {
          console.error('Fehler beim Prüfen der aktiven Fahrtanfrage:', error);
          alert('Ein Fehler ist aufgetreten. Bitte versuche es später erneut.');
        }
      }
    });
  }


  submitRideRequest () {
    //send to backend
    const form = this.tripRequestForm.value;
    const tripRequestBody: TripRequestBody = {
      startLocation: this.start,
      endLocation: this.end,
      carType: form.carType,
      note: form.note || ''
    };


    this.tripService.createCurrentActiveTripRequest(tripRequestBody).subscribe({
      next: (response) => {
        console.log('Fahrt erfolgreich erstellt!',response);
        alert('Fahrt wurde erfolgreich erstellt!');
        this.resetForm();
        this.router.navigate(['/aktiveFahranfrage']);
      },
      error: err => {
        console.error('Fehler beim Erstellen:', err);
        alert('Fehler beim Erstellen der Fahrt.');
      }
    });
  }


  resetForm(): void {
    this.tripRequestForm.reset();
  }
}
