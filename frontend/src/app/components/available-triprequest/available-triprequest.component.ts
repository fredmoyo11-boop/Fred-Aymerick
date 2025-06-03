import {Component, ViewChild, AfterViewInit, OnInit} from '@angular/core';
import {
  MatCell, MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef, MatHeaderRow, MatRow,
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
import {AvailableTripRequestDTO, LocationDTO, TripRequestService} from '../../../api/sep_drive';
import {debounceTime, distinctUntilChanged, tap} from 'rxjs';


const ELEMENT_DATA: AvailableTripRequestDTO[] = [
  {requestId: 2,requestTime: '12.05.2025 13:25',distanceInKm: 1000,customerUsername: 'Max',customerRating: 5,desiredCarType: 'SMALL', totalDistanceInKm: 10,preis: 20,duration: 20},{
  requestId: 1,requestTime: '12.05.2025 13:25',distanceInKm: 1000,customerUsername: 'Max',customerRating: 5,desiredCarType: 'SMALL', totalDistanceInKm: 10,preis: 20,duration: 20
  }];

@Component({
  selector: 'app-available-triprequest',
  imports: [MatSort, MatTable, FormsModule, MatFormField, MatIcon, MatIconButton, MatInput, MatLabel, MatOption, MatSelect, MatSuffix, MatTooltip, ReactiveFormsModule, MatButton, MatColumnDef, MatSortHeader, MatHeaderCell, MatCell, MatHeaderCellDef, MatCellDef, MatHeaderRow, MatRow],
  templateUrl: './available-triprequest.component.html',
  styleUrl: './available-triprequest.component.css'
})


export class AvailableTriprequestComponent implements AfterViewInit, OnInit{
  locationForm: FormGroup = new FormGroup({
    startQuery: new FormControl("", [Validators.required])
  });
  lat: number | null = null;
  lon: number | null = null;
  error: string | null = null;

  start!:LocationDTO;
  startLocations: LocationDTO[] = [];

  constructor(private tripService: TripRequestService) { }


  onStartChange(event: MatSelectChange) {
    this.start = event.value
  }
  ngOnInit(): void {
    this.locationForm.get("startQuery")!.valueChanges.pipe(
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
          error: (err: any) => {
            console.error(err)
          }
        });
      },
      error: err => {
        console.error(err)}
    });
  }

  displayedColumns: string[] = ['requestId', 'requestTime', 'distanceInKm','customerUsername','customerRating','desiredCarType','totalDistanceInKm','preis','duration'];
  dataSource = new MatTableDataSource(ELEMENT_DATA);

  @ViewChild(MatSort) sort!: MatSort;


  ngAfterViewInit() {
    this.dataSource.sort = this.sort;
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

  onSave() {
    //send adress to backend and receive table data from the backend
    const startAddress: LocationDTO = this.locationForm.value;
    this.tripService.getAvailableRequests(startAddress).subscribe({
      next: (response) => {
        console.log('got available trip request',response);
        this.dataSource.data = response;
      },
      error: (err) => {
        console.error('Fehler bei der Suche von verfügbare Fahranfragen', err);
      }
    })
  }
}



