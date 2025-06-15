import {Component, ViewChild, AfterViewInit, OnInit} from '@angular/core';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef,
  MatTableDataSource
} from '@angular/material/table';
import {MatSort, MatSortHeader} from '@angular/material/sort';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatFormField, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import {MatIcon} from '@angular/material/icon';
import {MatOption} from '@angular/material/core';
import {MatSelect, MatSelectChange} from '@angular/material/select';
import {MatTooltip} from '@angular/material/tooltip';
import {
  AvailableTripRequestDTO,
  TripRequestService,
  Location,
  TripOfferService,
  TripOffer
} from '../../../api/sep_drive';
import {debounceTime, distinctUntilChanged, firstValueFrom, tap} from 'rxjs';
import {CommonModule} from '@angular/common';
import {MatTable} from '@angular/material/table';
import {SecondsToTimePipe} from '../../pipes/seconds-to-time.pipe';
import {MeterToKmPipe} from '../../pipes/meter-to-km.pipe';
import {CarTypePipe} from '../../pipes/car-type.pipe';
import {EuroPipe} from '../../pipes/euro.pipe';
import {AngularNotificationService} from '../../services/angular-notification.service';
import {Router} from '@angular/router';
import {MatCard, MatCardContent, MatCardTitle} from '@angular/material/card';
import {MatDivider} from '@angular/material/divider';

@Component({
  selector: 'app-available-triprequest',
  standalone: true,
  imports: [CommonModule, MatTable, FormsModule, MatFormField, MatIcon, MatIconButton, MatInput, MatLabel, MatOption, MatSelect, MatSuffix, MatTooltip, ReactiveFormsModule, MatButton, MatSortHeader, MatSort, MatColumnDef, MatHeaderCell, MatCell, MatHeaderCellDef, MatCellDef, MatHeaderRow, MatRow, MatHeaderRowDef, MatRowDef, SecondsToTimePipe, MeterToKmPipe, CarTypePipe, EuroPipe, MatCard, MatCardTitle, MatDivider, MatCardContent],
  templateUrl: './available-triprequest.component.html',
  styleUrl: './available-triprequest.component.css'
})


export class AvailableTriprequestComponent implements OnInit, AfterViewInit {
  locationForm: FormGroup = new FormGroup({
    startQuery: new FormControl('', [Validators.required])
  });
  lat: number | null = null;
  lon: number | null = null;
  error: string | null = null;
  selectedDisplayName: string = '';

  start!: Location;
  startLocations: Location[] = [];

  //datasource initialisieren
  dataSource = new MatTableDataSource<AvailableTripRequestDTO>([]);
  @ViewChild(MatSort) sort!: MatSort;

  displayedColumns: string[] = ['requestId', 'requestTime', 'distanceInKm', 'customerUsername', 'customerRating', 'desiredCarType', 'totalDistanceInKm', 'price', 'duration', 'acceptTrip'];
  showTable = false;

  activeTripOffer: TripOffer | null = null;

  constructor(private tripRequestService: TripRequestService, private tripOfferService: TripOfferService, private angularNotificationService: AngularNotificationService, private router: Router) {
  }


  onStartChange(event: MatSelectChange) {
    this.start = event.value
  }

  ngOnInit(): void {
    this.angularNotificationService.latestNotification$.subscribe({
      next: notification => {
        if (notification) {
          if (notification.notificationType.startsWith("TRIP_OFFER")) {
            void this.getCurrentActiveTripRequest()
          }
        }
      }
    })

    void this.getCurrentActiveTripRequest()


    this.dataSource = new MatTableDataSource<AvailableTripRequestDTO>([]);

    this.locationForm.get("startQuery")?.valueChanges.pipe(
      tap(value => console.log('Eingabewert:', value)),
      debounceTime(300),
      distinctUntilChanged(),
    ).subscribe({
      next: query => {
        return this.tripRequestService.searchLocations(query).subscribe({
          next: locations => {
            console.log(locations)
            this.startLocations = locations;
          },
          error: (err: any) => {
            console.error(err)
          }
        });
      },
      error: err => {
        console.error(err)
      }
    });
  }

  async getCurrentActiveTripRequest() {
    try {
      this.activeTripOffer = await firstValueFrom(this.tripOfferService.getCurrentActiveTripOffer());
    } catch (error) {
      console.error(error)
      this.activeTripOffer = null
    }
  }

  revokeTripOffer(id: number) {
    this.tripOfferService.revokeTripOffer(id).subscribe({
      next: value => {
        console.log("Revoked trip offer.")
      }, error: err => {
        console.error(err)
      }
    })
  }


  currentLocation() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition((position) => {
          this.lat = position.coords.latitude;
          this.lon = position.coords.longitude;
          console.log(this.lat);
          console.log(this.lon);
          // stores lon and lat in var startQuery
          this.locationForm.get("startQuery")!.setValue(`${this.lat}, ${this.lon}`)
        },
        (err) => {
          this.error = 'Error getting location' + err.message;
        });
    } else {
      this.error = 'Geolocation is not supported by this browser.';
    }
  }

  ngAfterViewInit() {

    this.dataSource.sort = this.sort;


    // Benutzerdefinierte Sortierfunktion für Fahrzeugklassen
    this.dataSource.sortingDataAccessor = (item, property: string) => {
      switch (property) {
        case 'desiredCarType':
          // Definieren Sie die gewünschte Reihenfolge
          const order = {
            'SMALL': 1,
            'MITTEL': 2,
            'DELUXE': 3
          };
          return order[item.desiredCarType as keyof typeof order] || 0;

        case 'requestTime':
          // Konvertieren des Zeitstempel in ein Date-Objekt für korrekte Sortierung
          return new Date(item.requestTime).getTime();

        default:
          return (item as any)[property];
      }
    };


  }

  onSave() {
    //send adress to backend and receive table data from the backend
    if (this.start) {
      this.tripRequestService.getAvailableRequests(this.start).subscribe({
        next: (response) => {
          console.log('Backend response', response);
          this.dataSource.data = response;

          this.selectedDisplayName = this.start.displayName
          //sort-objekt zuweisen
          this.dataSource.sort = this.sort;
          this.showTable = true;

        },
        error: (err) => {
          console.error('Fehler bei der Suche von verfügbare Fahranfragen', err);
          this.showTable = false;
        }
      });
    }
  }

  getStars(rating: number): number[] {
    return Array(5).fill(0).map((x, i) => i);
  }

  acceptTrip(tripRequestId: number) {
    this.tripOfferService.createNewTripOffer(tripRequestId).subscribe({
      next: value => {
        console.log("Created new trip offer.")
        void this.getCurrentActiveTripRequest()
      },
      error: error => {
        console.error(error)
      }
    })
  }

  navigateToSimulation(): void {
    if (this.activeTripOffer && this.activeTripOffer.status === "ACCEPTED") {
      this.router.navigate(["/offer", this.activeTripOffer.id])
    }
  }

}



