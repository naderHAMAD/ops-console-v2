import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isAuthenticated()) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};

// Guard paramétrable par permission, ex: canActivate: [roleGuard('user.manage')]
export const roleGuard = (requiredPermission: string): CanActivateFn => {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);

    if (auth.isAuthenticated() && auth.hasPermission(requiredPermission)) {
      return true;
    }

    router.navigate(['/dashboard']);
    return false;
  };
};
