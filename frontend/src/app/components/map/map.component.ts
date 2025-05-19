import {Component, inject, OnInit} from '@angular/core';
import * as L from 'leaflet';
import 'leaflet-routing-machine';
import {RouteService, WaypointResponse} from '../../../api/sep_drive';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {CircleMarkerOptions, LatLngExpression} from "leaflet";

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

    private map!: L.Map;
    private routeLayer = L.layerGroup()

    index = 0
    selectedGeoJSONFile: File | null = null;

    ngOnInit(): void {
        this.map = L.map("map").setView([0, 0], 2);

        L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        }).addTo(this.map);

        this.routeLayer.addTo(this.map);
    }


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
            this.updateMap(data)
        });
    }

    private updateMap(data: WaypointResponse[]): void {
        let stopMarkerOptions: CircleMarkerOptions = {
            radius: 5,
            fillColor: "#ff7800",
            color: "#000",
            weight: 1,
            opacity: 1,
            fillOpacity: 0.8
        }

        let startMarkerOptions: CircleMarkerOptions = {
            radius: 8,
            fillColor: "#0db610",
            color: "#000",
            weight: 1,
            opacity: 1,
            fillOpacity: 0.8
        }

        let endMarkerOptions: CircleMarkerOptions = {
            radius: 8,
            fillColor: "#df081f",
            color: "#000",
            weight: 1,
            opacity: 1,
            fillOpacity: 0.8
        }

        this.routeLayer.clearLayers()

        const coordinates: LatLngExpression[] = data.map(wp => [Number(wp.latitude), Number(wp.longitude)]).slice() as LatLngExpression[];

        L.polyline(coordinates, {color: "blue"}).addTo(this.routeLayer);

        const start = coordinates[0]
        L.circleMarker(start, startMarkerOptions).addTo(this.routeLayer);

        const end = coordinates[coordinates.length - 1]
        L.circleMarker(end, endMarkerOptions).addTo(this.routeLayer);

        // choose random point from coordinates
        const randomPoint = (points: LatLngExpression[]) => {
            const idx = Math.floor(Math.random() * points.length)
            return points[idx]
        }

        // for demonstration add between 0 and 5 random stop points to the route
        for (let i = 0; i < Math.floor(Math.random() * 6); i++) {
            L.circleMarker(randomPoint(coordinates), stopMarkerOptions).addTo(this.routeLayer);
        }

        this.map.fitBounds([start as L.LatLngTuple, end as L.LatLngTuple])
    }


}
