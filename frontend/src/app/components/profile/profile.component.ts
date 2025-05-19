import {Component, inject, OnInit} from '@angular/core';

import {ActivatedRoute} from '@angular/router';
import {AccountDTO} from '../../../api/sep_drive';
import {AccountService} from '../../../api/sep_drive';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-profile',
  imports: [
    NgIf
  ],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  service = inject(AccountService)

  constructor(private route: ActivatedRoute) {
  }

  account!: AccountDTO;
  paramUsername = "";

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.paramUsername = params.get('username') ?? '';
      if (this.paramUsername) {
        this.service.getAccountProfile(this.paramUsername).subscribe({
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

  getLocalizedRole(role: string): string {
    if (role === "CUSTOMER") {
      return "Kunde";
    } else {
      return "Fahrer"
    }
  }

  getLocalizedCarType(carType: string): string {
    if (carType === "SMALL") {
      return "Klein";
    } else if (carType === "MEDIUM") {
      return "Medium"
    } else if (carType === "DELUXE") {
      return "Deluxe"
    } else {
      return ""
    }
  }
}
