import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {StringResponse} from '../model/stringResponse';
import {DriverEntity} from '../model/driverEntity';



@Injectable({
  providedIn: 'root'
})
export class BalanceService {
  private readonly apiUrl = '/api/balance';

  constructor(private http: HttpClient) {}

  deposit(amount: number): Observable<StringResponse> {
    return this.http.post<StringResponse>(`${this.apiUrl}/deposit`, null, {
      params: { amount: amount.toString() }
    });
  }

  withdraw(amount: number): Observable<StringResponse> {
    return this.http.post<StringResponse>(`${this.apiUrl}/withdrawal`, null, {
      params: { amount: amount.toString() }
    });
  }

  transaction(amount: number, driver: DriverEntity): Observable<StringResponse> {
    return this.http.post<StringResponse>(`${this.apiUrl}/transaction`, driver, {
      params: { amount: amount.toString() }
    });
  }

  getHistory(): Observable<StringResponse> {
    return this.http.post<StringResponse>(`${this.apiUrl}/history`, {});
  }
}
