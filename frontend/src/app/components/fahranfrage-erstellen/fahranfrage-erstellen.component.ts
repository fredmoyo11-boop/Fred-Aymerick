import {Component, OnInit} from '@angular/core';
import {
  ReactiveFormsModule,
  FormGroup,
  FormControl,
  FormsModule,
  Validators,
} from '@angular/forms';
import {MatRadioButton, MatRadioGroup} from '@angular/material/radio';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatFormField, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import {MatOption} from '@angular/material/autocomplete';
import {NgIf} from '@angular/common';
import {debounceTime, distinctUntilChanged, tap} from 'rxjs/operators';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatIcon} from '@angular/material/icon';
import {RouterLink} from '@angular/router';
import {Router} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {ActiveRideDialogComponent} from '../active-ride-dialog/active-ride-dialog.component';
import {ORSFeatureCollection, TripRequestBody, TripRequestService} from '../../../api/sep_drive';
import {TripRequestDTO, Location} from '../../../api/sep_drive';
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
  styleUrls: ['./fahranfrage-erstellen.component.css']
})
export class FahranfrageErstellenComponent implements OnInit {
  tripRequestForm: FormGroup = new FormGroup({
    startQuery: new FormControl('', [Validators.required]),
    endQuery: new FormControl('', [Validators.required]),
    carType: new FormControl('', [Validators.required]),
    note: new FormControl('')
  });

  lat: number | null = null;
  lon: number | null = null;
  error: string | null = null;
  private activeRequest: TripRequestDTO | null = null;

  start!: Location;
  startLocations: Location[] = [];

  end!: Location;
  endLocations: Location[] = [];

  constructor(private router: Router,
              private dialog: MatDialog,
              private tripService: TripRequestService) {}

  onStartChange(event: MatSelectChange) {
    this.start = event.value;
  }

  onEndChange(event: MatSelectChange) {
    this.end = event.value;
  }

  ngOnInit(): void {
    this.tripRequestForm.get('startQuery')!.valueChanges.pipe(
      tap(value => console.log('Eingabewert:', value)),
      debounceTime(300),
      distinctUntilChanged(),
    ).subscribe(query => {
      this.tripService.searchLocations(query).subscribe({
        next: locations => this.startLocations = locations,
        error: err => console.error(err)
      });
    });

    this.tripRequestForm.get('endQuery')!.valueChanges.pipe(
      tap(value => console.log('Eingabewert:', value)),
      debounceTime(300),
      distinctUntilChanged(),
    ).subscribe(query => {
      this.tripService.searchLocations(query).subscribe({
        next: locations => this.endLocations = locations,
        error: err => console.error(err)
      });
    });
  }

  currentLocation() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition((position) => {
          this.lat = position.coords.latitude;
          this.lon = position.coords.longitude;
          this.tripRequestForm.get('startQuery')!.setValue(`${this.lat}, ${this.lon}`);
        },
        (err) => {
          this.error = 'Error getting location: ' + err.message;
        });
    } else {
      this.error = 'Geolocation is not supported by this browser.';
    }
  }

  checkActiveRide(): void {
    if (!this.start || !this.end) {
      alert('Bitte Start- und Zielort auswählen.');
      return;
    }

    const sameLocation =
      this.start.latitude === this.end.latitude &&
      this.start.longitude === this.end.longitude &&
      this.start.displayName === this.end.displayName;

    if (sameLocation) {
      this.tripRequestForm.setErrors({sameStartEndLocation: true});
      alert('Start- und Zieladresse dürfen nicht gleich sein.');
      return;
    } else {
      this.tripRequestForm.setErrors(null);
    }

    if (this.tripRequestForm.invalid) {
      this.tripRequestForm.markAllAsTouched();
      return;
    }

    this.tripService.getCurrentActiveTripRequest().subscribe({
      next: response => {
        if (!response) {
          this.submitRideRequest();
          return;
        }
        this.activeRequest = response;
        this.dialog.open(ActiveRideDialogComponent, {
          width: '350px',
          data: {message: 'Du hast bereits eine aktive Fahranfrage. Bitte beende sie zuerst.'}
        });
      },
      error: error => {
        if (error.status === 404) {
          this.submitRideRequest();
        } else {
          console.error('Fehler beim Prüfen der aktiven Fahrtanfrage:', error);
          alert('Ein Fehler ist aufgetreten. Bitte versuche es später erneut.');
        }
      }
    });
  }

  submitRideRequest() {
    const form = this.tripRequestForm.value;

    const locations: Location[] = [this.start, this.end];

    // Erstellen eines leeren ORSFeatureCollection Objekts
    const geojson: ORSFeatureCollection = {
      type: "FeatureCollection",
      features: [],
    };


    const tripRequestBody: TripRequestBody = {
      locations: locations,
      geojson: geojson,
      carType: form.carType,
      note: form.note || ''
    };

    this.tripService.createCurrentActiveTripRequest(tripRequestBody).subscribe({
      next: (response) => {
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
    // this.startLocations = [];
    // this.endLocations = [];
    // this.start = undefined!;
    // this.end = undefined!;
  }
}
