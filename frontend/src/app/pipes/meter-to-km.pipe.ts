import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'meterToKm',
  standalone: true
})
export class MeterToKmPipe implements PipeTransform {

  transform(value: number): string {
    if (value == null || isNaN(value)) return '0.00 km';

    const km = value / 1000;
    return `${km.toFixed(2)} km`;
  }
}
