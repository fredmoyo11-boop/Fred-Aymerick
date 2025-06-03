import {Component, inject, OnInit} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {AngularAuthService} from './services/angular-auth.service';
import {StompService} from './services/stomp.service';
import {AngularNotificationService} from './services/angular-notification.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'SEP-Drive Gruppe P';

  stompService = inject(StompService)
  angularAuthService = inject(AngularAuthService)
  angularNotificationService = inject(AngularNotificationService)

  ngOnInit(): void {
    this.angularAuthService.accessToken$.subscribe(token => {
      if (token) {
        this.stompService.connect()
      }
    })

    this.angularNotificationService.latestNotification$.subscribe({
      next: notification => {
        if (notification) {
          console.log("Received the following notification : ", notification)
        }
      },
      error: err => {
        console.error(err)
      }
    })
  }

}
