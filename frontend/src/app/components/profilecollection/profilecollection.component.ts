import { Component, inject, OnInit } from '@angular/core';
import {NgForOf, NgOptimizedImage} from '@angular/common';
import { AccountService } from '../../../api/sep_drive';
import { AccountDTO } from '../../../api/sep_drive';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import {debounceTime, Observable} from 'rxjs';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-profilecollection',
  standalone: true,
  imports: [
    NgForOf,
    FormsModule,
    ReactiveFormsModule,
    RouterLink,
  ],
  templateUrl: './profilecollection.component.html',
  styleUrl: './profilecollection.component.css'
})
export class ProfilecollectionComponent implements OnInit {
  service = inject(AccountService);

  searchControl = new FormControl('');
  allAccounts: AccountDTO[] = [];
  filteredAccounts: AccountDTO[] = [];
  length = 0;
  i: number = 0;

  ngOnInit(): void {
    this.loadAllAccounts();

    this.searchControl.valueChanges.pipe(
      debounceTime(300)
    ).subscribe(searchTerm => {
      this.filterAccounts(searchTerm ?? '');
    });
  }



  loadAllAccounts() {
    this.service.searchUserProfiles("").subscribe(accounts => {
      console.log("Accounts: ", accounts);
      this.allAccounts = accounts;
      this.filteredAccounts = accounts;
    });
  }





  filterAccounts(search: string): void {
    const term = search.toLowerCase().trim();
    if (!term) {
      this.filteredAccounts = this.allAccounts;
    } else {
      this.filteredAccounts = this.allAccounts.filter(account =>
        account.username.toLowerCase().includes(term)
      );
    }
  }

  getInitialen(firstName: string, lastName: string): string {
    const first = firstName?.charAt(0).toUpperCase() || '';
    const last = lastName?.charAt(0).toUpperCase() || '';
    return first + last;
  }
}
