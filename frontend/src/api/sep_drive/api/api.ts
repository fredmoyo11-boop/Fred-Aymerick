export * from './account.service';
import { AccountService } from './account.service';
export * from './auth.service';
import { AuthService } from './auth.service';
export * from './route.service';
import { RouteService } from './route.service';
export const APIS = [AccountService, AuthService, RouteService];
