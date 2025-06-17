import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatTableDataSource} from "@angular/material/table";
import {MatFormField, MatInput, MatLabel} from "@angular/material/input";
import {MatSort, MatSortModule} from "@angular/material/sort";
import {DatePipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {MatIcon} from '@angular/material/icon';
import {TripHistoryDTO, TripHistoryService} from '../../../api/sep_drive';
import {MatTableModule} from '@angular/material/table';
import {MeterToKmPipe} from '../../pipes/meter-to-km.pipe';
import {SecondsToTimePipe} from '../../pipes/seconds-to-time.pipe';
import {EuroPipe} from '../../pipes/euro.pipe';
import {Router} from '@angular/router';


@Component({
  selector: 'app-trip-history',
  imports: [
    FormsModule,
    MatInput,
    MatLabel,
    MatSortModule,
    NgForOf,
    ReactiveFormsModule,
    MatFormField,
    NgClass,
    MatIcon,
    MatTableModule,
    MeterToKmPipe,
    SecondsToTimePipe,
    EuroPipe,
    DatePipe,
  ],
  templateUrl: './trip-history.component.html',
  styleUrl: './trip-history.component.css'
})
export class TripHistoryComponent implements OnInit, AfterViewInit {

  @ViewChild(MatSort) sort!: MatSort;
  tripHistories: TripHistoryDTO[] = []
  dataSource = new MatTableDataSource<TripHistoryDTO>(this.tripHistories);
  showTable: boolean = false;

  displayedColumns: string[] = ['tripId', 'endTime', 'distance', 'duration', 'price', 'driverRating', 'customerRating', 'customerName', 'customerUsername', 'driverName', 'driverUsername'];

  constructor(private tripHistoryService: TripHistoryService, private router: Router) {
  }

  ngOnInit(): void {
    // Custom filter to search in customerName and driverName only
    this.dataSource.filterPredicate = (data: TripHistoryDTO, filter: string): boolean => {
      const searchStr = filter.trim().toLowerCase();
      return data.customerName?.toLowerCase().includes(searchStr) ||
        data.driverName?.toLowerCase().includes(searchStr);
    };


    this.tripHistoryService.getTripHistory().subscribe({
      next: (response) => {
        console.log('Backend response', response);
        this.tripHistories = response;
        this.dataSource.data = response;

        this.showTable = true;
      },
      error: (err) => {
        console.error('Error', err);
        this.tripHistories = []
        this.dataSource.data = []

        this.showTable = false;
      }
    })
  }

  ngAfterViewInit() {
    this.dataSource.sort = this.sort;

    setTimeout(() => {
      this.sort.active = "tripId"
      this.sort.direction = "asc"
      this.sort.sortChange.emit({
        active: this.sort.active,
        direction: this.sort.direction
      })
    })
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }


  getStars(): number[] {
    return Array(5).fill(0).map((x, i) => i);
  }

  navigateToProfile(username: string) {
    void this.router.navigate(["/profile", username])
  }
}
