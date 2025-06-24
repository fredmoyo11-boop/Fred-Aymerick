import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'tripOfferStatus'
})
export class TripOfferStatusPipe implements PipeTransform {

  transform(value: string): string {
    if (value === "PENDING") {
      return "Ausstehend"
    } else if (value === "ACCEPTED") {
      return "Akzeptiert"
    } else if (value === "REJECTED") {
      return "Abgelehnt"
    } else if (value === "REVOKED") {
      return "Zurückgezogen"
    } else if (value === "COMPLETED") {
      return "Abgeschlossen"
    } else {
      return "Unbekannter Status"
    }
  }


}
