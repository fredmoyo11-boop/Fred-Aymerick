import {Component, inject, OnInit} from '@angular/core';
import {AccountDTO, AccountService, BalanceService, Transaction} from '../../../api/sep_drive';
import {Router} from '@angular/router';
import {AbstractControl, FormControl, ReactiveFormsModule, ValidatorFn} from '@angular/forms';
import {NgIf} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatIcon} from '@angular/material/icon';
import {EuroPipe} from '../../pipes/euro.pipe';
import {AngularAuthService} from '../../services/angular-auth.service';
import {MatSnackBar} from '@angular/material/snack-bar';


@Component({
  selector: 'app-balance',
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatIcon,
    EuroPipe
  ],
  templateUrl: './balance.component.html',
  styleUrl: './balance.component.css'
})
export class BalanceComponent implements OnInit {

  angularAuthService = inject(AngularAuthService)
  accountService = inject(AccountService)
  balanceService = inject(BalanceService)
  router = inject(Router)
  snackBar = inject(MatSnackBar)


  account!: AccountDTO;
  amountControl = new FormControl(0, [amountValidator]);

  transactions: Transaction[] = []

  role: string | null = null

  ngOnInit(): void {
    this.angularAuthService.role$.subscribe({
      next: value => {
        this.role = value
      }
    })

    this.angularAuthService.email$.subscribe({
      next: _ => {
        this.refresh()
      }
    })
  }

  deposit() {
    if (this.amountControl.valid && this.amountControl.value) {
      this.balanceService.deposit(Number(this.amountControl.value.toFixed(2))).subscribe({
        next: response => {
          this.refresh()
          this.amountControl.reset()
          console.log("Deposit success (balance.ts):", response);
          console.log(this.account.balance)
        }, error: (err) => {
          console.error("Deposit error (balance.ts):", err);
        }
      })
    }
  }


  withdrawal() {
    if (this.amountControl.valid && this.amountControl.value) {
      if (this.amountControl.value > this.account.balance) {
        this.snackBar.open("Nicht genügend Guthaben zum abheben!", "Okay")
        return;
      }

      this.balanceService.withdraw(Number(this.amountControl.value.toFixed(2))).subscribe({
        next: response => {
          this.refresh()
          this.amountControl.reset()
          console.log("Withdraw success :", response);
          console.log(this.account.balance)
        }, error: (err) => {
          console.error("Deposit error (balance.ts):", err);
        }
      })
    }
  }

  refresh() {
    this.accountService.getCurrentAccount().subscribe({
      next: value => {
        this.account = value;
      },
      error: err => {
        console.error("Fehler beim Abrufen der Account-Informationen:", err)
      }
    })

    this.balanceService.getCurrentTransactions().subscribe({
      next: value => {
        console.log("Transactions:", value)
        this.transactions = value
      }, error: err => {
        console.error(err)
      }
    })
  }

  navigateToProfile(username: string) {
    this.router.navigate(["/profile", username])
  }
}

function amountValidator(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: boolean } | null => {
    const value = control.value as number

    if (!value) {
      return {amountEmpty: true}
    }

    if (isNaN(value)) {
      return {amountNaN: true}
    }

    if (value < 0) {
      return {amountNegative: true}
    }

    return null
  }
}





