import { Injectable } from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient,HttpParams} from '@angular/common/http';
import {TripRequestDTO} from '../models/trip-request.model';

@Injectable({
  providedIn: 'root'
})
export class TripRequestService {
  //nd point of request and responses
  private apiurl = 'http://localhost:8080/map';

  constructor(private http: HttpClient) { }

  searchLocation(search:string):Observable<any>{
    const params = new HttpParams().set('search',search);
    return this.http.post(`${this.apiurl}/search`, null, {params});
  }

  createTripRequest(tripData:TripRequestDTO):Observable<void>{
    return this.http.post<void>(`${this.apiurl}/request/create`,tripData)
  }
  viewTripRequest(email: string): Observable<TripRequestDTO> {
    const params = new HttpParams().set('email', email);
    return this.http.post<TripRequestDTO>(`${this.apiurl}/request/view`, null, { params });
  }
  deleteTripRequest(email: string):Observable<void>{
    const params = new HttpParams().set('email',email);
    return this.http.delete<void>(`${this.apiurl}/request/view/delete`,{params});
  }
}
