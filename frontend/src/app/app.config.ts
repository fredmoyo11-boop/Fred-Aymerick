import {ApplicationConfig, LOCALE_ID, provideZoneChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';

import {routes} from './app.routes';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {MAT_DATE_LOCALE} from '@angular/material/core';
import {registerLocaleData} from '@angular/common';
import localeDe from "@angular/common/locales/de"
import {AccountService, AuthService} from '../api/sep_drive';
import {authInterceptor} from './interceptors/auth-interceptor';


registerLocaleData(localeDe);

export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({eventCoalescing: true}), provideRouter(routes), AuthService, AccountService, provideHttpClient(withInterceptors([authInterceptor])), {
    provide: MAT_DATE_LOCALE,
    useValue: "de"
  }, {provide: LOCALE_ID, useValue: "de"}]
};

