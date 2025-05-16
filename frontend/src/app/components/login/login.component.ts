import {Component, ElementRef, inject, QueryList, signal, ViewChildren} from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatFormField, MatInput, MatLabel, MatSuffix} from '@angular/material/input';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {Router, RouterLink} from '@angular/router';
import {MatDivider} from '@angular/material/list';
import {AuthService, LoginRequest, OtpRequest} from '../../../api/sep_drive';
import {catchError, of} from 'rxjs';
import {AngularAuthService} from '../../services/angular-auth.service';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'app-login',
  imports: [
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatInput,
    MatButton,
    MatIconButton,
    MatSuffix,
    MatIcon,
    RouterLink,
    MatDivider,
    FormsModule,
  ],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  router = inject(Router)
  authService = inject(AuthService)
  angularAuthService = inject(AngularAuthService)

  // regex for common email addresses, not RFC 5322 conform, for our purpose enough
  emailRegex = /^[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$/;

  loginForm = new FormGroup({
    email: new FormControl("", [Validators.required, Validators.pattern(this.emailRegex)]),
    password: new FormControl("", [Validators.required, Validators.minLength(1)])
  })

  loginRequestSent: boolean = false;
  loginRequestError: boolean = false;
  loginRequestErrorMessage: string = "";


  @ViewChildren("otpInput") inputs!: QueryList<ElementRef>


  otp: string[] = new Array(6).fill("")
  otpString: string = ""

  hidePassword = signal(true)

  onSubmit(): void {
    this.sendLoginRequest()
  }

  sendLoginRequest(): void {
    const value = this.loginForm.value;

    const authRequest: LoginRequest = {
      email: value.email!,
      password: value.password!,
    }

    console.log(authRequest);

    this.authService.login(authRequest).subscribe({
      next: res => {
        this.loginRequestSent = true;
        this.loginRequestError = false;
      },
      error: err => {
        this.loginRequestError = true;
        if (err instanceof HttpErrorResponse && err.status === 401) {
          this.loginRequestErrorMessage = err.error.message
        } else {
          this.loginRequestErrorMessage = "Unbekannter Fehler beim Login. Bitte versuche es später erneut.";
        }
      }
    })
  }

  onClick(): void {
    const otpRequest: OtpRequest = {email: this.loginForm.value.email!, otp: this.otpString};
    console.log(otpRequest);
    this.authService.verifyOtp(otpRequest).subscribe({
      next: res => {
        this.angularAuthService.consumeAuthResponse(res)
        this.router.navigate(["/"]).then(of)
      },
      error: err => {
        console.error(err);
      }
    })
  }

  onInput(event: any, index: number): void {
    const value = event.target.value;

    this.otpString = this.otp.join("").replaceAll(" ", "")

    // if inputted value, go right
    if (value && index < this.otp.length - 1) {
      const idx = index + 1;
      this.focusOtpInput(idx)
    }
  }

  onKeyDown(event: KeyboardEvent, index: number): void {
    const target = event.target as HTMLInputElement;

    if (event.key === "Backspace") {
      // go left in otp input if backspace on empty field
      if (target.value === "" && index > 0) {
        const idx = index - 1;
        this.focusOtpInput(idx)
      }
    } else if (event.key >= "0" && event.key <= "9") {
      // valid input, do nothing
    }
  }

  focusOtpInput(index: number): void {
    this.inputs.toArray()[index].nativeElement.focus();
  }
}
