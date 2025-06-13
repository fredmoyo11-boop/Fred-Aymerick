import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'carType'
})
export class CarTypePipe implements PipeTransform {

  transform(value: string): string {
    if (value === "SMALL") {
      return "Kleines Auto"
    } else if (value === "MEDIUM") {
      return "Mittel großes Auto"
    } else if (value === "DELUXE") {
      return "Deluxe Auto"
    } else {
      return "Unbekanntes Auto"
    }
  }

}
