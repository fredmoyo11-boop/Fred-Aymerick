import {inject, Injectable} from '@angular/core';
import {Client, IMessage, StompSubscription} from '@stomp/stompjs';
import {AngularAuthService} from './angular-auth.service';
import {BehaviorSubject, filter, map, Observable, of, Subject, takeUntil} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StompService {
  private angularAuthService = inject(AngularAuthService);
  private stompClient!: Client;
  private _connectionState$ = new BehaviorSubject<boolean>(false);
  private destroy$ = new Subject<void>();

  connectionState$ = this._connectionState$.asObservable()

  connect(): void {
    const token = this.angularAuthService.getAccessToken();

    this.stompClient = new Client({
      brokerURL: 'ws://localhost:8080/ws',
      webSocketFactory: () => new WebSocket(`ws://localhost:8080/ws`),
      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },
      debug: (str) => console.log(str),
      reconnectDelay: 5000
    });

    this.stompClient.onConnect = () => {
      console.log('STOMP connected');
      this._connectionState$.next(true);
    };

    this.stompClient.onDisconnect = () => {
      console.log('STOMP disconnected');
      this._connectionState$.next(false);
    };

    this.stompClient.onStompError = (frame) => console.error('STOMP error', frame);

    this.stompClient.activate();
  }

  public watchTopic<T>(topic: string): Observable<T> {
    return new Observable<IMessage>((subscriber) => {
      let subscription: StompSubscription;

      const waitForConnection = this._connectionState$
        .pipe(filter(connected => connected), takeUntil(this.destroy$))
        .subscribe(() => {
          subscription = this.stompClient.subscribe(topic, (message) => {
            subscriber.next(message);
          });
        });

      return () => {
        waitForConnection.unsubscribe();
        if (subscription) subscription.unsubscribe();
      };
    }).pipe(map(message => JSON.parse(message.body) as T));
  }

  public send<T = any>(destination: string, payload: T): void {
    if (!this.stompClient.connected) {
      console.warn('STOMP not connected, cannot send message');
      return;
    }

    this.stompClient.publish({
      destination,
      body: JSON.stringify(payload)
    });
  }

  public disconnect(): void {
    this.destroy$.next();
    void this.stompClient.deactivate()
  }

  public isConnected(): boolean {
    return this.stompClient.connected;
  }
}
