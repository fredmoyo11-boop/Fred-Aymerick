import {Component, inject, signal} from '@angular/core';
import {MatButtonToggle, MatButtonToggleGroup} from '@angular/material/button-toggle';
import {MatIcon} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from '@angular/material/datepicker';
import {
  DateAdapter,
  MAT_DATE_FORMATS,
  MAT_DATE_LOCALE,
  MatOption,
  provideNativeDateAdapter
} from '@angular/material/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators
} from '@angular/forms';
import {MatButton, MatIconButton} from '@angular/material/button';
import {formatDate, NgClass, NgOptimizedImage} from '@angular/common';
import {MatDivider} from '@angular/material/list';
import {RouterLink} from '@angular/router';
import {AuthService} from '../../../api/sep_drive';
import {MomentDateAdapter} from '@angular/material-moment-adapter';
import {HttpErrorResponse} from '@angular/common/http';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatSelect} from '@angular/material/select';

export const MY_DATE_FORMATS = {
  parse: {
    dateInput: 'DD.MM.YYYY',
  },
  display: {
    dateInput: 'DD.MM.YYYY',
    monthYearLabel: 'MMMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  },
}

@Component({
  selector: 'app-register-edit',
  imports: [
    MatButtonToggleGroup,
    MatButtonToggle,
    MatIcon,
    MatInputModule,
    MatFormFieldModule,
    MatDatepickerInput,
    MatDatepickerToggle,
    MatDatepicker,
    ReactiveFormsModule,
    MatButton,
    MatDivider,
    RouterLink,
    MatIconButton,
    MatSelect,
    MatOption
  ],
  providers: [{provide: MAT_DATE_LOCALE, useValue: 'de'},
    {provide: DateAdapter, useClass: MomentDateAdapter},
    {provide: MAT_DATE_FORMATS, useValue: MY_DATE_FORMATS},],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  authService = inject(AuthService)

  registerRequestSuccess: boolean = false;
  registerRequestErrorMessage = ""

  private _snackBar = inject(MatSnackBar)

  registerForm = new FormGroup({
    role: new FormControl('', [Validators.required]),
    firstName: new FormControl('', [Validators.required]),
    lastName: new FormControl('', [Validators.required]),
    username: new FormControl('', [Validators.required]),
    birthday: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required, Validators.email]),
    emailConfirmation: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required]),
    passwordConfirmation: new FormControl('', [Validators.required]),
    carType: new FormControl("MEDIUM")
  }, {validators: [emailMatchValidator, passwordMatchValidator]})

  selectedFile: File | null = null;
  imagePreview: string | ArrayBuffer | null = null;

  hidePassword = signal(true)
  hidePasswordConfirmation = signal(true)

  onSubmit() {
    this.sendRegisterRequest()
  }

  onFileChanged(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length) {
      this.selectedFile = input.files[0];
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result;
      }
      reader.readAsDataURL(input.files[0]);
    }
  }

  convertDate(date: string | Date): string {
    date = date instanceof Date ? date : new Date(date);
    return formatDate(date, "yyyy-MM-dd", "de");
  }

  sendRegisterRequest() {
    const value = this.registerForm.value;

    let registrationDTO = {
      email: value.email!,
      password: value.password!,
      firstName: value.firstName!,
      lastName: value.lastName!,
      username: value.username!,
      birthday: this.convertDate(value.birthday!),
      role: value.role!,
      carType: ""
    };

    if (registrationDTO.role === "DRIVER") {
      registrationDTO.carType = value.carType!;
    }

    this.authService.register(registrationDTO, this.selectedFile || undefined).subscribe({
      next: success => {
        console.log(success)
        this.registerRequestSuccess = true;
      },
      error: err => {
        console.log("Err", err)
        this.registerRequestErrorMessage = "";
        if (err instanceof HttpErrorResponse && err.status === 401) {
          const errorMessage = err.error.message;
          if (errorMessage === "Email already exists.") {
            this.registerForm.get("email")!.setValue("")
            this.registerForm.get("emailConfirmation")!.setValue("")
            this.registerRequestErrorMessage = "Diese Email ist bereits vergeben."
          } else if (errorMessage === "Username already exists.") {
            this.registerForm.get("username")!.setValue("")
            this.registerRequestErrorMessage = "Dieser Benutzername ist bereits vergeben."
          } else if (errorMessage === "Username cannot contain @ as character.") {
            this.registerForm.get("username")!.setValue("")
            this.registerRequestErrorMessage = "Der Benutzername darf kein @ enthalten."
          }
        }
        if (!this.registerRequestErrorMessage) {
          this.registerRequestErrorMessage = "Unbekannter Fehler beim Registrieren. Bitte versuche es später erneut."
          this.registerForm.reset();
        }
        this._snackBar.open(this.registerRequestErrorMessage, "Okay")
      }
    })
  }
}

const emailMatchValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const email = control.get("email")
  const emailConfirmation = control.get("emailConfirmation");

  if (!email || !emailConfirmation) {
    return null;
  }

  return email.value === emailConfirmation.value ? null : {emailMismatch: true};
}

const passwordMatchValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const password = control.get("password")
  const passwordConfirmation = control.get("passwordConfirmation");

  if (!password || !passwordConfirmation) {
    return null;
  }

  return password.value === passwordConfirmation.value ? null : {passwordMismatch: true};
}
