import { Component } from '@angular/core';
import {MatButton} from "@angular/material/button";
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-top-buttons',
  imports: [
    MatButton,
    RouterLink
  ],
  templateUrl: './top-buttons.component.html',
  styleUrl: './top-buttons.component.css'
})
export class TopButtonsComponent {

}
