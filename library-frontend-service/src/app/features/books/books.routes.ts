import { Routes } from '@angular/router';

export const BOOKS_ROUTES: Routes = [
  {
    path: '',
    redirectTo: 'available',
    pathMatch: 'full'
  },
  {
    path: 'available',
    loadComponent: () => import('./available-books/available-books').then(m => m.AvailableBooks),
    title: 'Available Books - Library Frontend Service'
  },
  {
    path: 'all',
    loadComponent: () => import('./books.component').then(m => m.BooksComponent),
    title: 'All Books - Library Frontend Service'
  }
];