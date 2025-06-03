import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'euro'
})
export class EuroPipe implements PipeTransform {

  transform(value: number): string {
    if (value == null || isNaN(value)) return "0.00 €"

    return `${value.toFixed(2)} €`
  }

}
