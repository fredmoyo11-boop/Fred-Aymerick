import { Injectable } from '@angular/core';

export interface RideRequest {
  startAddress : string
  endAddress: string
  carType: string
  status:boolean

}

@Injectable({
  providedIn: 'root'
})
export class RideRequestService {
  private _rideRequest: RideRequest | null = null;


  constructor() { }

  //saves the request
  setRideRequest(data: RideRequest):void{
    this._rideRequest = data;
  }
  //returns saved request
  getRideRequest(): RideRequest | null {
    return this._rideRequest;
  }
  //deletes  request
  clearRideRequest(): void {
    this._rideRequest = null;
  }

}
