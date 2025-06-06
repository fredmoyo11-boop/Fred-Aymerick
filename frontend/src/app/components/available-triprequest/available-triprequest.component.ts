import {Component, ViewChild, AfterViewInit, OnInit, ChangeDetectorRef} from '@angular/core';
import {
  MatCell, MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef,
  MatTable,
  MatTableDataSource
} from '@angular/material/table';
import {MatSort, MatSortHeader, Sort} from '@angular/material/sort';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatFormField, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import {MatIcon} from '@angular/material/icon';
import {MatOption} from '@angular/material/core';
import {MatSelect, MatSelectChange} from '@angular/material/select';
import {MatTooltip} from '@angular/material/tooltip';
import {AvailableTripRequestDTO, TripRequestService,Location} from '../../../api/sep_drive';
import {debounceTime, distinctUntilChanged, tap} from 'rxjs';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-available-triprequest',
  standalone: true,
  imports: [CommonModule, MatTable, FormsModule, MatFormField, MatIcon, MatIconButton, MatInput, MatLabel, MatOption, MatSelect, MatSuffix, MatTooltip, ReactiveFormsModule, MatButton, MatColumnDef, MatSortHeader, MatHeaderCell, MatCell, MatHeaderCellDef, MatCellDef, MatHeaderRow, MatRow, MatHeaderRowDef, MatRowDef, MatSort],
  templateUrl: './available-triprequest.component.html',
  styleUrl: './available-triprequest.component.css'
})


export class AvailableTriprequestComponent implements OnInit,AfterViewInit{
  locationForm: FormGroup = new FormGroup({
    startQuery: new FormControl('', [Validators.required])
  });
  lat: number | null = null;
  lon: number | null = null;
  error: string | null = null;

  start!:Location;
  startLocations: Location[] = [];

  dataSource = new MatTableDataSource<AvailableTripRequestDTO>([]);
  @ViewChild(MatSort) sort!: MatSort;

  displayedColumns: string[] = ['requestId', 'requestTime', 'distanceInKm','customerUsername','customerRating','desiredCarType','totalDistanceInKm','price','duration','acceptTrip'];
  showTable = false;


  constructor(
    private tripService: TripRequestService
  , private cdr: ChangeDetectorRef) {}


  onStartChange(event: MatSelectChange) {
    this.start = event.value
  }
  ngOnInit(): void {
    //datasource initialisieren
    this.dataSource = new MatTableDataSource<AvailableTripRequestDTO>([]);

    this.locationForm.get("startQuery")?.valueChanges.pipe(
        tap(value => console.log('Eingabewert:', value)),
        debounceTime(300),
        distinctUntilChanged(),
      ).subscribe({
        next: query => {
          return this.tripService.searchLocations(query).subscribe({
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

  currentLocation() {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition((position) => {
          this.lat = position.coords.latitude;
          this.lon = position.coords.longitude;
          console.log(this.lat);
          console.log(this.lon);
          // // stores lon and lat in var startQuery
          this.locationForm.get("startQuery")!.setValue(`${this.lat}, ${this.lon}`)
        },
        (err) => {
          this.error = 'Error getting location' + err.message;
        });
    } else {
      this.error = 'Geolocation is not supported by this browser.';
    }
  }
  // initialise datasource


  ngAfterViewInit() {
      if (this.dataSource && this.sort) {
        this.dataSource.sort = this.sort;
        this.cdr.detectChanges();
      }
  }

  onSave() {
    //send adress to backend and receive table data from the backend
    if (this.start) {
      this.tripService.getAvailableRequests(this.start).subscribe({
        next: (response) => {
          console.log('Backend response', response);
          this.dataSource.data = response;
          this.showTable = true;

          setTimeout(() => {
            if(this.sort){
              this.dataSource.sort = this.sort;
            }
          })
          },
        error: (err) => {
          console.error('Fehler bei der Suche von verfügbare Fahranfragen', err);
          this.showTable = false;
        }
      });
    }
  }

  acceptTrip() {


  }

  getStars(rating: number): number[] {
    return Array(5).fill(0).map((x, i) => i); // [0,1,2,3,4]
  }
}



