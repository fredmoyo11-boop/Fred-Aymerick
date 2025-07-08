import {Routes} from '@angular/router';
import {RegisterComponent} from './components/register/register.component';
import {VerifyEmailComponent} from './components/verify-email/verify-email.component';
import {LoginComponent} from './components/login/login.component';
import {StandardLayoutComponent} from './layouts/standard-layout/standard-layout.component';
import {HomeComponent} from './components/home/home.component';
import {authGuard} from './guards/auth.guard';
import {ProfileComponent} from './components/profile/profile.component';
import {ProfileSearchComponent} from './components/profile-search/profile-search.component';
import {AvailableTriprequestComponent} from './components/available-triprequest/available-triprequest.component';
import {TripHistoryComponent} from './components/trip-history/trip-history.component';
import {TripRequestComponent} from './components/trip-request/trip-request.component';
import {TripOfferComponent} from './components/trip-offer/trip-offer.component';
import {tripOfferResolver} from './resolvers/trip-offer.resolver';
import {BalanceComponent} from './components/balance/balance.component';
import {LeaderboardComponent} from './components/leaderboard/leaderboard.component';

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
        path: "request",
        component: TripRequestComponent,
        canActivate: [authGuard],
        data: {roles: ["CUSTOMER"]}
      },
      {
        path: "requests",
        component: AvailableTriprequestComponent,
        canActivate: [authGuard],
        data: {roles: ["DRIVER"]}
      },
      {
        path: "history",
        component: TripHistoryComponent,
        canActivate: [authGuard],
        data: {roles: ["DRIVER", "CUSTOMER"]}
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
        path: "offer/:id",
        component: TripOfferComponent,
        resolve: {
          tripOffer: tripOfferResolver
        },
        canActivate: [authGuard],
        data: {roles: ["CUSTOMER", "DRIVER"]}
      },
      {
        path: "balance",
        component: BalanceComponent,
        canActivate: [authGuard],
        data: {roles: ["CUSTOMER", "DRIVER"]}
      },
      {
        path:"leaderboard",
        component:LeaderboardComponent,
        canActivate: [authGuard],
        data: {roles: ["CUSTOMER", "DRIVER"]}
      }
    ]
  },
  {path: "**", redirectTo: ""}
];
