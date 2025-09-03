import { Injectable, inject } from '@angular/core';
import { Observable, BehaviorSubject, tap, catchError } from 'rxjs';
import { BookApiService } from './book-api.service';
import {
  BookTO,
  CreateBookRequest,
  UpdateBookRequest,
  BookSearchCriteria
} from '../models';

@Injectable({
  providedIn: 'root'
})
export class BookService {
  private readonly bookApiService = inject(BookApiService);
  
  private readonly _books$ = new BehaviorSubject<BookTO[]>([]);
  private readonly _loading$ = new BehaviorSubject<boolean>(false);
  private readonly _error$ = new BehaviorSubject<string | null>(null);

  // Public observables
  readonly books$ = this._books$.asObservable();
  readonly loading$ = this._loading$.asObservable();
  readonly error$ = this._error$.asObservable();

  searchBooks(criteria: BookSearchCriteria): Observable<BookTO[]> {
    this.setLoading(true);
    this.setError(null);

    // Note: Search functionality would need to be implemented on backend
    // For now, get all books and filter on frontend
    return this.bookApiService.getAllBooks()
      .pipe(
        tap(() => {
          this.setLoading(false);
        }),
        catchError(error => {
          this.handleError('Failed to search books', error);
          throw error;
        })
      );
  }

  getAllBooks(): Observable<BookTO[]> {
    this.setLoading(true);
    this.setError(null);

    return this.bookApiService.getAllBooks()
      .pipe(
        tap(books => {
          this._books$.next(books);
          this.setLoading(false);
        }),
        catchError(error => {
          this.handleError('Failed to fetch books', error);
          throw error;
        })
      );
  }

  getBookById(id: number): Observable<BookTO> {
    this.setLoading(true);
    this.setError(null);

    return this.bookApiService.getBookById(id)
      .pipe(
        tap(() => this.setLoading(false)),
        catchError(error => {
          this.handleError(`Failed to fetch book with ID: ${id}`, error);
          throw error;
        })
      );
  }

  createBook(book: CreateBookRequest): Observable<BookTO> {
    this.setLoading(true);
    this.setError(null);

    // Convert CreateBookRequest to BookTO
    const bookTO: BookTO = {
      title: book.title,
      author: book.author,
      totalCopies: book.totalCopies,
      availableCopies: book.totalCopies // Initially all copies are available
    };

    return this.bookApiService.createBook(bookTO)
      .pipe(
        tap(newBook => {
          const currentBooks = this._books$.value;
          this._books$.next([...currentBooks, newBook]);
          this.setLoading(false);
        }),
        catchError(error => {
          this.handleError('Failed to create book', error);
          throw error;
        })
      );
  }

  updateBook(id: number, book: UpdateBookRequest): Observable<BookTO> {
    this.setLoading(true);
    this.setError(null);

    // For update, we need the full BookTO - this would typically come from the current book
    // This is a simplified implementation
    const bookTO: BookTO = {
      title: book.title || '',
      author: book.author || '',
      totalCopies: book.totalCopies || 0,
      availableCopies: book.totalCopies || 0
    };

    return this.bookApiService.updateBook(id, bookTO)
      .pipe(
        tap(updatedBook => {
          const currentBooks = this._books$.value;
          const index = currentBooks.findIndex(b => 
            // Since BookTO doesn't have id, we need a different way to match
            b.title === updatedBook.title && b.author === updatedBook.author
          );
          if (index !== -1) {
            const newBooks = [...currentBooks];
            newBooks[index] = updatedBook;
            this._books$.next(newBooks);
          }
          this.setLoading(false);
        }),
        catchError(error => {
          this.handleError(`Failed to update book with ID: ${id}`, error);
          throw error;
        })
      );
  }

  deleteBook(id: number): Observable<void> {
    this.setLoading(true);
    this.setError(null);

    return this.bookApiService.deleteBook(id)
      .pipe(
        tap(() => {
          // For BookTO without id, we can't easily filter by id
          // This would need to be handled differently in a real implementation
          this.setLoading(false);
        }),
        catchError(error => {
          this.handleError(`Failed to delete book with ID: ${id}`, error);
          throw error;
        })
      );
  }


  private setLoading(loading: boolean): void {
    this._loading$.next(loading);
  }

  private setError(error: string | null): void {
    this._error$.next(error);
  }

  private handleError(message: string, error: unknown): void {
    console.error(message, error);
    this.setError(message);
    this.setLoading(false);
  }
}