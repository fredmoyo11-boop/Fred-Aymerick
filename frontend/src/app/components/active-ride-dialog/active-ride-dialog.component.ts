import { Component, Inject } from '@angular/core';
import {
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialogTitle
} from '@angular/material/dialog';
import {MatButton} from '@angular/material/button';

@Component({
  selector: 'app-active-ride-dialog',
  imports: [
    MatDialogContent,
    MatDialogActions,
    MatButton,
    MatDialogTitle
  ],
  templateUrl: './active-ride-dialog.component.html',
  styleUrl: './active-ride-dialog.component.css'
})
export class ActiveRideDialogComponent {

  constructor(
    public dialogRef: MatDialogRef<ActiveRideDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data:{message:string}
  ) {
  }


  closeDialog() {
    this.dialogRef.close();

  }
}
