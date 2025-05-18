import {inject, Injectable} from '@angular/core';
import {AuthResponse, AuthService} from '../../api/sep_drive';
import {BehaviorSubject, catchError, Observable, of, switchMap, throwError} from 'rxjs';
import {jwtDecode} from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class AngularAuthService {
  authService = inject(AuthService)

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

  getEmail(): string {
    return this._email$.value || "";
  }

  getRole(): string {
    return this._role$.value || "";
  }

  logout() {
    this.authService.logout().subscribe({
      next: value => {
        console.log("Logout successful.");
        this._accessToken$.next(null);
        this._email$.next(null);
        this._role$.next(null);
        window.location.reload()
      },
      error: err => {
        console.error("Logout failed.", err);
      }
    })
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
