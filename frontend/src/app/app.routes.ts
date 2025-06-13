import {Routes} from '@angular/router';
import {RegisterComponent} from './components/register/register.component';
import {VerifyEmailComponent} from './components/verify-email/verify-email.component';
import {LoginComponent} from './components/login/login.component';
import {StandardLayoutComponent} from './layouts/standard-layout/standard-layout.component';
import {HomeComponent} from './components/home/home.component';
import {FahranfrageErstellenComponent} from './components/fahranfrage-erstellen/fahranfrage-erstellen.component';
import {AktiveFahranfrageComponent} from './components/aktive-fahranfrage/aktive-fahranfrage.component';
import {authGuard} from './guards/auth.guard';
import {ProfileComponent} from './components/profile/profile.component';
import {ProfileSearchComponent} from './components/profile-search/profile-search.component';
import {BalanceComponent} from './components/balance/balance.component';
import {TripRequestComponent} from './components/trip-request/trip-request.component';

export const routes: Routes = [
  {path: "register", component: RegisterComponent},
  {path: "login", component: LoginComponent},
  {
    path: "verify", children: [
      {path: "email", component: VerifyEmailComponent},
    ]
  },
  {
    path: "", component: StandardLayoutComponent, children: [
      {path: "", component: HomeComponent},
      {
        path: "map",
        component: TripRequestComponent,
        canActivate: [authGuard],
        data: {roles: ["CUSTOMER"]}
      },
      {
        path: "social",
        component: ProfileSearchComponent,
        canActivate: [authGuard],
        data: {roles: ["CUSTOMER", "DRIVER"]}
      },
      {
        path: "profile/:username",
        component: ProfileComponent,
        canActivate: [authGuard],
        data: {roles: ["CUSTOMER", "DRIVER"]}
      },
      {
        path: "balance/:username",
        component: BalanceComponent,
        canActivate: [authGuard],
        data: {roles: ["CUSTOMER", "DRIVER"]}
      }
    ]
  },
  {path: "**", redirectTo: ""}
];
