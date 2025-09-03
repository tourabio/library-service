import { Routes } from '@angular/router';

export const LOANS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () => import('./loans.component').then(m => m.LoansComponent),
    title: 'Loans - Library Frontend Service'
  }
];