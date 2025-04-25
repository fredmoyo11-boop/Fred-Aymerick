import { Component } from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {NavbarComponent} from '../../components/navbar/navbar.component';

@Component({
  selector: 'app-standard-layout',
  imports: [
    RouterOutlet,
    NavbarComponent
  ],
  templateUrl: './standard-layout.component.html',
  styleUrl: './standard-layout.component.css'
})
export class StandardLayoutComponent {

}
