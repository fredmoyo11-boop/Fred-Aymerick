import {Component, inject, OnInit} from '@angular/core';
import {NgForOf, NgOptimizedImage} from '@angular/common';
import {AccountService} from '../../../api/sep_drive';
import {AccountDTO} from '../../../api/sep_drive';
import {FormControl, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {debounceTime, Observable} from 'rxjs';
import {RouterLink} from '@angular/router';
import {MatFormField, MatInput, MatLabel} from '@angular/material/input';

@Component({
  selector: 'app-profile-search',
  standalone: true,
  imports: [
    NgForOf,
    FormsModule,
    ReactiveFormsModule,
    RouterLink,
    MatFormField,
    MatLabel,
    MatInput,
  ],
  templateUrl: './profile-search.component.html',
  styleUrl: './profile-search.component.css'
})
export class ProfileSearchComponent implements OnInit {
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

  getLocalizedRole(role: string): string {
    if (role === "CUSTOMER") {
      return "Kunde";
    } else {
      return "Fahrer"
    }
  }
}
