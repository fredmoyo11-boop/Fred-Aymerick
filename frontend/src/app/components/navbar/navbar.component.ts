import {Component} from '@angular/core';
import {MatToolbar} from '@angular/material/toolbar';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';


@Component({
  selector: 'app-navbar',
  imports: [
    MatIconModule,RouterModule
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {

  isLoggedIn: boolean = false;
}
