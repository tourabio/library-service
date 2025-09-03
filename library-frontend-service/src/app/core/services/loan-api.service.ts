import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

// Import backend-aligned DTOs
import { LoanTO, LoanRequestTO } from '../models';

@Injectable({
  providedIn: 'root'
})
export class LoanApiService {
  private readonly baseUrl = `${environment.apiUrl}/loans`;

  constructor(private http: HttpClient) {}

  // Loan CRUD operations using LoanTO and LoanRequestTO from backend
  getAllLoans(): Observable<LoanTO[]> {
    return this.http.get<LoanTO[]>(this.baseUrl).pipe(
      retry(2),
      catchError(this.handleError)
    );
  }

  getLoanById(id: number): Observable<LoanTO> {
    return this.http.get<LoanTO>(`${this.baseUrl}/${id}`).pipe(
      retry(2),
      catchError(this.handleError)
    );
  }

  createLoan(loan: LoanRequestTO): Observable<LoanTO> {
    return this.http.post<LoanTO>(this.baseUrl, loan).pipe(
      catchError(this.handleError)
    );
  }

  returnLoan(id: number): Observable<LoanTO> {
    return this.http.put<LoanTO>(`${this.baseUrl}/${id}/return`, {}).pipe(
      catchError(this.handleError)
    );
  }

  // Specialized loan queries
  getOverdueLoans(): Observable<LoanTO[]> {
    return this.http.get<LoanTO[]>(`${this.baseUrl}/overdue`).pipe(
      retry(2),
      catchError(this.handleError)
    );
  }

  getLoansByMemberId(memberId: number): Observable<LoanTO[]> {
    return this.http.get<LoanTO[]>(`${this.baseUrl}/member/${memberId}`).pipe(
      retry(2),
      catchError(this.handleError)
    );
  }

  getLoansByBookId(bookId: number): Observable<LoanTO[]> {
    return this.http.get<LoanTO[]>(`${this.baseUrl}/book/${bookId}`).pipe(
      retry(2),
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
          errorMessage = 'Bad Request: Invalid loan data provided';
          break;
        case 401:
          errorMessage = 'Unauthorized: Please log in';
          break;
        case 403:
          errorMessage = 'Forbidden: Insufficient permissions to manage loans';
          break;
        case 404:
          errorMessage = 'Not Found: Loan, book, or member does not exist';
          break;
        case 409:
          errorMessage = 'Conflict: Book not available or loan operation not allowed';
          break;
        case 500:
          errorMessage = 'Server Error: Please try again later';
          break;
        default:
          errorMessage = `Server Error: ${error.status} - ${error.message}`;
      }
    }

    console.error('Loan API Error:', error);
    return throwError(() => new Error(errorMessage));
  }
}