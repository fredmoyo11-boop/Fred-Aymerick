import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Notification, NotificationService } from '../../../api/sep_drive'; // Import passt!

@Component({
  selector: 'app-notification-banner',
  template: `
    <div *ngIf="notification">
      <div class="message">
        {{ notification.message }}
      </div>
      <button class="view-button" (click)="viewOffers()">Angebote ansehen</button>
    </div>
  `
})
export class NotificationComponent implements OnInit {
  notification: Notification | null = null;

  constructor(
    private notificationService: NotificationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.notificationService.getCurrentNotifications().subscribe((notifications: Notification[]) => {

      this.notification = notifications.length > 0 ? notifications[0] : null;
    });
  }

  viewOffers(): void {
    if (this.notification?.id) {
      this.router.navigate(['/angebote', this.notification.id]);
    } else {
      console.warn('Kein Angebot verknüpft.');
    }
  }
}
