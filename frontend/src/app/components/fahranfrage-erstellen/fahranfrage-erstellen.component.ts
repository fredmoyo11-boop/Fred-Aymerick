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
import {catchError, debounceTime, distinctUntilChanged, map, Observable, of, switchMap, tap} from 'rxjs';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatIcon} from '@angular/material/icon';
import {RouterLink} from '@angular/router';
import {RideRequest,RideRequestService} from '../../services/ride-request.service';
import {Router} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {ActiveRideDialogComponent} from '../active-ride-dialog/active-ride-dialog.component';
import {TripRequestService} from '../../../api/sep_drive';
import {LocationDTO,TripRequestDTO} from '../../../api/sep_drive';
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
  }
  ,{validators: this.noSameStartEndValidator.bind(this)}
  );

  lat: number | null = null;
  lon: number | null = null;
  error: string | null = null;

  constructor(private rideRequestService: RideRequestService,
              private router: Router,
              private dialog: MatDialog,
              private tripService: TripRequestService,
              private auth: AngularAuthService) {

  }
  filteredStartAddressOptions!: Observable<Array<LocationDTO>>;
  filteredEndAddressOptions!:Observable<Array<LocationDTO>>;


  noSameStartEndValidator(group:AbstractControl):ValidationErrors| null{
    const start = group.get('startAddress')?.value;
    const end = group.get('endAddress')?.value;

    if (start && end && start.display_name === end.display_name){
      return {sameAddress: true};
    }
    return null;
  };
  ngOnInit(): void {
    this.filteredStartAddressOptions = this.fahranfrageForm.get("startAddress")!.valueChanges.pipe(
      tap(value => console.log('Eingabewert:', value)),
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(query => {
        const searchValue = this.extractSearchValue(query);
        if (!searchValue) return of([]);

        return this.tripService.suggestions(searchValue).pipe(
          tap(response => {
            console.log('Backend-Antwort:', response); // Debug-Logging
            // Stellen Sie sicher, dass die Antwort ein Array ist
            return Array.isArray(response) ? response : [];
          }),

          catchError(error => {
            console.error('Fehler bei der Adresssuche:', error);
            return of([]);
          })
        );
      })
    );
    this.filteredEndAddressOptions = this.fahranfrageForm.get("endAddress")!.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(query => {
        const searchValue = this.extractSearchValue(query);
        if (!searchValue) return of([]);

        return this.tripService.suggestions(searchValue).pipe(
          catchError(error => {
            console.error('Fehler bei der Adresssuche:', error);
            return of([]);
          })
        );
      })
    );

  }
  // ngOnInit(): void {
  //   this.filteredStartAddressOptions = this.fahranfrageForm.get("startAddress")!.valueChanges.pipe(
  //     debounceTime(300),
  //     distinctUntilChanged(),
  //     switchMap(query => this.tripService.suggestions(this.extractSearchValue(query)).pipe(
  //       catchError(err => {
  //         console.error('Start-Suche Fehler:', err);
  //         return of([]); // leere Liste zurückgeben, falls Fehler
  //       })
  //     ))
  //   )
  //   //   .subscribe(res => {
  //   //   console.log('Startadresse Optionen:', res);
  //   //   this.filteredStartAddressOptions = res;
  //   // });
  //   this.filteredEndAddressOptions = this.fahranfrageForm.get("endAddress")!.valueChanges.pipe(
  //     debounceTime(300),
  //     distinctUntilChanged(),
  //     switchMap(query => this.tripService.suggestions(this.extractSearchValue(query)).pipe(
  //       catchError(err => {
  //         console.error('Start-Suche Fehler:', err);
  //         return of([]); // leere Liste zurückgeben, falls Fehler
  //       })
  //     ))
  //   )
    //   .subscribe(res => {
    //   console.log('Startadresse Optionen:', res);
    //   this.filteredEndAddressOptions = res;
    // });


    //   )
    // this.filteredEndAddressOptions = this.fahranfrageForm.get("endAddress")!.valueChanges.pipe(
    //     debounceTime(300),
    //     distinctUntilChanged(),
    //     switchMap(query =>
    //       this.tripService.suggestions(this.extractSearchValue(query)).pipe(
    //         catchError(err => {
    //           console.error('Start-Suche Fehler:', err);
    //           return of([] as LocationDTO[]);
    //         })
    //       )
    //     )
    //   )
    //   .subscribe((res: LocationDTO[]) => {
    //      this.filteredEndAddressOptions = res;
    //    });

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
  // optionToString(option: any): string {
  //   if (typeof option === 'string') return option;
  //   return option?.displayName ?? option?.startLocation ?? 'Unbekannt';
  // }
  // optionToString(option: TripRequestDTO | string): string {
  //   if (typeof option === 'string') return option;
  //   return ${option?.startLocation ?? ''} → ${option?.endLocation ?? ''};
  // }

  // optionToString(option: LocationDTO | string): string {
  //   if (typeof option === 'string') return option;
  //   return `${option.display_name} (${option.lat}, ${option.lon})`;
  // }
  optionToString = (option: LocationDTO | string): string => {
    if (!option) return '';
    if (typeof option === 'string') return option;
    return option.display_name ? `${option.display_name}` : '';
  }


  userHasActiveRideRequest() {
    // check if user has an activ ride from backend
    return false;
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
    if (value.display_name) return value.display_name;
    if (value?.displayName) return value.displayName;
    return '';
  }
  submitRideRequest () {
    // local storage for frontend service
    const formData: RideRequest = {
      startAddress: this.optionToString(this.fahranfrageForm.value.startAddress),
      endAddress:this.optionToString(this.fahranfrageForm.value.endAddress),
      carType:this.fahranfrageForm.value.carType,
      status: 'ACTIVE'  // Standardstatus: aktiv
    };
    this.rideRequestService.setRideRequest(formData);

    //send to backend
    const form = this.fahranfrageForm.value;
    const tripRequest: TripRequestDTO = {
      email: this.auth.getEmailFromAccessToken() || 'user@example.com', // dynamisch aus Auth holen
      startLocation: {
        display_name: form.startAddress.displayName,
        lat: form.startAddress.latitude,
        lon: form.startAddress.longitude
      },
      endLocation: {
        display_name: form.endAddress.displayName,
        lat: form.endAddress.latitude,
        lon: form.endAddress.longitude
      },
      carType: form.carType,
      note: form.note || '',
      status: 'ACTIVE'
    };


    this.tripService.create(tripRequest).subscribe({
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
    // nut zum debugging
    console.log('Fahranfrage erstellt:', formData )
  }


  resetForm(): void {
    this.fahranfrageForm.reset();
  }



}
