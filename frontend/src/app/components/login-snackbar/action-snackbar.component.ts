import {Component, Inject} from '@angular/core';
import {MAT_SNACK_BAR_DATA, MatSnackBarRef} from '@angular/material/snack-bar';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';

@Component({
  selector: 'app-login-snackbar',
  imports: [
    MatButton,
    MatIconButton,
    MatIcon
  ],
  templateUrl: './action-snackbar.component.html',
  styleUrl: './action-snackbar.component.css'
})
export class ActionSnackbarComponent {

  constructor(public snackBarRef: MatSnackBarRef<ActionSnackbarComponent>,
              @Inject(MAT_SNACK_BAR_DATA) public data: {
                message: string,
                actionButtonIcon: string,
                actionButtonClick: (() => void )| undefined
              }) {
  }

  onActionClick(): void {
    console.log("Action clicked")
    if (this.data.actionButtonClick) {
      this.data.actionButtonClick()
    }
    this.snackBarRef.dismiss()
  }

  onDismissClick(): void {
    this.snackBarRef.dismiss()
  }

}
