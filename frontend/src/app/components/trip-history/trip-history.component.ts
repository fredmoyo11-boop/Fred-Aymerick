import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { MatTableDataSource} from "@angular/material/table";
import {MatFormField, MatInput, MatLabel} from "@angular/material/input";
import {MatSort, MatSortHeader} from "@angular/material/sort";
import {CurrencyPipe, DatePipe, DecimalPipe, NgClass, NgForOf, NgIf} from "@angular/common";
import {MatIcon} from '@angular/material/icon';
import {TripHistoryDTO, TripRequestService} from '../../../api/sep_drive';
import { MatTableModule } from '@angular/material/table';



@Component({
  selector: 'app-trip-history',
  imports: [
    FormsModule,
    MatInput,
    MatLabel,
    MatSort,
    MatSortHeader,
    NgForOf,
    NgIf,
    ReactiveFormsModule,
    MatFormField,
    NgClass,
    MatIcon,
    MatTableModule,
    DatePipe,
    DecimalPipe,
    CurrencyPipe,
  ],
  templateUrl: './trip-history.component.html',
  styleUrl: './trip-history.component.css'
})
export class TripHistoryComponent implements OnInit, AfterViewInit {

  displayedColumns: string[] = ['tripId', 'endTime', 'distance', 'duration', 'price', 'driverRating', 'customerRating', 'customerName', 'customerUsername', 'driverName', 'driverUsername'];
  dataSource = new MatTableDataSource<TripHistoryDTO>([]);
  @ViewChild(MatSort) sort!: MatSort;
  showTable: boolean = false;

  constructor(private tripService: TripRequestService) {
  }

  ngOnInit(): void {
    // Custom filter to search in customerName and driverName only
    this.dataSource.filterPredicate = (data: TripHistoryDTO, filter: string): boolean => {
      const searchStr = filter.trim().toLowerCase();
      return data.customerName?.toLowerCase().includes(searchStr) ||
        data.driverName?.toLowerCase().includes(searchStr);
    };

    this.tripService.getTripHistory().subscribe({
      next: (response) => {
        console.log('Backend response', response);
        this.dataSource.data = response;

        setTimeout(() => {
          this.dataSource.sort = this.sort;
        });

        this.showTable = true;

      },
      error: (err) => {
        console.error('Error', err);
        this.showTable = false;
      }
    })
  }
  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

  }

  ngAfterViewInit() {
    if (this.dataSource && this.sort) {
      this.dataSource.sort = this.sort;
    }
  }

  getStars(rating: number): number[] {
    return Array(5).fill(0).map((x, i) => i);
  }

}
