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
  this.http.get<any>('http://localhost:8080/api/route')
    .subscribe(data => {
      this.startLat = data.startLat;
      this.startLng = data.startLng;
      this.endLat = data.endLat;
      this.endLng = data.endLng;
      this.initMap();
    });
} */

  viaPoints: Array<{ lat: number, lng: number }> = [
    { lat: 51.4800, lng: 7.2000 },
    { lat: 51.4900, lng: 7.3000 }
  ];

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

    var orangeIcon = L.icon({
      iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-orange.png',
      iconSize: [40, 40],
      iconAnchor: [20, 40],
      popupAnchor: [0, -40]
    });





    const startMarker = L.marker([this.startLan, this.startLon], { icon: greenIcon }).addTo(this.map);
    startMarker.bindPopup('Startpunkt');

    const endMarker = L.marker([this.endLan, this.endLon], { icon: redIcon }).addTo(this.map);
    endMarker.bindPopup('Endpunkt');

    this.viaPoints.forEach((point, index) => {
      const marker = L.marker([point.lat, point.lng], { icon: orangeIcon }).addTo(this.map);
      marker.bindPopup(`Zwischenstopp ${index + 1}`);
    });

    (L as any ).Routing.control({
      waypoints: [
        L.latLng(this.startLan, this.startLon),
        ...this.viaPoints.map(p => L.latLng(p.lat, p.lng)),
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
      show: true
    }).addTo(this.map);


  }




}
