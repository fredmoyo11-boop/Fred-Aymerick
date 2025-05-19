import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandlerFn,
  HttpInterceptorFn,
  HttpRequest
} from '@angular/common/http';
import {inject} from '@angular/core';
import {AngularAuthService} from '../services/angular-auth.service';
import {AuthService, AuthResponse} from '../../api/sep_drive';
import {BehaviorSubject, finalize, Observable, of, throwError} from 'rxjs';
import {catchError, filter, switchMap, take} from 'rxjs/operators';

let isRefreshing = false;
const accessTokenSubject = new BehaviorSubject<string | null>(null);

export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<any>, next: HttpHandlerFn): Observable<HttpEvent<any>> => {
  const authService = inject(AuthService);
  const angularAuthService = inject(AngularAuthService);

  const accessToken = angularAuthService.getAccessToken();

  let modifiedReq = addAuthenticationHeader(req, accessToken);

  return next(modifiedReq).pipe(
    catchError(err => {
      if (err instanceof HttpErrorResponse && err.status === 401) {
        const noRefreshEndpoints = [
          '/api/auth/login',
          '/api/auth/register',
          '/api/auth/refresh',
          '/api/auth/logout',
          '/api/auth/verify/otp',
          '/api/auth/verify/email',
          '/api/auth/verify/email/resend',
        ];

        if (noRefreshEndpoints.some(url => req.url.includes(url))) {
          return throwError(() => err);
        }

        return handleRefresh(req, next, authService, angularAuthService);
      }
      // throw error if it is not authentication related
      return throwError(() => err);
    })
  );
};

const addAuthenticationHeader = (req: HttpRequest<any>, token: string): HttpRequest<any> => {
  return req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });
}

const handleRefresh = (
  req: HttpRequest<any>,
  next: HttpHandlerFn,
  authService: AuthService,
  angularAuthService: AngularAuthService,
): Observable<HttpEvent<any>> => {
  if (!isRefreshing) {
    isRefreshing = true;
    accessTokenSubject.next(null);

    return authService.refresh().pipe(
      switchMap((authResponse: AuthResponse) => {
        angularAuthService.consumeAuthResponse(authResponse);
        accessTokenSubject.next(authResponse.accessToken);
        return next(addAuthenticationHeader(req, authResponse.accessToken));
      }),
      catchError(err => {
        accessTokenSubject.next(null);
        return throwError(() => err);
      }),
      finalize(() => isRefreshing = false)
    );
  } else {
    return accessTokenSubject.pipe(
      filter(token => token !== null),
      take(1),
      switchMap(token => next(addAuthenticationHeader(req, token!)))
    );
  }
}
