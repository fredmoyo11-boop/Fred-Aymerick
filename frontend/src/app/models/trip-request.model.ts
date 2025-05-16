export interface LocationDTO{
  lat: number;
  lon: number;
  displayName: string;
}
export interface TripRequestDTO {
  email: string;
  startLocation: LocationDTO;
  endLocation: LocationDTO;
  carType: string;
  note?: string;

}
