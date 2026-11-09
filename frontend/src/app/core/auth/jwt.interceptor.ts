import { HttpInterceptorFn, HttpRequest, HttpHandlerFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError, BehaviorSubject, filter, take } from 'rxjs';
import { AuthService } from './auth.service';

let isRefreshing = false;
const refreshedToken$ = new BehaviorSubject<string | null>(null);

function addToken(req: HttpRequest<unknown>, token: string | null) {
  return token ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }) : req;
}

export const jwtInterceptor: HttpInterceptorFn = (req, next: HttpHandlerFn) => {
  const auth = inject(AuthService);

  const isAuthEndpoint = req.url.includes('/auth/login')
    || req.url.includes('/auth/verify-otp')
    || req.url.includes('/auth/refresh');

  const token = isAuthEndpoint ? null : auth.getAccessToken();
  const authReq = addToken(req, token);

  return next(authReq).pipe(
    catchError(err => {
 const isAuthError = err.status === 401; // 403 = permission manquante, pas un problème de session

      if (!isAuthError || isAuthEndpoint) {
        return throwError(() => err);
      }

      // Évite de lancer plusieurs refresh en parallèle si plusieurs requêtes échouent en même temps
      if (!isRefreshing) {
        isRefreshing = true;
        refreshedToken$.next(null);

        return auth.refreshAccessToken().pipe(
          switchMap(response => {
            isRefreshing = false;
            refreshedToken$.next(response.accessToken);
            return next(addToken(req, response.accessToken));
          }),
          catchError(refreshErr => {
            isRefreshing = false;
            auth.logout();
            return throwError(() => refreshErr);
          })
        );
      }

      // Une requête refresh est déjà en cours : on attend son résultat puis on rejoue
      return refreshedToken$.pipe(
        filter(t => t !== null),
        take(1),
        switchMap(newToken => next(addToken(req, newToken)))
      );
    })
  );
};