import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'isoDateShort'
})
export class IsoDateShortPipe implements PipeTransform {
  transform(value: string): string {
    if (!value) return '';

    const trimmed = value.length > 23 ? value.substring(0, 23) : value;

    const date = new Date(trimmed);
    if (isNaN(date.getTime())) return '';

    const pad = (n: number) => n.toString().padStart(2, '0');
    const year = date.getFullYear();
    const month = pad(date.getMonth() + 1);
    const day = pad(date.getDate());
    const hours = pad((date.getHours() + 2) % 24);
    const minutes = pad(date.getMinutes());

    return `${year}-${month}-${day} ${hours}:${minutes}`;
  }
}
