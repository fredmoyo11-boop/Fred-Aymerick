import {inject, Injectable} from '@angular/core';
import {AuthResponse, AuthService} from '../../api/sep_drive';
import {BehaviorSubject, catchError, Observable, of, switchMap, throwError} from 'rxjs';
import {jwtDecode} from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class AngularAuthService {
  private authService = inject(AuthService)

  private _email$ = new BehaviorSubject<string | null>(null)
  private _role$ = new BehaviorSubject<string | null>(null)
  private _accessToken$ = new BehaviorSubject<string | null>(null)

  email$ = this._email$.asObservable()
  role$ = this._role$.asObservable()
  accessToken$ = this._accessToken$.asObservable()


  consumeAuthResponse(authResponse: AuthResponse) {
    this._accessToken$.next(authResponse.accessToken);
    this.loadFromToken();
  }

  getAccessToken(): string {
    return this._accessToken$.value || '';
  }
  // to get email of current user
  getEmailFromAccessToken(): string | null {
    if (!this.accessToken) return null;

    try {
      const payloadPart = this.accessToken.split('.')[1];
      const decodedPayload = JSON.parse(atob(payloadPart));

      // Typisch: E-Mail im Feld "sub" oder "email"
      return decodedPayload.email || decodedPayload.sub || null;
    } catch (e) {
      console.error('Token-Parsing fehlgeschlagen', e);
      return null;
    }
  }

  private loadFromToken = () => {
    if (!this._accessToken$) return;

    try {
      const decoded = jwtDecode<{ sub: string, role: string }>(this.getAccessToken());
      this._email$.next(decoded.sub || null);
      this._role$.next(decoded.role || null);
    } catch (e) {
      console.error("Invalid token", e);
      this._email$.next(null);
      this._role$.next(null);
    }
  }
}
