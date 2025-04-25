import {HttpEvent, HttpHandlerFn, HttpRequest} from '@angular/common/http';
import {catchError, Observable, of, switchMap} from "rxjs";
import {inject} from "@angular/core";
import {AngularAuthService} from '../services/angular-auth.service';
import {AuthResponse, AuthService} from '../../api/sep_drive';
import {Router} from "@angular/router";

export function authInterceptor(req: HttpRequest<any>, next: HttpHandlerFn): Observable<HttpEvent<any>> {
  const angularAuthService = inject(AngularAuthService)
  const authService = inject(AuthService)

  let refreshAttempted = false;

  const setAuthHeader = (token: string) => req.clone({
    setHeaders: {Authorization: `Bearer ${token}`},
  })

  let authRequest = setAuthHeader(angularAuthService.getAccessToken())

  console.log(authRequest);

  return next(authRequest).pipe(
    catchError(err => {
      // pass error if not auth related or refresh wasn't successful
      // if (err.status !== 403 || refreshAttempted) {
      //   throw err;
      // }

      refreshAttempted = true;

      console.log("Invalid access token.")
      return authService.refresh().pipe(
        switchMap((res: AuthResponse) => {
          angularAuthService.consumeAuthResponse(res);
          authRequest = setAuthHeader(angularAuthService.getAccessToken())
          return next(authRequest);
        }),
        catchError(() => {
          // navigate to login if refresh failed?
          throw err;
        })
      )
      // if (err.error === "Invalid refresh token.") {
      //   console.log("Invalid refresh token.");
      //   // maybe navigate to login?
      //   return of();
      // } else if (err.error === "Invalid access token.") {
      //   console.log("Invalid access token.")
      //   return authService.refresh().pipe(
      //     switchMap((data: AccessResponse) => {
      //       angularAuthService.setAccessToken(data.accessToken);
      //       authRequest = setAuthHeader(angularAuthService.getAccessToken() || "")
      //       return next(authRequest);
      //     }),
      //     catchError(() => {
      //       // navigate to login if refresh failed?
      //       throw err;
      //     })
      //   )
      // } else {
      //   throw err;
      // }
    })
  )
}
