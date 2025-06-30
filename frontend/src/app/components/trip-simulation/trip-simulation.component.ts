import {Component, inject, Input, OnDestroy, OnInit} from '@angular/core';
import {StompService} from '../../services/stomp.service';
import {
  BehaviorSubject,
  debounceTime,
  distinctUntilChanged,
  interval,
  Observable,
  Subscription,
  takeWhile,
  tap
} from 'rxjs';
import {
  Coordinate,
  Location,
  NominatimService,
  ORSFeatureCollection,
  ORSService,
  Route,
  RouteService,
  RouteUpdateRequestBody,
  SimulationAction,
  TripOffer,
  TripSimulationService,
} from '../../../api/sep_drive';
import * as L from "leaflet";
import {LatLngExpression} from "leaflet";
import * as turf from '@turf/turf';
import {TripVisualizerComponent} from '../trip-visualizer/trip-visualizer.component';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {MatSlider, MatSliderThumb} from '@angular/material/slider';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {DialogRatingComponent} from '../dialog-rating/dialog-rating.component';
import {AngularAuthService} from '../../services/angular-auth.service';
import {MatCard, MatCardContent, MatCardTitle} from '@angular/material/card';
import {Router} from '@angular/router';
import {MatDivider} from '@angular/material/divider';
import {MeterToKmPipe} from '../../pipes/meter-to-km.pipe';
import {SecondsToTimePipe} from '../../pipes/seconds-to-time.pipe';
import {CdkDrag, CdkDragDrop, CdkDragHandle, CdkDropList, moveItemInArray} from '@angular/cdk/drag-drop';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';
import {NgIf} from '@angular/common';


@Component({
  selector: 'app-trip-simulation',
  imports: [
    TripVisualizerComponent,
    MatIcon,
    MatIconButton,
    MatSlider,
    MatSliderThumb,
    FormsModule,
    ReactiveFormsModule,
    MatCard,
    MatCardContent,
    MatCardTitle,
    MatDivider,
    MeterToKmPipe,
    SecondsToTimePipe,
    MatButton,
    CdkDropList,
    MatFormField,
    MatLabel,
    MatInput,
    NgIf,
    CdkDrag,
    CdkDragHandle,
  ],
  templateUrl: './trip-simulation.component.html',
  standalone: true,
  styleUrl: './trip-simulation.component.css'
})
export class TripSimulationComponent implements OnInit, OnDestroy {
  @Input() tripOfferId!: string;
  @Input() tripOffer!: TripOffer

  private stompService = inject(StompService)
  private tripSimulationService = inject(TripSimulationService)
  private angularAuthService = inject(AngularAuthService)
  private router = inject(Router)
  routeService = inject(RouteService)
  orsService = inject(ORSService)
  nominatimService = inject(NominatimService)

  stops: Location[] = []

  private subscription!: Subscription;

  readonly dialog = inject(MatDialog);

  private _route$ = new BehaviorSubject<Route | null>(null);
  private _animationLocked$ = new BehaviorSubject<boolean>(true)
  private _animationDuration$ = new BehaviorSubject<number>(15000);

  animationLocked!: boolean
  route!: Route;
  originalRoute: Route | null = null

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

  partnerDisplayName = ""

  role: string | null = null
  private driverPresent = false


  //Reroute imports
  form = new FormGroup({
    query: new FormControl("")
  })
  showCard: boolean = false;
  selectedIndex = 0
  suggestedLocations: Location[] = []
  orsFeatureCollection: ORSFeatureCollection | null = null;



  ngOnInit(): void {
    this.originalRoute = structuredClone(this.tripOffer.tripRequest.route)

    this._route$.next(this.tripOffer.tripRequest.route);

    // update coordinates when route changes
    this._route$.subscribe(route => {
      if (route) {
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

        this.route = route;
        this.stops = this.route.stops;
      }
    })


    this.subscription = this.stompService
      .watchTopic<SimulationAction>(`/topic/simulation/${this.tripOfferId}`)
      .subscribe(message => {
        console.log("Received from socket:", message)
        if (message && message.actionType) {
          this.handleSimulationAction(message as SimulationAction)
        }
      })

    this.angularAuthService.role$.subscribe({
      next: role => {
        this.role = role
        if (this.role === "DRIVER") {
          this.partnerDisplayName = `${this.tripOffer.tripRequest.customer.firstName} ${this.tripOffer.tripRequest.customer.lastName} (${this.tripOffer.tripRequest.customer.username})`
          interval(1000)
            .pipe(
              takeWhile(() => !this.driverPresent),
              tap(() => {
                this.sendDriverPresent()
              })
            )
            .subscribe()
        } else if (this.role === "CUSTOMER") {
          this.partnerDisplayName = `${this.tripOffer.driver.firstName} ${this.tripOffer.driver.lastName} (${this.tripOffer.driver.username})`
        }
      }
    })

    this._animationDuration$.subscribe({
      next: value => {
        this.animationDuration = value
      }
    })

    this._animationLocked$.subscribe(animationLocked => {
      this.animationLocked = animationLocked;
    })

    //suggestion list
    this.form.get('query')!.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe({
      next: query => {
        this.nominatimService.search(query!).subscribe({
          next: response => {
            console.log(response)
            this.suggestedLocations = response
            this.showCard = response.length > 0 && !!query && query.trim().length > 0
          },
          error: err => {
            console.error(err)
            this.showCard = false
          }
        })
      },
      error: err => {
        console.error(err)
      }
    })
  }



