import {Component, Input, OnInit} from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-routing-machine';
// import { HttpClient } from '@angular/common/http';



@Component({
  selector: 'app-map',
  imports: [],
  templateUrl: './map.component.html',
  styleUrl: './map.component.css'
})
export class MapComponent implements OnInit {
  // constructor(private http: HttpClient) {}
  /*
  ngOnInit(): void {
  this.getRouteDataFromBackend();
}

private getRouteDataFromBackend(): void {
  this.http.get<any>('https://dein-backend.de/api/route')
    .subscribe(data => {
      this.startLat = data.startLat;
      this.startLng = data.startLng;
      this.endLat = data.endLat;
      this.endLng = data.endLng;
      this.initMap();
    });
} */



  map: any;
  @Input() startLat!: number;
  @Input() startLng!: number;
  @Input() endLat!: number;
  @Input() endLng!: number;



  ngOnInit(): void {
    this.initMap();
  }




  private initMap(): void {
    this.map =  L.map('map').setView([51.505, -0.09], 13);
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(this.map);

    var greenIcon = L.icon({
      iconUrl: 'https://leafletjs.com/examples/custom-icons/leaf-green.png',
      shadowUrl: 'https://leafletjs.com/examples/custom-icons/leaf-shadow.png',
      iconSize: [38, 95],
      iconAnchor: [22, 94],
      popupAnchor: [-3, -76],
      shadowSize: [50, 64],
      shadowAnchor: [4, 62]
    });

    var redIcon = L.icon({
      iconUrl: 'https://leafletjs.com/examples/custom-icons/leaf-red.png',
      shadowUrl: 'https://leafletjs.com/examples/custom-icons/leaf-shadow.png',
      iconSize: [38, 95],
      iconAnchor: [22, 94],
      popupAnchor: [-3, -76],
      shadowSize: [50, 64],
      shadowAnchor: [4, 62]
    });


    this.startLat = 51.505;
    this.startLng = -0.09;
    this.endLat = 51.507;
    this.endLng = -0.08;


    const startMarker = L.marker([this.startLat, this.startLng], { icon: greenIcon }).addTo(this.map);
    startMarker.bindPopup('Startpunkt');

    const endMarker = L.marker([this.endLat, this.endLng], { icon: redIcon }).addTo(this.map);
    endMarker.bindPopup('Endpunkt');

    (L as any ).Routing.control({
      waypoints: [
        L.latLng(this.startLat, this.startLng),
        L.latLng(this.endLat, this.endLng)
      ],
      routeWhileDragging: false,
      lineOptions: {
        styles: [
          { color: '#1E90FF', opacity: 0.85, weight: 6, dashArray: '', lineCap: 'round' }
        ]
      },
      addWaypoints: false,
      fitSelectedRoutes: true,
      show: false
    }).addTo(this.map);


  }




}
