import {inject, Injectable} from '@angular/core';
import {StompService} from './stomp.service';
import {BehaviorSubject, Subscription} from 'rxjs';
import {AngularAuthService} from './angular-auth.service';
import {Notification} from '../../api/sep_drive';

@Injectable({
  providedIn: 'root'
})
export class AngularNotificationService {
  private stompService = inject(StompService)
  private angularAuthService = inject(AngularAuthService)

  private notifications: Notification[] = []

  private _latestNotification$ = new BehaviorSubject<Notification | null>(null);
  public latestNotification$ = this._latestNotification$.asObservable();

  private _latestTripOfferNotification$ = new BehaviorSubject<Notification | null>(null);
  public latestTripOfferNotification$ = this._latestTripOfferNotification$.asObservable()

  private TRIP_OFFER_TYPE_PREFIX = "TRIP_OFFER"

  constructor() {
    console.log("Init notification service.")
    this.stompService.connectionState$.subscribe({
      next: value => {
        // user needs to be authenticated in order to connect to ws
        if (value) {
          const email = this.angularAuthService.getEmail();
          this.stompService
            .watchTopic<Notification>(`/topic/notification/${email}`)
            .subscribe(notification => {
              console.log("Received notification", notification)
              this.notifications.push(notification)
              this._latestNotification$.next(notification)

              if (notification.notificationType.startsWith(this.TRIP_OFFER_TYPE_PREFIX)) {
                this._latestTripOfferNotification$.next(notification)
              }
            })
        } else {
          console.error("Notification lost connection to stomp!")
        }
      }
    })
  }

  getSessionNotifications(): Notification[] {
    return this.notifications;
  }

  getLatestNotification(): Notification | null {
    return this._latestNotification$.value;
  }
}
