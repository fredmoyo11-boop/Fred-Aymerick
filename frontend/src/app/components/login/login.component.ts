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
import {MatSnackBar} from '@angular/material/snack-bar';
import {ActionSnackbarComponent} from '../action-snackbar/action-snackbar.component';

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
  private _snackBar = inject(MatSnackBar)


  loginForm = new FormGroup({
    uniqueIdentifier: new FormControl("", [Validators.required]),
    password: new FormControl("", [Validators.required, Validators.minLength(1)])
  })

  loginRequestSent: boolean = false;
  loginRequestErrorMessage: string = "";

  otpRequestErrorMessage: string = "";

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
      uniqueIdentifier: value.uniqueIdentifier!,
      password: value.password!,
    }

    console.log(authRequest);

    this.authService.login(authRequest).subscribe({
      next: res => {
        this.loginRequestSent = true;
      },
      error: err => {
        let actionButtonIcon = ""
        let actionButtonClick: (() => void) | undefined = undefined;
        this.loginRequestErrorMessage = "";

        if (err instanceof HttpErrorResponse && err.status === 401) {
          const errorMessage = err.error.message;
          if (errorMessage.includes("credentials")) {
            this.loginRequestErrorMessage = "Ungültige Benutzerdaten. Benutzername oder Passwort falsch."
          } else if (errorMessage.includes("verification")) {
            this.loginRequestErrorMessage = "Du musst deine Email noch verifizieren. Bitte klicke auf den Link in der Email. Erneut senden?"
            actionButtonIcon = "restart_alt"
            actionButtonClick = () => {
              this.authService.resendVerificationEmail(this.loginForm.value.uniqueIdentifier!).subscribe({
                next: res => {
                  this._snackBar.open("Email erneut gesendet.", "Okay")
                },
                error: err => {
                  console.error(err);
                }
              })
            }
          }
        }
        if (!this.loginRequestErrorMessage) {
          this.loginRequestErrorMessage = "Unbekannter Fehler beim Login. Bitte versuche es später erneut."
        }
        this.loginForm.get("password")!.setValue("")

        this._snackBar.openFromComponent(ActionSnackbarComponent, {
          data: {
            message: this.loginRequestErrorMessage,
            actionButtonIcon: actionButtonIcon,
            actionButtonClick: actionButtonClick
          }
        })
      }
    })
  }

  onClick(): void {
    const otpRequest: OtpRequest = {uniqueIdentifier: this.loginForm.value.uniqueIdentifier!, otp: this.otpString};
    console.log(otpRequest);
    this.authService.verifyOtp(otpRequest).subscribe({
      next: res => {
        this.angularAuthService.consumeAuthResponse(res)
        this.router.navigate(["/"]).then(of)
      },
      error: err => {
        let actionButtonIcon = ""
        let actionButtonClick: (() => void) | undefined = undefined;
        this.otpRequestErrorMessage = "";

        if (err instanceof HttpErrorResponse && err.status === 401) {
          const errorMessage = err.error.message;
          console.log(errorMessage)
          if (errorMessage === "OTP not found." || errorMessage === "OTP expired." || errorMessage === "OTP invalid.") {
            this.otpRequestErrorMessage = "Das von dir eingegbene OTP ist ungültig. Erneut senden?"
            actionButtonIcon = "restart_alt"
            actionButtonClick = () => {
              this.sendLoginRequest()
              this._snackBar.open("OTP erneut gesendet.", "Okay")
            }
          } else {
            this.otpRequestErrorMessage = "Bei der Verifizierung des OTP is ein unbekannter Fehler aufgetreten. Bitte versuche es später erneut."
          }
        }
        if (!this.otpRequestErrorMessage) {
          this.otpRequestErrorMessage = "Unbekannter Fehler beim Login. Bitte versuche es später erneut."
        }

        console.log(this.otpRequestErrorMessage, actionButtonIcon, actionButtonClick)

        this._snackBar.openFromComponent(ActionSnackbarComponent, {
          data: {
            message: this.otpRequestErrorMessage,
            actionButtonIcon: actionButtonIcon,
            actionButtonClick: actionButtonClick
          }
        })
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
