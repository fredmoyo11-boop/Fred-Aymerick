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

  setAccessToken(authToken: string) {
    this.accessToken = authToken;
  }

  clearAccessToken(): void {
    this.accessToken = "";
  }
}
