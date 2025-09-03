import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/auth/login',
    pathMatch: 'full'
  },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  {
    path: '',
    loadComponent: () => import('./core/layout/main-layout/main-layout.component').then(m => m.MainLayoutComponent),
    children: [
      {
        path: 'dashboard',
        loadComponent: () => import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent),
        title: 'Dashboard - Library Frontend Service'
      },
      {
        path: 'books',
        loadChildren: () => import('./features/books/books.routes').then(m => m.BOOKS_ROUTES),
        title: 'Books - Library Frontend Service'
      },
      {
        path: 'members',
        loadChildren: () => import('./features/members/members.routes').then(m => m.MEMBERS_ROUTES),
        title: 'Members - Library Frontend Service'
      },
      {
        path: 'loans',
        loadChildren: () => import('./features/loans/loans.routes').then(m => m.LOANS_ROUTES),
        title: 'Loans - Library Frontend Service'
      }
    ]
  },
  {
    path: '404',
    loadComponent: () => import('./shared/components/not-found/not-found.component').then(m => m.NotFoundComponent),
    title: '404 - Page Not Found | Library Frontend Service'
  },
  {
    path: '**',
    redirectTo: '/404'
  }
];
