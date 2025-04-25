import {Component, inject, OnInit} from '@angular/core';
import {AccountService} from '../../../api/sep_drive';
import {AngularAuthService} from '../../services/angular-auth.service';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {

  accountService = inject(AccountService)
  angularAuthService = inject(AngularAuthService)

  message: string = "No request sent."

  accessToken: string = "";

  ngOnInit(): void {
    this.accessToken = this.angularAuthService.getAccessToken()
  }

  sendRequest(): void {
    this.accountService.health1().subscribe({
      next: stringResponse => {
        this.message = stringResponse.message;
      },
      error: error => {
        this.message = error.message;
      }
    })
  }

  clearAccessToken(): void {
    this.accessToken = "";
    this.angularAuthService.clearAccessToken()
  }

}
