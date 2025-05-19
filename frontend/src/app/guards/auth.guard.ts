import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {AngularAuthService} from '../services/angular-auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  let router = inject(Router)
  let angularAuthService = inject(AngularAuthService)

  const expectedRoles: string[] = route.data["roles"] || []
  const userRole = angularAuthService.getRole()

  console.log(`User ${angularAuthService.getEmail()} with role ${userRole} is trying to access ${route.url}`)

  // navigate to login on invoke on protected route
  if (!userRole) {
    router.navigate(["/"])
    return false
  }

  // navigate to home if user has insufficient permissions, act like route does not exist
  if (expectedRoles.length && !expectedRoles.includes(userRole)) {
    router.navigate(["/"])
    return false;
  }

  return true;
};
