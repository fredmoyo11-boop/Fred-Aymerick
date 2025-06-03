import {Component, inject, Input, OnDestroy, OnInit} from '@angular/core';
import {StompService} from '../../services/stomp.service';
import {BehaviorSubject, Subscription} from 'rxjs';
import {
  Location,
  TripOffer,
  RouteService,
  SimulationAction, Route, TripSimulationService,
} from '../../../api/sep_drive';
import * as L from "leaflet";
import * as turf from '@turf/turf';
import {LatLngExpression} from 'leaflet';
import {TripVisualizerComponent} from '../trip-visualizer/trip-visualizer.component';
import {MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatSlider, MatSliderThumb} from '@angular/material/slider';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CdkDrag, CdkDragDrop, CdkDragHandle, CdkDropList, moveItemInArray} from '@angular/cdk/drag-drop';
import {
  MatAccordion,
  MatExpansionPanel,
  MatExpansionPanelHeader, MatExpansionPanelTitle
} from '@angular/material/expansion';
import {MatDialog} from '@angular/material/dialog';
import {DialogRatingComponent} from '../dialog-rating/dialog-rating.component';


@Component({
  selector: 'app-trip-simulation',
  imports: [
    TripVisualizerComponent,
    MatIcon,
    MatIconButton,
    MatSlider,
    MatSliderThumb,
    FormsModule,
    CdkDrag,
    CdkDragHandle,
    CdkDropList,
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    ReactiveFormsModule,
  ],
  templateUrl: './trip-simulation.component.html',
  standalone: true,
  styleUrl: './trip-simulation.component.css'
})
export class TripSimulationComponent implements OnInit, OnDestroy {
  @Input() tripOfferId!: string;
  @Input() tripOffer!: TripOffer

  private stompService = inject(StompService)
  private routeService = inject(RouteService)
  private tripSimulationService = inject(TripSimulationService)

  stops: Location[] = []

  private subscription!: Subscription;

  readonly dialog = inject(MatDialog);

  private _routeDTO$ = new BehaviorSubject<Route | null>(null);
  private _isLocked$ = new BehaviorSubject<boolean>(false)
  private _animationDuration$ = new BehaviorSubject<number>(15000);

  isLocked!: boolean
  routeDTO!: Route;

  // routeDTO!: RouteDTO;
  animationLayer = L.layerGroup()
  private positionMarker = L.marker([0, 0])

  positionMarkerAnimationTimer = 0
  animationPaused = false
  animationCompleted = false
  animationInitialized = false
  animationIndex = 0

  sliderDuration = 15
  animationDuration!: number

  coordinates: number[][] = []

  ngOnInit(): void {
    // update coordinates when route changes
    this._routeDTO$.subscribe(routeDTO => {
      if (routeDTO) {
        const features = this.tripOffer.tripRequest.route.geoJson.features;
        if (features.length != 1) {
          throw new Error("Invalid ORS GeoJSON: Expected exactly one feature, got " + features.length + " features.")
        }
        const feature = features[0];
        if (feature.geometry.type != "LineString") {
          throw new Error("Invalid ORS GeoJSON: Expected LineString, got " + feature.geometry.type + ".")
        }

        let coordinates = feature.geometry.coordinates as number[][];
        this.coordinates = this.interpolateCoordinates(coordinates).map(coordinate => [coordinate[1], coordinate[0]]) as number[][];

        this.routeDTO = routeDTO;
        this.stops = this.routeDTO.stops;
      }
    })

    this._animationDuration$.subscribe({
      next: value => {
        this.animationDuration = value
      }
    })

    this._isLocked$.subscribe(isLocked => {
      this.isLocked = isLocked;
    })

    this._routeDTO$.next(this.tripOffer.tripRequest.route);

    this.subscription = this.stompService
      .watchTopic<SimulationAction>(`/topic/simulation/${this.tripOfferId}`)
      .subscribe(message => {
        console.log("Received from socket:", message)
        if (message && message.actionType) {
          this.handleSimulationAction(message as SimulationAction)
        }
      })
  }

