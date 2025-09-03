import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject, combineLatest } from 'rxjs';
import { map, shareReplay, tap } from 'rxjs/operators';

import { LoanApiService } from '../../../core/services/loan-api.service';
import { LoanTO, LoanRequestTO, LoanWithDetails, LoanStatus } from '../../../core/models/api.types';

export interface LoanSearchFilters {
  readonly memberId?: number;
  readonly bookId?: number;
  readonly status?: LoanStatus;
  readonly isOverdue?: boolean;
  readonly sortBy?: 'loanDate' | 'dueDate' | 'status';
  readonly sortOrder?: 'asc' | 'desc';
}

@Injectable({
  providedIn: 'root'
})
export class LoanFacadeService {
  private loansSubject = new BehaviorSubject<LoanTO[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);
  private searchFiltersSubject = new BehaviorSubject<LoanSearchFilters>({});

  public loans$ = this.loansSubject.asObservable();
  public loading$ = this.loadingSubject.asObservable();
  public searchFilters$ = this.searchFiltersSubject.asObservable();

  // Computed observables
  public filteredLoans$ = combineLatest([
    this.loans$,
    this.searchFilters$
  ]).pipe(
    map(([loans, filters]) => this.applyFilters(loans, filters)),
    shareReplay(1)
  );

  public loansWithDetails$ = this.filteredLoans$.pipe(
    map(loans => loans.map(loan => this.enhanceLoanWithDetails(loan))),
    shareReplay(1)
  );

  public overdueLoans$ = this.loans$.pipe(
    map(loans => loans.filter(loan => this.isLoanOverdue(loan))),
    shareReplay(1)
  );

  constructor(private loanApiService: LoanApiService) {}

  // Load all loans from Quarkus backend
  loadLoans(): void {
    this.loadingSubject.next(true);
    
    this.loanApiService.getAllLoans().pipe(
      tap(() => this.loadingSubject.next(false))
    ).subscribe({
      next: (loans) => this.loansSubject.next(loans),
      error: (error) => {
        this.loadingSubject.next(false);
        console.error('Failed to load loans:', error);
      }
    });
  }

  // Load overdue loans specifically
  loadOverdueLoans(): void {
    this.loadingSubject.next(true);
    
    this.loanApiService.getOverdueLoans().pipe(
      tap(() => this.loadingSubject.next(false))
    ).subscribe({
      next: (loans) => this.loansSubject.next(loans),
      error: (error) => {
        this.loadingSubject.next(false);
        console.error('Failed to load overdue loans:', error);
      }
    });
  }

  // Get loan by ID
  getLoanById(id: number): Observable<LoanTO> {
    this.loadingSubject.next(true);
    
    return this.loanApiService.getLoanById(id).pipe(
      tap(() => this.loadingSubject.next(false))
    );
  }

  // Create new loan (checkout book)
  createLoan(loanRequest: LoanRequestTO): Observable<LoanTO> {
    this.loadingSubject.next(true);
    
    return this.loanApiService.createLoan(loanRequest).pipe(
      tap(() => {
        this.loadingSubject.next(false);
        this.loadLoans(); // Refresh list
      })
    );
  }

  // Return book (complete loan)
  returnLoan(id: number): Observable<LoanTO> {
    this.loadingSubject.next(true);
    
    return this.loanApiService.returnLoan(id).pipe(
      tap(() => {
        this.loadingSubject.next(false);
        this.loadLoans(); // Refresh list
      })
    );
  }

  // Get loans by member ID
  getLoansByMember(memberId: number): Observable<LoanTO[]> {
    this.loadingSubject.next(true);
    
    return this.loanApiService.getLoansByMemberId(memberId).pipe(
      tap(() => this.loadingSubject.next(false))
    );
  }

  // Get loans by book ID
  getLoansByBook(bookId: number): Observable<LoanTO[]> {
    this.loadingSubject.next(true);
    
    return this.loanApiService.getLoansByBookId(bookId).pipe(
      tap(() => this.loadingSubject.next(false))
    );
  }

  // Update search filters
  updateSearchFilters(filters: Partial<LoanSearchFilters>): void {
    const currentFilters = this.searchFiltersSubject.value;
    this.searchFiltersSubject.next({ ...currentFilters, ...filters });
  }

  // Clear search filters
  clearSearchFilters(): void {
    this.searchFiltersSubject.next({});
  }

  // Private methods for filtering and enhancement
  private applyFilters(loans: LoanTO[], filters: LoanSearchFilters): LoanTO[] {
    let filtered = [...loans];

    if (filters.memberId) {
      // Note: LoanTO has nested member object, not memberId directly
      filtered = filtered.filter(loan => 
        // This would need to be implemented based on actual LoanTO structure
        true // Placeholder
      );
    }

    if (filters.status) {
      filtered = filtered.filter(loan => loan.status === filters.status);
    }

    if (filters.isOverdue) {
      filtered = filtered.filter(loan => this.isLoanOverdue(loan));
    }

    // Apply sorting
    if (filters.sortBy) {
      filtered.sort((a, b) => {
        let aVal: any;
        let bVal: any;

        switch (filters.sortBy) {
          case 'loanDate':
            aVal = new Date(a.loanDate).getTime();
            bVal = new Date(b.loanDate).getTime();
            break;
          case 'dueDate':
            aVal = new Date(a.dueDate).getTime();
            bVal = new Date(b.dueDate).getTime();
            break;
          case 'status':
            aVal = a.status;
            bVal = b.status;
            break;
          default:
            aVal = 0;
            bVal = 0;
        }

        const comparison = aVal < bVal ? -1 : aVal > bVal ? 1 : 0;
        return filters.sortOrder === 'desc' ? -comparison : comparison;
      });
    }

    return filtered;
  }

  private enhanceLoanWithDetails(loan: LoanTO): LoanWithDetails {
    const now = new Date();
    const dueDate = new Date(loan.dueDate);
    const isOverdue = loan.status === LoanStatus.ACTIVE && now > dueDate;
    const daysUntilDue = Math.ceil((dueDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24));

    return {
      ...loan,
      isOverdue,
      daysUntilDue
    };
  }

  private isLoanOverdue(loan: LoanTO): boolean {
    const now = new Date();
    const dueDate = new Date(loan.dueDate);
    return loan.status === LoanStatus.ACTIVE && now > dueDate;
  }
}