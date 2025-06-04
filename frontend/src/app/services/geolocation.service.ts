import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GeolocationService {

  getCurrentLocation(): Promise<GeolocationPosition> {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        reject("Geolocation is not supported by your browser.");
      }
      navigator.geolocation.getCurrentPosition(
        (position) => resolve(position),
        (error) => reject(error),
      )
    })
  }
}
