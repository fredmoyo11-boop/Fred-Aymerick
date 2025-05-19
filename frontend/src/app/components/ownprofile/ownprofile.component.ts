import {Component, inject, OnInit} from '@angular/core';
import {NgIf} from "@angular/common";
import {AccountDTO, AccountService} from '../../../api/sep_drive';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-ownprofile',
    imports: [
        NgIf
    ],
  templateUrl: './ownprofile.component.html',
  styleUrl: './ownprofile.component.css'
})
export class OwnprofileComponent implements OnInit{

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

        this.service.getCurrentAccount().subscribe({
          next: (value: AccountDTO)=> {
            this.account= value;
            console.log(this.account)
          },
          error: (err) => {
            console.error("Fehler beim aufrufen der Account-Inofrmationen:",err)
          }
        }
        )
      }
    }




