import * as L from "leaflet";
import * as turf from '@turf/turf';
import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {RouteDTO} from '../../../api/sep_drive';
import {CircleMarkerOptions, LayerGroup} from 'leaflet';


@Component({
  selector: 'app-route-visualizer',
  imports: [],
  templateUrl: './route-visualizer.component.html',
  styleUrl: './route-visualizer.component.css'
})
export class RouteVisualizerComponent implements OnInit, OnChanges{
  @Input() routeDTO!: RouteDTO
  @Input() pointerLayer!: LayerGroup

  private map!: L.Map
  private routeLayer = L.layerGroup()

  ngOnInit(): void {
    this.map = L.map("map").setView([0, 0], 2)

    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    }).addTo(this.map)

    this.routeLayer.addTo(this.map)
    if (this.pointerLayer) {
      this.pointerLayer.addTo(this.map)
    }
    this.updateMap()
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['routeDTO'] && this.routeDTO && this.map) {
      console.log("Route DTO: ", this.routeDTO)
      this.updateMap()
    }
  }

  private updateMap(): void {
    let startMarkerOptions: CircleMarkerOptions = {
      radius: 8,
      fillColor: '#54d60a',
      color: '#000',
      weight: 1,
      opacity: 1,
      fillOpacity: 0.8
    }

    let endMarkerOptions: CircleMarkerOptions = {
      radius: 8,
      fillColor: '#d80000',
      color: '#000',
      weight: 1,
      opacity: 1,
      fillOpacity: 0.8
    }

    let stopMarkerOptions: CircleMarkerOptions = {
      radius: 6,
      fillColor: '#1671d0',
      color: '#000',
      weight: 1,
      opacity: 1,
      fillOpacity: 0.8
    }

    //When coordinates in geojson part of the line, then interpolate.
    this.routeDTO.geojson.features = this.routeDTO.geojson.features.map(feature => {
      if (feature.geometry.type === "LineString") {
        let coordinates = feature.geometry.coordinates as number [][]
        feature.geometry.coordinates = this.interpolateCoordinates(coordinates)
      }
      return feature
    })
    this.routeLayer.clearLayers()
    const geoJson = this.routeDTO.geojson

    this.routeLayer.addLayer(L.geoJSON(geoJson, {
      onEachFeature: (feature, layer) => {
        if (feature.properties && feature.properties.displayName) {
          layer.bindPopup(feature.properties.displayName);
        }
      }
    }))

    const stops = this.routeDTO.stops
    stops.forEach((stop, index)=> {
      if (index === 0) {
        L.circleMarker([stop.coordinate.latitude, stop.coordinate.longitude], startMarkerOptions).addTo(this.routeLayer)
      } else if (index === stops.length -1) {
        L.circleMarker([stop.coordinate.latitude, stop.coordinate.longitude], endMarkerOptions).addTo(this.routeLayer)
      } else {
        L.circleMarker([stop.coordinate.latitude, stop.coordinate.longitude], stopMarkerOptions).addTo(this.routeLayer)
      }
    })
    this.map.fitBounds([[geoJson.bbox[1], geoJson.bbox[0]], [geoJson.bbox[3], geoJson.bbox[2]]])
  }

  //Adds additional coordinates to the line coordinates
  private interpolateCoordinates(coordinates: number[][]) {
    const line = turf.lineString(coordinates)
    const distance = turf.length(line)
    const steps = 3000
    const interpolated = []

    for(let i = 0; i <= steps; i++) {
      const pt = turf.along(line, (distance * i) / steps).geometry.coordinates
      interpolated.push(pt)
    }
    return interpolated
  }
}
