import {Component, inject} from '@angular/core';
import {MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle} from '@angular/material/dialog';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatIcon} from '@angular/material/icon';
import {NgClass} from '@angular/common';

@Component({
  selector: 'app-dialog-rating',
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatIconButton,
    MatIcon,
    NgClass,
    MatDialogActions,
    MatButton
  ],
  templateUrl: './dialog-rating.component.html',
  styleUrl: './dialog-rating.component.css'
})
export class DialogRatingComponent {
  dialog = inject(MatDialogRef<DialogRatingComponent>)


  selectedIndex = 0

  setSelectedIndex(index: number) {
    this.selectedIndex = index
  }

  onCancel(): void {
    this.dialog.close();
  }

  onConfirm() {
    this.dialog.close(this.selectedIndex);
  }

}