  handleSimulationAction(simulationAction: SimulationAction): void {
    this.animationIndex = simulationAction.parameters.startIndex;

    if (simulationAction.actionType === "START") {
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
      if (!this.animationPaused && this.animationInitialized) {
        this.pauseAnimation()
        this.resumeAnimation()
      }
    } else if (simulationAction.actionType === "LOCK") {
      this._animationLocked$.next(true)
    } else if (simulationAction.actionType === "UNLOCK") {
      this._animationLocked$.next(false)
    } else if (simulationAction.actionType === "DRIVER_PRESENT") {
      if (this.role === "CUSTOMER") {
        this.sendAckDriverPresent()
      }
    } else if (simulationAction.actionType === "ACK_DRIVER_PRESENT") {
      if (this.role === "DRIVER") {
        this.unlock()
      }
      this.driverPresent = true
    } else {
      console.error("Unknown action type:", simulationAction.actionType)
    }
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  sendSimulationAction(simulationAction: SimulationAction) {
    this.stompService.send(`/app/simulation/${this.tripOfferId}`, simulationAction)
  }

  sendDriverPresent() {
    const action: SimulationAction = {
      actionType: "DRIVER_PRESENT",
      timestamp: new Date().toISOString(),
      parameters: {startIndex: this.animationIndex}
    }
    this.sendSimulationAction(action)
  }

  sendAckDriverPresent() {
    const action: SimulationAction = {
      actionType: "ACK_DRIVER_PRESENT",
      timestamp: new Date().toISOString(),
      parameters: {startIndex: this.animationIndex}
    }
    this.sendSimulationAction(action)
  }


  start(): void {
    const action: SimulationAction = {
      actionType: "START",
      timestamp: new Date().toISOString(),
      parameters: {startIndex: this.animationIndex}
    }
    this.sendSimulationAction(action)
  }

  stop(): void {
    const action: SimulationAction = {
      actionType: "STOP", timestamp: new Date().toISOString(),
      parameters: {startIndex: this.animationIndex}
    }
    this.sendSimulationAction(action)
  }

  info(): void {
    const action: SimulationAction = {
      actionType: "INFO", timestamp: new Date().toISOString(),
      parameters: {startIndex: this.animationIndex}
    }
    this.sendSimulationAction(action)
  }

  reroute(): void {
    const action: SimulationAction = {
      actionType: "REROUTE", timestamp: new Date().toISOString(),
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
    this.sendSimulationAction(action)
  }

  complete(): void {
    if (this.role && this.role === "CUSTOMER") {
      this.tripSimulationService.completeTrip(Number(this.tripOfferId)).subscribe({
        next: value => {
          this.openRatingDialog()
        },
        error: err => {
          console.error(err)
        }
      });
    } else {
      this.openRatingDialog()
    }
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
    this.sendSimulationAction(action)
  }

  unlock(): void {
    const action: SimulationAction = {
      actionType: "UNLOCK",
      timestamp: new Date().toISOString(),
      parameters: {startIndex: this.animationIndex}
    }
    this.sendSimulationAction(action)
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

  updateRoute() {
    if (this.stops.length >= 2) {
      const currentCoordinate: Coordinate = {
        longitude: this.coordinates[this.animationIndex][1],
        latitude: this.coordinates[this.animationIndex][0],
      }
      const routeUpdateRequestBody: RouteUpdateRequestBody = {
        currentCoordinate: currentCoordinate,
        locations: this.stops
      }
      this.lock()
      this.routeService.updateRoute(this.route.routeId, routeUpdateRequestBody).subscribe({
        next: value => {
          this.reroute()
        },
        error: err => {
          console.error(err)
        }
      })
    }
  }

  //Updates the map without having to save new routeEntity
  updateLocalRoute() {
    if (this.stops.length >= 2) {
      this.orsService.getRouteDirections(this.stops.map(stop => {
        let coordinate: Coordinate = {
          latitude: stop.coordinate.latitude,
          longitude: stop.coordinate.longitude
        }
        return coordinate
      })).subscribe({
        next: value => {
          this.orsFeatureCollection = value
          this.route = {
            routeId: -1,
            stops: this.stops,
            geoJson: value
          }
        },
        error: err => {
          console.log(err)
        }
      })
    }
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
      if (result !== null) {
        this.tripSimulationService.rateTrip(Number(this.tripOfferId), result as number + 1).subscribe({
          next: value => {
            console.log("Rated trip", result + 1)
            this.router.navigate(["/"])
          },
          error: err => {
            console.error(err)
          }
        })
      }
    })
  }
  //--------------------------------Reroute Card
  //suggestions card
  clickCard(index: number): void {
    console.log("Chosen card: ", index)
    this.selectedIndex = index
  }

  onInputChange() {
    const query = this.form.get('query')?.value
    this.showCard = query !== null && query!.trim().length > 0
  }

  onConfirm() {
    const selected = this.suggestedLocations[this.selectedIndex]

    this.stops.push(selected)
    this.form.get('query')?.setValue('')
    this.suggestedLocations = []
    this.showCard = false
    this.updateLocalRoute()
  }

  //droplist
  drop(event: CdkDragDrop<string[]>) {
    moveItemInArray(this.stops, event.previousIndex, event.currentIndex)
    this.updateLocalRoute();
  }

  remove(index: number) {
    this.stops.splice(index, 1)
    this.updateLocalRoute()
  }

  //Confirm or Cancel
  onCancel() {
    this.route = structuredClone(this.originalRoute!)
    this.stops = []
    this.stops = this.route.stops
    console.log(this.stops)
    this._route$.next(this.route)
  }
}
