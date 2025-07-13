import {
  Component,
  OnInit,
  ViewChild,
  inject,
  Input,
  OnChanges,
  SimpleChanges,
  Output,
  EventEmitter, AfterViewInit
} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';
import {CommonModule} from '@angular/common';
import {TripOffer, TripOfferService} from '../../../api/sep_drive';
import {MatTableModule} from '@angular/material/table';
import {MatSortModule} from '@angular/material/sort';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MeterToKmPipe} from '../../pipes/meter-to-km.pipe';
import {AngularNotificationService} from '../../services/angular-notification.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-trip-offers',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatSortModule,
    MatButtonModule,
    MatIconModule,
    MeterToKmPipe
  ],
  templateUrl: './trip-offers.component.html',
  styleUrls: ['./trip-offers.component.css'],
})
export class TripOffersComponent implements OnInit, AfterViewInit {
  private readonly tripOfferService = inject(TripOfferService);
  private readonly angularNotificationService = inject(AngularNotificationService)
  private readonly router = inject(Router)

  @ViewChild(MatSort) sort!: MatSort;
  tripOffers: TripOffer[] = []
  dataSource = new MatTableDataSource<TripOffer>();
  displayedColumns: string[] = ['firstName', 'lastName', 'username', 'rating', 'totalDriveCount', 'driveDistance', 'actions'];

  @Output() offerAccepted = new EventEmitter<void>

  ngOnInit(): void {
    this.angularNotificationService.latestNotification$.subscribe({
      next: notification => {
        if (notification) {
          if (notification.notificationType.startsWith("TRIP_OFFER")) {
            this.refresh()
          }
        }
      }
    })
    this.refresh();
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort

    this.dataSource.sortingDataAccessor = (item, property) => {
      switch (property) {
        case 'rating':
          return item.driverStatistics?.averageRating;
        case 'totalDriveCount':
          return item.driverStatistics?.totalTrips;
        case 'driveDistance':
          return item.driverStatistics?.totalDistance;
        case 'firstName':
          return item.driver?.firstName;
        case 'lastName':
          return item.driver?.lastName;
        case 'username':
          return item.driver?.username;
        default:
          return (item as any)[property];
      }
    };

    setTimeout(() => {
      this.sort.active = "rating"
      this.sort.direction = "asc"
      this.sort.sortChange.emit({
        active: this.sort.active,
        direction: this.sort.direction
      })
    })
  }

  acceptOffer(tripOfferId: number) {
    this.tripOfferService.acceptTripOffer(tripOfferId).subscribe(() => this.refresh())
    this.offerAccepted.emit()
  }

  declineOffer(tripOfferId: number) {
    this.tripOfferService.rejectTripOffer(tripOfferId).subscribe(() => this.refresh())
  }

  navigateToTripOffer(id: number) {
    this.router.navigate(["/offer", id])
  }

  refresh() {
    this.tripOfferService.getCurrentTripOffers().subscribe({
      next: value => {
        this.tripOffers = value
        this.dataSource.data = value
      }, error: err => {
        console.error(err)
        this.tripOffers = []
        this.dataSource.data = []
      }
    });
  }
}
