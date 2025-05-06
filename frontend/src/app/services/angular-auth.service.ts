import {Injectable} from '@angular/core';
import {AuthResponse} from '../../api/sep_drive';

@Injectable({
  providedIn: 'root'
})
export class AngularAuthService {
  private accessToken: string = "";

  consumeAuthResponse(authResponse: AuthResponse) {
    this.accessToken = authResponse.accessToken;
  }

  getAccessToken(): string {
    return this.accessToken;
  }

  setAccessToken(authToken: string) {
    this.accessToken = authToken;
  }

  clearAccessToken(): void {
    this.accessToken = "";
  }
}
