import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

// Import backend-aligned DTOs
import { MemberTO } from '../models';

@Injectable({
  providedIn: 'root'
})
export class MemberApiService {
  private readonly baseUrl = `${environment.apiUrl}/members`;

  constructor(private http: HttpClient) {}

  // Member CRUD operations using MemberTO from backend
  getAllMembers(): Observable<MemberTO[]> {
    return this.http.get<MemberTO[]>(this.baseUrl).pipe(
      retry(2),
      catchError(this.handleError)
    );
  }

  getMemberById(id: number): Observable<MemberTO> {
    return this.http.get<MemberTO>(`${this.baseUrl}/${id}`).pipe(
      retry(2),
      catchError(this.handleError)
    );
  }

  createMember(member: MemberTO): Observable<MemberTO> {
    return this.http.post<MemberTO>(this.baseUrl, member).pipe(
      catchError(this.handleError)
    );
  }

  updateMember(id: number, member: MemberTO): Observable<MemberTO> {
    return this.http.put<MemberTO>(`${this.baseUrl}/${id}`, member).pipe(
      catchError(this.handleError)
    );
  }

  deleteMember(id: number): Observable<void> {
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
          errorMessage = 'Bad Request: Invalid member data provided';
          break;
        case 401:
          errorMessage = 'Unauthorized: Please log in';
          break;
        case 403:
          errorMessage = 'Forbidden: Insufficient permissions to manage members';
          break;
        case 404:
          errorMessage = 'Not Found: Member does not exist';
          break;
        case 409:
          errorMessage = 'Conflict: Member already exists (email must be unique)';
          break;
        case 500:
          errorMessage = 'Server Error: Please try again later';
          break;
        default:
          errorMessage = `Server Error: ${error.status} - ${error.message}`;
      }
    }

    console.error('Member API Error:', error);
    return throwError(() => new Error(errorMessage));
  }
}