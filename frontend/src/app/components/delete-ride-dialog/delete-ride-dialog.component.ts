import { Component } from '@angular/core';
import {MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle} from '@angular/material/dialog';
import {MatButton} from '@angular/material/button';

@Component({
  selector: 'app-delete-ride-dialog',
  imports: [
    MatDialogActions,
    MatDialogContent,
    MatButton,
    MatDialogTitle
  ],
  templateUrl: './delete-ride-dialog.component.html',
  styleUrl: './delete-ride-dialog.component.css'
})
export class DeleteRideDialogComponent {

  constructor(
    public dialogRef: MatDialogRef<DeleteRideDialogComponent>
  ) {}

  onCancel() {
    this.dialogRef.close(false);

  }

  onConfirm() {
    this.dialogRef.close(true);

  }
}
