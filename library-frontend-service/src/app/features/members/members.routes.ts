import { Routes } from '@angular/router';

export const MEMBERS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./members.component').then(m => m.MembersComponent),
    title: 'Members - Library Frontend Service'
  }
];