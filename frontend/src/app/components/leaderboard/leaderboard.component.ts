import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import { CommonModule} from '@angular/common';
import {EuroPipe} from '../../pipes/euro.pipe';
import {MatTableDataSource, MatTableModule} from '@angular/material/table';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {MatSort, MatSortHeader} from '@angular/material/sort';
import {MeterToKmPipe} from '../../pipes/meter-to-km.pipe';
import {SecondsToTimePipe} from '../../pipes/seconds-to-time.pipe';
import {AccountService, Leaderboard} from '../../../api/sep_drive';
import {MatIcon} from '@angular/material/icon';
import {Router} from '@angular/router';

@Component({
  selector: 'app-leaderboard',
  imports: [
    EuroPipe,
    MatInput,
    MatLabel,
    MatSort,
    MatSortHeader,
    MatTableModule,
    MeterToKmPipe,
    CommonModule,
    SecondsToTimePipe,
    MatFormField,
    MatIcon
  ],
  templateUrl: './leaderboard.component.html',
  styleUrl: './leaderboard.component.css'
})
export class LeaderboardComponent implements OnInit, AfterViewInit {


  dataSource: MatTableDataSource<Leaderboard> = new MatTableDataSource<Leaderboard>();
  displayedColumns: string[] = ['driverUsername', 'driverName', 'totalDrivenDistance', 'averageRating', 'totalDriveTime', 'totalNumberOfDrivenTrip', 'totalEarnings'];

  constructor(private accountService: AccountService, private router:Router) {}

  @ViewChild(MatSort) sort!: MatSort;


  ngOnInit() {
    //Fahrername filter:
    this.dataSource.filterPredicate = (data: Leaderboard, filterStr: string): boolean =>{
      const searchStr = filterStr.trim().toLowerCase();
      return data.driverName?.toLowerCase().includes(searchStr) ;
    };

    this.accountService.getDriverLeaderboards().subscribe({
      next: (response) => {
        console.log('Backend response', response);
        this.dataSource.data = response;
        this.dataSource.sort = this.sort;
      },
      error: (err) => {
        console.error('Error', err);
        this.dataSource.data = []
      }
    })

  }
 ngAfterViewInit() {
    this.dataSource.sort = this.sort;
 }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  navigateToProfile(Username: string) {
    void this.router.navigate(["/profile", Username])
  }

  getStars(): number[] {
    return Array(5).fill(0).map((x, i) => i);
  }
}
