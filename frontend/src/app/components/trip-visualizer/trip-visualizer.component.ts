import * as L from "leaflet";
import * as turf from '@turf/turf';
import {
  CircleMarkerOptions,
  LayerGroup,
} from 'leaflet';
import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {Route} from '../../../api/sep_drive';

@Component({
  selector: 'app-trip-visualizer',
  templateUrl: './trip-visualizer.component.html',
  standalone: true,
  styleUrl: './trip-visualizer.component.css'
})
export class TripVisualizerComponent implements OnInit, OnChanges {
  @Input() route!: Route;
  @Input() animationLayerGroup!: LayerGroup;

  private map!: L.Map;
  private routeLayer = L.layerGroup()


  ngOnInit(): void {
    this.map = L.map('map',  {
      zoomControl: false,
      center: [0, 0],
      zoom: 2
    });

    L.control.zoom({
      position: 'bottomright'
    }).addTo(this.map)

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(this.map);

    this.routeLayer.addTo(this.map);
    this.animationLayerGroup.addTo(this.map)
    console.log("Route during init", this.route)

    this.updateMap();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['route'] && this.route && this.map) {
      console.log("Route dto:", this.route)
      this.updateMap();
    }
  }

  private updateMap(): void {
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

    // interpolate route coordinates
    this.route.geoJson.features = this.route.geoJson.features.map(feature => {
      if (feature.geometry.type === "LineString") {
        let coordinates = feature.geometry.coordinates as number[][];
        feature.geometry.coordinates = this.interpolateCoordinates(coordinates);
      }
      return feature;
    })


    this.routeLayer.clearLayers()

    const geoJSON = this.route.geoJson;

    this.routeLayer.addLayer(L.geoJSON(geoJSON, {
      onEachFeature: (feature, layer) => {
        if (feature.properties && feature.properties.displayName) {
          layer.bindPopup(feature.properties.displayName);
        }
      }
    }))

    const stops = this.route.stops;

    stops.forEach((stop, index) => {
      if (index === 0) {
        L.circleMarker([stop.coordinate.latitude!, stop.coordinate.longitude!], startMarkerOptions).addTo(this.routeLayer)
      } else if (index === stops.length - 1) {
        L.circleMarker([stop.coordinate.latitude!, stop.coordinate.longitude!], endMarkerOptions).addTo(this.routeLayer)
      } else {
        L.circleMarker([stop.coordinate.latitude!, stop.coordinate.longitude!], stopMarkerOptions).addTo(this.routeLayer)
      }
    })

    this.map.fitBounds([[geoJSON.bbox[1], geoJSON.bbox[0]], [geoJSON.bbox[3], geoJSON.bbox[2]]]);

  }

  private interpolateCoordinates(coordinates: number[][]) {
    const line = turf.lineString(coordinates);
    const distance = turf.length(line);
    const steps = 3000;
    const interpolated = [];

    for (let i = 0; i <= steps; i++) {
      const pt = turf.along(line, (distance * i) / steps).geometry.coordinates;
      interpolated.push(pt);
    }

    return interpolated;
  }
}
