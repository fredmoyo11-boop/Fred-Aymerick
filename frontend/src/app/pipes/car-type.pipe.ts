import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'carType'
})
export class CarTypePipe implements PipeTransform {

  transform(value: string): string {
    if (value === "SMALL") {
      return "Small car"
    } else if (value === "MEDIUM") {
      return "Medium car"
    } else if (value === "DELUXE") {
      return "Deluxe car"
    } else {
      return "Unknown car"
    }
  }

}
