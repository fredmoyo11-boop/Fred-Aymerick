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
    const token = this.getAccessToken();
    if (!token) return null;

    try {
      const decoded = jwtDecode<{ email?: string, sub?: string }>(token);
      return decoded.email || decoded.sub || null;
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
