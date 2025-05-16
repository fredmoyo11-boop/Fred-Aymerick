import {Component, inject, Input, OnInit} from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-routing-machine';
import {RouteService} from '../../../api/sep_drive';




@Component({
  selector: 'app-map',
  imports: [],
  templateUrl: './map.component.html',
  styleUrl: './map.component.css'
})
export class MapComponent implements OnInit {
  routeService = inject(RouteService)

  map: any;
  startLan!: number;
  startLon!: number;
  endLan!: number;
  endLon!: number;
  viaPoints: Array<{ lan: number; lon: number }> = [];




  ngOnInit(): void {
  this.getRouteDataFromBackend();
}

private getRouteDataFromBackend(): void {

  this.routeService.getFullRoute(1).subscribe(data => {
    if (data.length >= 2) {
      const start = data[0];
      const end = data[data.length - 1];
      const midpoints = data.slice(1, -1);

      this.startLan = parseFloat(start.latitude);
      this.startLon = parseFloat(start.longitude);
      this.endLan = parseFloat(end.latitude);
      this.endLon = parseFloat(end.longitude);
      this.viaPoints = midpoints.map(p => ({
        lan: parseFloat(p.latitude),
        lon: parseFloat(p.longitude)
      }));

      this.initMap();
    }
  });
}





  private initMap(): void {
    //
    // this.startLan = 51.4516; // Essen Hbf
    // this.startLon = 7.0146;
    //
    // this.endLan = 51.5136;   // Dortmund Hbf
    // this.endLon = 7.4653;

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
      const marker = L.marker([point.lan, point.lon], { icon: orangeIcon }).addTo(this.map);
      marker.bindPopup(`Zwischenstopp ${index + 1}`);
    });

    (L as any ).Routing.control({
      waypoints: [
        L.latLng(this.startLan, this.startLon),
        ...this.viaPoints.map(p => L.latLng(p.lan, p.lon)),
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
