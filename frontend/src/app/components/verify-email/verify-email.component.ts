import {Component, inject, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {AngularAuthService} from '../../services/angular-auth.service';
import {of} from 'rxjs';
import {MatButton} from '@angular/material/button';
import {AuthService} from '../../../api/sep_drive';

@Component({
  selector: 'app-verify-email',
  imports: [
    MatButton
  ],
  templateUrl: './verify-email.component.html',
  styleUrl: './verify-email.component.css'
})
export class VerifyEmailComponent implements OnInit {
  angularAuthService = inject(AngularAuthService)
  authService = inject(AuthService)
  route = inject(ActivatedRoute)
  router = inject(Router);

  verified: boolean = false;

  token: string = "";

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get("token") || "";
    this.authService.verifyEmail(this.token).subscribe({
      next: (res) => {
        console.log(res);
        this.angularAuthService.consumeAuthResponse(res);
        this.verified = true;
        this.router.navigate(["/"]).then(of)
      },
      error: (err) => {
        console.error(err);
      }
    })
  }

  continue(): void {
    this.router.navigate(['/']).then(of);
  }
}
