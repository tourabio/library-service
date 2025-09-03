import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

// Import backend-aligned DTOs
import { BookTO } from '../models';

@Injectable({
  providedIn: 'root'
})
export class BookApiService {
  private readonly baseUrl = `${environment.apiUrl}/books`;

  constructor(private http: HttpClient) {}

  // Book CRUD operations using BookTO from backend
  getAllBooks(): Observable<BookTO[]> {
    return this.http.get<BookTO[]>(this.baseUrl).pipe(
      retry(2),
      catchError(this.handleError)
    );
  }

  getAvailableBooks(): Observable<BookTO[]> {
    return this.http.get<BookTO[]>(`${this.baseUrl}/available`).pipe(
      retry(2),
      catchError(this.handleError)
    );
  }

  getBookById(id: number): Observable<BookTO> {
    return this.http.get<BookTO>(`${this.baseUrl}/${id}`).pipe(
      retry(2),
      catchError(this.handleError)
    );
  }

  createBook(book: BookTO): Observable<BookTO> {
    return this.http.post<BookTO>(this.baseUrl, book).pipe(
      catchError(this.handleError)
    );
  }

  updateBook(id: number, book: BookTO): Observable<BookTO> {
    return this.http.put<BookTO>(`${this.baseUrl}/${id}`, book).pipe(
      catchError(this.handleError)
    );
  }

  deleteBook(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  // Error handling for Quarkus backend responses
  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unknown error occurred';

    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = `Client Error: ${error.error.message}`;
    } else {
      // Server-side error from Quarkus backend
      switch (error.status) {
        case 400:
          errorMessage = 'Bad Request: Invalid book data provided';
          break;
        case 401:
          errorMessage = 'Unauthorized: Please log in';
          break;
        case 403:
          errorMessage = 'Forbidden: Insufficient permissions to manage books';
          break;
        case 404:
          errorMessage = 'Not Found: Book does not exist';
          break;
        case 409:
          errorMessage = 'Conflict: Book already exists or operation not allowed';
          break;
        case 500:
          errorMessage = 'Server Error: Please try again later';
          break;
        default:
          errorMessage = `Server Error: ${error.status} - ${error.message}`;
      }
    }

    console.error('Book API Error:', error);
    return throwError(() => new Error(errorMessage));
  }
}