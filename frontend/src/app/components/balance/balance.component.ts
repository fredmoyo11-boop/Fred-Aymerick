import {Component, inject, OnInit} from '@angular/core';
import {AccountDTO, AccountService, TransactionService} from '../../../api/sep_drive';
import {ActivatedRoute} from '@angular/router';
import {FormControl, ReactiveFormsModule} from '@angular/forms';
import {NgIf} from '@angular/common';


@Component({
  selector: 'app-balance',
  imports: [
    ReactiveFormsModule,
    NgIf,
  ],
  templateUrl: './balance.component.html',
  styleUrl: './balance.component.css'
})
export class BalanceComponent implements OnInit{

  accountService = inject(AccountService)
  transactionService = inject(TransactionService)


  account! : AccountDTO;
  // paramUsername =  "";
  searchControl =new FormControl("");
  inputAmount = 0;
  paramUsername = "";


  constructor(private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.paramUsername = params.get('username') ?? '';
      if (this.paramUsername) {
        this.accountService.getAccountProfile(this.paramUsername).subscribe({
          next: (value: AccountDTO) => {
            this.account = value;
          },
          error: (err) => {
            console.error('Fehler beim Abrufen der Account-Informationen:', err);
          }
        });
      }
    });
  }




validator(input: FormControl): number {
  const value = input.value;

  if (value === null || value === '') {
    throw new Error('Eingabe ist leer oder null');
  }



  if (isNaN(value)) {
    throw new Error('Ungültiges Zahlenformat');
  }
  const roundedValue = value.toFixed(2);

  return Number(roundedValue);
}


deposit(){
    this.inputAmount = this.validator(this.searchControl);
    this.transactionService.deposit(this.inputAmount).subscribe({
      next: (response) => {

        this.refresh();
        this.searchControl.setValue("")
        console.log("Deposit success (balance.ts):", response);
        console.log(this.account.balance)
      },
      error: (err) => {
        console.error("Deposit error (balance.ts):", err);
      }
    });

}


  withdrawal() {
    this.inputAmount = this.validator(this.searchControl);

    if (this.account.balance - this.inputAmount < 0){
      this.searchControl.setValue("")

       throw new Error("not enough in the Balance")
    }
    this.transactionService.withdraw(this.inputAmount).subscribe({
      next: (response) => {

        this.refresh();
        this.searchControl.setValue("")
        console.log("Withdraw success :", response);
      },
      error: (err) => {
        console.error("Withdraw error:", err);
      }
    });
  }

  refresh(){
        this.accountService.getAccountProfile(this.paramUsername).subscribe({
          next: (value: AccountDTO) => {
            this.account = value;
          },
          error: (err) => {
            console.error('Fehler beim ERNEUTEN Abrufen der Account-Informationen:', err);
          }
        });
      }

  validWithdrawal() {
    this.inputAmount = this.validator(this.searchControl);
    return this.account.balance - this.inputAmount < 0

  }


}






