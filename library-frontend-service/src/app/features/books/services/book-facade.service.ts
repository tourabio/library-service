import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject, combineLatest } from 'rxjs';
import { map, shareReplay, tap } from 'rxjs/operators';

import { BookApiService } from '../../../core/services/book-api.service';
import { BookTO, BookWithAvailability, SearchFilters } from '../../../core/models/api.types';

@Injectable({
  providedIn: 'root'
})
export class BookFacadeService {
  private booksSubject = new BehaviorSubject<BookTO[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);
  private searchFiltersSubject = new BehaviorSubject<SearchFilters>({});

  public books$ = this.booksSubject.asObservable();
  public loading$ = this.loadingSubject.asObservable();
  public searchFilters$ = this.searchFiltersSubject.asObservable();

  // Computed observables
  public filteredBooks$ = combineLatest([
    this.books$,
    this.searchFilters$
  ]).pipe(
    map(([books, filters]) => this.applyFilters(books, filters)),
    shareReplay(1)
  );

  public booksWithAvailability$ = this.filteredBooks$.pipe(
    map(books => books.map(book => this.enhanceBookWithAvailability(book))),
    shareReplay(1)
  );

  constructor(private bookApiService: BookApiService) {}

  // Load all books from Quarkus backend
  loadBooks(): void {
    this.loadingSubject.next(true);
    
    this.bookApiService.getAllBooks().pipe(
      tap(() => this.loadingSubject.next(false))
    ).subscribe({
      next: (books) => this.booksSubject.next(books),
      error: (error) => {
        this.loadingSubject.next(false);
        console.error('Failed to load books:', error);
      }
    });
  }

  // Create new book using BookTO
  createBook(bookRequest: BookTO): Observable<BookTO> {
    this.loadingSubject.next(true);
    
    return this.bookApiService.createBook(bookRequest).pipe(
      tap(() => {
        this.loadingSubject.next(false);
        this.loadBooks(); // Refresh list
      })
    );
  }

  // Update search filters
  updateSearchFilters(filters: Partial<SearchFilters>): void {
    const currentFilters = this.searchFiltersSubject.value;
    this.searchFiltersSubject.next({ ...currentFilters, ...filters });
  }

  // Clear search filters
  clearSearchFilters(): void {
    this.searchFiltersSubject.next({});
  }

  // Private methods for filtering and enhancement
  private applyFilters(books: BookTO[], filters: SearchFilters): BookTO[] {
    let filtered = [...books];

    if (filters.query) {
      const query = filters.query.toLowerCase();
      filtered = filtered.filter(book => 
        book.title?.toLowerCase().includes(query) || 
        book.author?.toLowerCase().includes(query)
      );
    }

    if (filters.author) {
      filtered = filtered.filter(book => 
        book.author?.toLowerCase().includes(filters.author!.toLowerCase())
      );
    }

    if (filters.availability && filters.availability !== 'all') {
      filtered = filtered.filter(book => {
        const available = (book.availableCopies || 0) > 0;
        return filters.availability === 'available' ? available : !available;
      });
    }

    // Apply sorting
    if (filters.sortBy) {
      filtered.sort((a, b) => {
        const aVal = a[filters.sortBy as keyof BookTO] as string || '';
        const bVal = b[filters.sortBy as keyof BookTO] as string || '';
        const comparison = aVal.localeCompare(bVal);
        return filters.sortOrder === 'desc' ? -comparison : comparison;
      });
    }

    return filtered;
  }

  private enhanceBookWithAvailability(book: BookTO): BookWithAvailability {
    const available = (book.availableCopies || 0);
    const total = (book.totalCopies || 0);
    
    let availabilityStatus: 'available' | 'limited' | 'unavailable';
    
    if (available === 0) {
      availabilityStatus = 'unavailable';
    } else if (available <= total * 0.2) {
      availabilityStatus = 'limited';
    } else {
      availabilityStatus = 'available';
    }

    return {
      ...book,
      isAvailable: available > 0,
      availabilityStatus
    };
  }
}