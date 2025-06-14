import {Component, OnInit} from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';
import {AngularNotificationService} from '../../services/angular-notification.service';

@Component({
  selector: 'app-notification',
  template: '',
  standalone: true
})
export class NotificationComponent implements OnInit {
  constructor(
    private angularNotificationService: AngularNotificationService,
    private snackBar: MatSnackBar
  ) {
  }

  ngOnInit(): void {
    this.angularNotificationService.latestNotification$.subscribe({
      next: value => {
        if (value) {
          this.snackBar.open(value.message, "OK")
        }
      }
    })
  }
}
