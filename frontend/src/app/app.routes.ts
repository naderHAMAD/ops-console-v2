import { Routes } from '@angular/router';
import { authGuard, roleGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./layout/shell/shell.component').then(m => m.ShellComponent),
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'jboss-update',
        canActivate: [roleGuard('jboss.update.trigger')],
        loadComponent: () =>
          import('./features/jboss-update/update-trigger/update-trigger.component')
            .then(m => m.UpdateTriggerComponent)
      },
      {
        path: 'vm',
        canActivate: [roleGuard('vm.create')],
        loadComponent: () =>
          import('./features/vm/vm-create-wizard/vm-create-wizard.component')
            .then(m => m.VmCreateWizardComponent)
      },
      {
        path: 'users',
        canActivate: [roleGuard('user.manage')],
        loadComponent: () =>
          import('./features/users/user-list/user-list.component').then(m => m.UserListComponent)
      },
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' }
    ]
  },
  { path: '**', redirectTo: '' }
];
