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
  @Input() startLan!: number;
  @Input() startLon!: number;
  @Input() endLan!: number;
  @Input() endLon!: number;



  ngOnInit(): void {
    this.initMap();
  }




  private initMap(): void {

    this.startLan = 51.4516; // Essen Hbf
    this.startLon = 7.0146;

    this.endLan = 51.5136;   // Dortmund Hbf
    this.endLon = 7.4653;

    this.map =  L.map('map').setView([this.startLan, this.startLon], 13);

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(this.map);

    var greenIcon = L.icon({
      iconUrl: 'https://cdn-icons-png.flaticon.com/512/684/684908.png',
      iconSize: [40, 40],
      iconAnchor: [20, 40],
      popupAnchor: [0, -40]
    });

    var redIcon = L.icon({
      iconUrl: 'https://cdn-icons-png.flaticon.com/512/190/190411.png',
      iconSize: [40, 40],
      iconAnchor: [20, 40],
      popupAnchor: [0, -40]
    });





    const startMarker = L.marker([this.startLan, this.startLon], { icon: greenIcon }).addTo(this.map);
    startMarker.bindPopup('Startpunkt');

    const endMarker = L.marker([this.endLan, this.endLon], { icon: redIcon }).addTo(this.map);
    endMarker.bindPopup('Endpunkt');

    (L as any ).Routing.control({
      waypoints: [
        L.latLng(this.startLan, this.startLon),
        L.latLng(this.endLan, this.endLon)
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
