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
import {MatAutocomplete, MatAutocompleteTrigger, MatOption} from '@angular/material/autocomplete';
import {AsyncPipe, NgIf} from '@angular/common';
import {debounceTime, distinctUntilChanged, map, Observable, switchMap} from 'rxjs';
import {MatTooltipModule} from '@angular/material/tooltip';
import {Address} from '../../models/address';
import {MatIcon} from '@angular/material/icon';
import {RouterLink} from '@angular/router';
import {RideRequest,RideRequestService} from '../../services/ride-request.service';
import {Router} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {ActiveRideDialogComponent} from '../active-ride-dialog/active-ride-dialog.component';
import {TripRequestService} from '../../services/trip-request.service';
import {LocationDTO,TripRequestDTO} from '../../models/trip-request.model';
import {AngularAuthService} from '../../services/angular-auth.service';


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
    MatAutocompleteTrigger,
    MatAutocomplete,
    MatOption,
    AsyncPipe,
    MatTooltipModule,
    MatIconButton,
    MatSuffix,
    MatIcon,
    RouterLink,
    NgIf
  ],
  templateUrl: './fahranfrage-erstellen.component.html',
  styleUrl: './fahranfrage-erstellen.component.css'
})
export class FahranfrageErstellenComponent implements OnInit {
  fahranfrageForm: FormGroup = new FormGroup({
    startAddress: new FormControl('', [Validators.required]),
    endAddress: new FormControl('',[Validators.required]),
    carType: new FormControl('',[Validators.required]),
    note: new FormControl()
  },{validators: this.noSameStartEndValidator.bind(this)});

  lat: number | null = null;
  lon: number | null = null;
  error: string | null = null;

  constructor(private rideRequestService: RideRequestService,
              private router: Router,
              private dialog: MatDialog,
              private tripService: TripRequestService,
              private auth: AngularAuthService) {

  }
  filteredStartAddressOptions!: Observable<LocationDTO[]>;
  filteredEndAddressOptions!: Observable<LocationDTO[]>;
  // filteredStartAddressOptions: Observable<Address[]> | undefined;
  // filteredEndAddressOptions: Observable<Address[]> | undefined;
  // options: Address[] = [
  //   {
  //     strasse: 'Alexanderplatz',
  //     hausnummer: '1',
  //     plz: '10178',
  //     stadt: 'Berlin'
  //   },
  //   {
  //     strasse: 'Berliner Straße',
  //     hausnummer: '85',
  //     plz: '13189',
  //     stadt: 'Berlin'
  //   }
  // ];
  noSameStartEndValidator(group:AbstractControl):ValidationErrors| null{
    const start = group.get('startAddress')?.value;
    const end = group.get('endAddress')?.value;

    if (start && end && start.displayName=== end.displayName){
      return {sameAddress: true};
    }
    return null;
  };

  ngOnInit() {

    this.filteredStartAddressOptions = this.fahranfrageForm.controls['startAddress'].valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((value) => this.tripService.searchLocation(typeof value === 'string' ? value : value?.displayName || ''))
    );

    this.filteredEndAddressOptions = this.fahranfrageForm.controls['endAddress'].valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap((value) => this.tripService.searchLocation(typeof value === 'string' ? value : value?.displayName || ''))
    );

    // this.filteredStartAddressOptions = this.fahranfrageForm.get('startAddress')?.valueChanges.pipe(
    //   map(value => this._filter(value || '')),
    // );
    // this.filteredEndAddressOptions = this.fahranfrageForm.get('endAddress')?.valueChanges.pipe(
    //   map(value => this._filter(value || '')),
    // );

  }

  // private _filter(value: string): Address[] {
  //   const filterValue = value.toLowerCase();
  //   return this.options.filter(option => (option.strasse + option.hausnummer + option.plz + option.stadt).toLowerCase().includes(filterValue));
  // }

  currentLocation() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition((position) => {
          this.lat = position.coords.latitude;
          this.lon = position.coords.longitude;
          console.log(this.lat);
          console.log(this.lon);
          this.fahranfrageForm.get("startAddress")?.setValue(`${this.lat}, ${this.lon}`)
        },
        (err) => {
          this.error = 'Error getting location' + err.message;
        });
      // send lng and lat to backend
    } else {
      this.error = 'Geolocation is not supported by this browser.';
    }
  }
  optionToString(option: any): string {
    return option?.displayName || option || '';
  }

  // optionToString(option: Address): string {
  //   return  option.strasse && option.hausnummer && option.plz && option.stadt ? `${option.strasse} ${option.hausnummer}, ${option.plz} ${option.stadt}` : '';
  // }

  // swapLocations() {
  //   const temp = this.startAddress;
  //   this.startAddress = this.endAddress;
  //   this.endAddress = temp;
  // }

  userHasActiveRideRequest() {
    // check if user has an activ ride from backend
    return true;
  }
  checkActiveRide() {
    if (this.fahranfrageForm.invalid){
      this.fahranfrageForm.markAllAsTouched();
      return;
    }

    if (this.userHasActiveRideRequest()){
      this.dialog.open(ActiveRideDialogComponent,{
        width: '350px',
        data:{message: 'Du hast bereits eine aktive Fahranfrage'}
      });
    }else {
      this.submitRideRequest();
    }
  }
//to extract string given in inputbox
  private extractSearchValue(value: any): string {
    if (typeof value === 'string') return value;
    if (value?.displayName) return value.displayName;
    return '';
  }
  submitRideRequest () {
    // local storage for frontend service
    const formData: RideRequest = {
      startAddress: this.optionToString(this.fahranfrageForm.value.startAddress),
      endAddress:this.optionToString(this.fahranfrageForm.value.endAddress),
      carType:this.fahranfrageForm.value.carType,
      status: true  // Standardstatus: aktiv
    };
    this.rideRequestService.setRideRequest(formData);

    //send to backend
    const form = this.fahranfrageForm.value;
    const tripRequest: TripRequestDTO = {
      email: this.auth.getEmailFromAccessToken() || 'user@example.com', // dynamisch aus Auth holen
      startLocation: form.startAddress,
      endLocation: form.endAddress,
      carType: form.carType,
      note: ''
    };

    this.tripService.createTripRequest(tripRequest).subscribe({
      next: () => {
        console.log('Fahrt erfolgreich erstellt!');
        alert('Fahrt wurde erfolgreich erstellt!');
        this.resetForm();
        this.router.navigate(['/aktiveFahranfrage']);
      },
      error: err => {
        console.error('Fehler beim Erstellen:', err);
        alert('Fehler beim Erstellen der Fahrt.');
      }
    });


    // nut zum debugging
    console.log('Fahranfrage erstellt:', formData )
    console.log((<Address>this.fahranfrageForm.get('startAddress')?.value));
  }


  resetForm(): void {
    this.fahranfrageForm.reset();
  }



}
