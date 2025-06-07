import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {RouteDTO} from '../../../api/sep_drive';
import {LayerGroup} from 'leaflet';

@Component({
  selector: 'app-route-visualizer',
  imports: [],
  templateUrl: './route-visualizer.component.html',
  styleUrl: './route-visualizer.component.css'
})
export class RouteVisualizerComponent implements OnInit, OnChanges{
  @Input() routeDTO!: RouteDTO
  @Input() pointerLayer!: LayerGroup


  ngOnInit() {

  }

  ngOnChanges(changes: SimpleChanges): void {
  }

}
