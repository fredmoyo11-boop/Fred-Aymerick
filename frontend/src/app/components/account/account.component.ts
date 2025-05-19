import {Component, inject, OnInit} from '@angular/core';


import {ActivatedRoute} from '@angular/router';
import {AccountDTO} from '../../../api/sep_drive/model/accountDTO';
import {AccountService} from '../../../api/sep_drive';
import {NgIf} from '@angular/common';



@Component({
  selector: 'app-account',
  imports: [
    NgIf
  ],
  templateUrl: './account.component.html',
  styleUrl: './account.component.css'
})


export class AccountComponent implements OnInit {
  service = inject(AccountService)

  constructor(private route: ActivatedRoute) {
  }


  account: AccountDTO = {
    ratings: [],
    birthday: "",
    email: "",
    firstName: "",
    lastName: "",

    role: "",
    totalNumberOfRides: 0,
    username: "",

    carType: "SMALL",
    profilePictureUrl: ""

  };
  urlusername = "";

  ngOnInit(): void {

    this.route.paramMap.subscribe(params => {
      this.urlusername = params.get('username') ?? '';

      console.log(this.urlusername)

      if (this.urlusername) {

        this.service.getAccountProfile(this.urlusername).subscribe({
          next: (value: AccountDTO) => {
            this.account = value;
            console.log(this.account.profilePictureUrl)
            // this.account.email = this.service.getAccountprofile(this.urlemail)

          },
          error: (err) => {
            console.error('Fehler beim Abrufen der Account-Informationen:', err);

          }
        });
      }
    });
  }
  getInitialen(firstName: string, lastName: string): string {
    const first = firstName?.charAt(0).toUpperCase() || '';
    const last = lastName?.charAt(0).toUpperCase() || '';
    return first + last;
  }

}


  //
  //
  // account: AccountDTO = {
  //   ratings: [],
  //
  //   username: 'amin',
  //   email: 'amin@rais.com',
  //   role: 'CUSTOMER',
  //   firstName: 'amin',
  //   lastName: 'rais',
  //   profilePictureUrl: '',
  //   carType: 'SMALL',
  //   birthday: '11.11.2000',
  //   // ratings: ["5"],
  //   totalNumberOfRides: 55,
  // }
  // account1: AccountDTO = {
  //   ratings: [],
  //
  //   username: 'truck',
  //   email: 'sara.khan@example.com',
  //   role: 'CUSTOMER',
  //   firstName: 'Sara',
  //   lastName: 'Khan',
  //   profilePictureUrl: '',
  //   carType: 'MEDIUM',
  //   birthday: '15.04.1995',
  //   totalNumberOfRides: 34,
  // };
  //
  // account2: AccountDTO = {
  //   ratings: [],
  //
  //   username: 'track',
  //   email: 'max.mustermann@example.com',
  //   role: 'DRIVER',
  //   firstName: 'Max',
  //   lastName: 'Mustermann',
  //   profilePictureUrl: '',
  //   carType: 'DELUXE',
  //   birthday: '08.08.1988',
  //   totalNumberOfRides: 120,
  // };
  //
  // account3: AccountDTO = {
  //   ratings: [],
  //
  //   username: 'trick',
  //   email: 'layla.ahmed@example.com',
  //   role: 'CUSTOMER',
  //   firstName: 'Layla',
  //   lastName: 'Ahmed',
  //   profilePictureUrl: '',
  //   carType: 'SMALL',
  //   birthday: '22.06.1999',
  //   totalNumberOfRides: 12,
  // };
  //
  // account4: AccountDTO = {
  //   ratings: [],
  //
  //   username: 'tick',
  //   email: 'tom.driver@example.com',
  //   role: 'DRIVER',
  //   firstName: 'Tom',
  //   lastName: 'Schneider',
  //   profilePictureUrl: '',
  //   carType: 'MEDIUM',
  //   birthday: '01.01.1990',
  //   totalNumberOfRides: 200,
  // };

//amin.rais@stud.uni-due.de

// type AccountInformation = {
//   username: string;
//   email: string;
//   role: string;
//   firstName: string;
//   lastName: string;
//   profilePictureUrl : string;
//   carType: string;
//   birthday: string;
//   ratings: string;
//   totalNumberOfRides: bigint;
//
//


