import {Component, inject, OnInit} from '@angular/core';
import {MatToolbar} from '@angular/material/toolbar';
import {Router, RouterModule} from '@angular/router';
import {MatIconModule} from '@angular/material/icon';
import {AngularAuthService} from '../../services/angular-auth.service';
import {AccountDTO, AccountService} from '../../../api/sep_drive';
import {NgStyle} from '@angular/common';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {MatPrefix, MatSuffix} from '@angular/material/input';


@Component({
  selector: 'app-navbar',
  imports: [
    MatIconModule, RouterModule, NgStyle, MatMenu, MatMenuTrigger, MatMenuItem, MatSuffix
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit {
  angularAuthService = inject(AngularAuthService)
  accountService = inject(AccountService)
  router = inject(Router)

  role: string | undefined = undefined;
  email: string | undefined = undefined;
  accountInformation: AccountDTO | undefined = undefined;

  profileImageExists: boolean = false;
  initials: string = 'JD';


  ngOnInit(): void {
    this.angularAuthService.role$.subscribe({
      next: val => {
        if (val) {
          this.role = val;
        }
      }
    })

    this.angularAuthService.email$.subscribe({
      next: val => {
        if (val) {
          this.email = val;
          this.accountService.getCurrentAccount().subscribe({
            next: res => {
              this.accountInformation = res;
              this.initials = this.getInitials(res.firstName, res.lastName)
              this.checkImageExists(res.profilePictureUrl ?? "")
            }
          })
        }
      }
    })

    this.accountService.getCurrentAccount().subscribe({
      next: res => {
        this.accountInformation = res;
        this.initials = this.getInitials(res.firstName, res.lastName)
        this.checkImageExists(res.profilePictureUrl ?? "")
      }
    })
  }

  getInitials(firstName: string, lastName: string): string {
    return (firstName?.charAt(0) || '') + (lastName?.charAt(0) || '');
  }

  checkImageExists(url: string) {
    if (!url) return;
    const img = new Image();
    img.onload = () => this.profileImageExists = true;
    img.onerror = () => this.profileImageExists = false;
    img.src = url;
  }

  onProfile(): void {
    if (this.accountInformation) {
      this.router.navigate(['/profile', this.accountInformation.username])
    }
  }

  onLogout(): void {
    this.angularAuthService.logout();
  }

  onBalance(): void {
    this.router.navigate(["/balance"])
  }
}