  handleSimulationAction(simulationAction: SimulationAction): void {
    if (simulationAction.actionType === "START") {
      this.animationIndex = simulationAction.parameters!.startIndex!;
      if (this.animationInitialized) {
        this.resumeAnimation()
      } else {
        this.initializeAnimation()
      }
    } else if (simulationAction.actionType === "STOP") {
      this.pauseAnimation()
    } else if (simulationAction.actionType === "INFO") {
      console.log("Info received")
    } else if (simulationAction.actionType === "CHANGE_VELOCITY") {
      this.sliderDuration = simulationAction.parameters!.velocity!
      this._animationDuration$.next(this.sliderDuration * 1000)
      this.animationIndex = simulationAction.parameters!.startIndex!;

      if (!this.animationPaused && this.animationInitialized) {
        this.pauseAnimation()
        this.resumeAnimation()
      }
    } else if (simulationAction.actionType === "LOCK") {
      this._isLocked$.next(true)
    } else if (simulationAction.actionType === "UNLOCK") {
      this._isLocked$.next(false)
    } else {
      console.error("Unknown action type:", simulationAction.actionType)
    }


  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  start(): void {
    const action: SimulationAction = {
      actionType: "START",
      timestamp: new Date().toISOString(),
      parameters: {startIndex: this.animationIndex}
    }
    this.stompService.send(`/app/simulation/${this.tripOfferId}`, action)
  }

  stop(): void {
    const action: SimulationAction = {
      actionType: "STOP", timestamp: new Date().toISOString(),
      parameters: {startIndex: this.animationIndex}
    }
    this.stompService.send(`/app/simulation/${this.tripOfferId}`, action)
  }

  info(): void {
    const action: SimulationAction = {
      actionType: "INFO", timestamp: new Date().toISOString(),
      parameters: {startIndex: this.animationIndex}
    }
    this.stompService.send(`/app/simulation/${this.tripOfferId}`, action)
  }


  changeVelocity(velocity: number): void {
    const action: SimulationAction = {
      actionType: "CHANGE_VELOCITY",
      timestamp: new Date().toISOString(),
      parameters: {velocity: velocity, startIndex: this.animationIndex}
    }
    this.stompService.send(`/app/simulation/${this.tripOfferId}`, action)
  }

  complete(): void {
    this.tripSimulationService.completeTrip(Number(this.tripOfferId)).subscribe({
      next: value => {
        this.openRatingDialog()
      },
      error: err => {
        console.error(err)
      }
    });
  }

  onSliderChange(value: number): void {
    this.changeVelocity(value)
  }

  lock(): void {
    const action: SimulationAction = {
      actionType: "LOCK",
      timestamp: new Date().toISOString(),
      parameters: {startIndex: this.animationIndex}
    }
    this.stompService.send(`/app/simulation/${this.tripOfferId}`, action)
  }

  unlock(): void {
    const action: SimulationAction = {
      actionType: "UNLOCK",
      timestamp: new Date().toISOString(),
      parameters: {startIndex: this.animationIndex}
    }
    this.stompService.send(`/app/simulation/${this.tripOfferId}`, action)
  }

  getVelocity(n: number, duration: number): number {
    return n / duration
  }

  animateRoute(): void {
    if (!this.coordinates.length) return;

    this.positionMarker.addTo(this.animationLayer)

    let startTime = Date.now()


    let startIndex = this.animationIndex
    let currentIndex = 0


    console.log("Start time:", startTime)
    console.log("Current index:", currentIndex)
    console.log("Coordinates:", this.coordinates)

    let updateMarkerPosition = () => {
      if (this.animationPaused || currentIndex >= this.coordinates.length - 1) {
        this.animationIndex = currentIndex
        return;
      }

      const velocity = this.getVelocity(this.coordinates.length, this.animationDuration)
      console.log("Velocity:", velocity)
      const elapsedTime = Date.now() - startTime

      currentIndex = Math.floor(Math.min((startIndex + elapsedTime * velocity), this.coordinates.length - 1));
      const currentPosition = this.coordinates[currentIndex];

      this.animationIndex = currentIndex

      console.log("Current index:", currentIndex)
      console.log("Current position:", currentPosition)

      this.positionMarker.setLatLng(currentPosition as LatLngExpression);

      if (currentIndex < this.coordinates.length - 1) {
        this.positionMarkerAnimationTimer = window.setTimeout(updateMarkerPosition, 100)
      } else {
        this.animationCompleted = true
        this.complete()
      }
    }

    updateMarkerPosition()
  }

  private interpolateCoordinates(coordinates: number[][]) {
    const line = turf.lineString(coordinates);
    const distance = turf.length(line);
    const steps = 5000;
    const interpolated = [];

    for (let i = 0; i <= steps; i++) {
      const pt = turf.along(line, (distance * i) / steps).geometry.coordinates;
      interpolated.push(pt);
    }

    return interpolated;
  }

  initializeAnimation() {
    this.animationInitialized = true;
    this.animateRoute()
  }

  pauseAnimation() {
    if (this.animationPaused) return;
    this.animationPaused = true;
    clearTimeout(this.positionMarkerAnimationTimer)
  }

  resumeAnimation() {
    if (!this.animationPaused) return;
    this.animationPaused = false;
    this.animateRoute()
  }


  openRatingDialog() {
    const dialogRef = this.dialog.open(DialogRatingComponent, {
      width: '1000px',
      disableClose: true
    })

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.tripSimulationService.rateTrip(Number(this.tripOfferId), result as number + 1).subscribe({
          next: value => {
            console.log("Rated trip", result + 1)
          },
          error: err => {
            console.error(err)
          }
        })
      }
    })
  }
}
