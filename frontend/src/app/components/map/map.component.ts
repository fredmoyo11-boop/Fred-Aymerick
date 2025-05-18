import {Component, inject, OnInit} from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-routing-machine';
import {RouteService} from '../../../api/sep_drive';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-map',
  imports: [
    MatButton,
    MatIconButton,
    MatIcon
  ],
  templateUrl: './map.component.html',
  styleUrl: './map.component.css'
})
export class MapComponent implements OnInit {
  routeService = inject(RouteService)

  index = 0

  map!: L.Map;
  routingControl: L.Routing.Control | undefined = undefined;

  startLan!: number;
  startLon!: number;
  endLan!: number;
  endLon!: number;
  viaPoints: Array<{ lan: number; lon: number }> = [];

  ngOnInit(): void {
    this.map = L.map("map");

    this.getRouteDataFromBackend(this.index);
  }

  selectedGeoJSONFile: File | null = null;

  onFileChanged(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length) {
      this.selectedGeoJSONFile = input.files[0];
    }
  }

  public createRoute(): void {
    this.routeService.importRoute(this.selectedGeoJSONFile!).subscribe({
      next: (res) => {
        this.index = Number(res.message);
        this.getRouteDataFromBackend(this.index)
      },
      error: (err) => {
        console.error(err);
      }
    })
  }


  private getRouteDataFromBackend(index: number): void {
    this.routeService.getFullRoute(index).subscribe(data => {
      if (data.length >= 2) {
        const start = data[0];
        const end = data[data.length - 1];

        this.startLan = parseFloat(start.latitude);
        this.startLon = parseFloat(start.longitude);

        this.endLan = parseFloat(end.latitude);
        this.endLon = parseFloat(end.longitude);

        this.viaPoints = []
      } else {
        // fallback demonstration route
        this.startLan = 51.4516; // Essen Hbf
        this.startLon = 7.0146;

        this.endLan = 51.5136;   // Dortmund Hbf
        this.endLon = 7.4653;

        this.viaPoints = [
          {lan: 51.4800, lon: 7.2000},
          {lan: 51.4900, lon: 7.3000}
        ];
      }
      this.initMap();
    });
  }

  private initMap(): void {
    this.map.setView([51.505, -0.09], 13);

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(this.map);


    const greenIcon = L.icon({
      iconUrl: 'https://cdn-icons-png.flaticon.com/512/684/684908.png',
      iconSize: [40, 40],
      iconAnchor: [20, 40],
      popupAnchor: [0, -40]
    });

    const redIcon = L.icon({
      iconUrl: 'https://cdn-icons-png.flaticon.com/512/190/190411.png',
      iconSize: [40, 40],
      iconAnchor: [20, 40],
      popupAnchor: [0, -40]
    });

    const orangeIcon = L.icon({
      iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-orange.png',
      iconSize: [25, 41],
      iconAnchor: [20, 40],
      popupAnchor: [0, -40]
    });

    if (this.routingControl) {
      this.routingControl.setWaypoints([])
    }

    this.routingControl = (L as any).Routing.control({
      waypoints: [
        L.latLng(this.startLan, this.startLon),
        ...this.viaPoints.map(p => L.latLng(p.lan, p.lon)),
        L.latLng(this.endLan, this.endLon)
      ],
      routeWhileDragging: false,
      lineOptions: {
        styles: [
          {color: '#1E90FF', opacity: 0.85, weight: 6, dashArray: '', lineCap: 'round'}
        ]
      },
      addWaypoints: false,
      fitSelectedRoutes: true,
      show: true,
      createMarker: (i: number, wp: any, nWps: number) => {
        if (i === 0) {
          return L.marker(wp.latLng, {icon: greenIcon}).bindPopup('Startpunkt');
        } else if (i === nWps - 1) {
          return L.marker(wp.latLng, {icon: redIcon}).bindPopup('Endpunkt');
        } else {
          return L.marker(wp.latLng, {icon: orangeIcon}).bindPopup(`Zwischenstopp ${i}`);
        }
      }
    }).addTo(this.map);
  }


}
