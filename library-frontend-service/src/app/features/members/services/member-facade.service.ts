import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject, combineLatest } from 'rxjs';
import { map, shareReplay, tap } from 'rxjs/operators';

import { MemberApiService } from '../../../core/services/member-api.service';
import { MemberTO, MemberWithStats } from '../../../core/models/api.types';

export interface MemberSearchFilters {
  readonly name?: string;
  readonly email?: string;
  readonly sortBy?: 'name' | 'email';
  readonly sortOrder?: 'asc' | 'desc';
}

@Injectable({
  providedIn: 'root'
})
export class MemberFacadeService {
  private membersSubject = new BehaviorSubject<MemberTO[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);
  private searchFiltersSubject = new BehaviorSubject<MemberSearchFilters>({});

  public members$ = this.membersSubject.asObservable();
  public loading$ = this.loadingSubject.asObservable();
  public searchFilters$ = this.searchFiltersSubject.asObservable();

  // Computed observables
  public filteredMembers$ = combineLatest([
    this.members$,
    this.searchFilters$
  ]).pipe(
    map(([members, filters]) => this.applyFilters(members, filters)),
    shareReplay(1)
  );

  constructor(private memberApiService: MemberApiService) {}

  // Load all members from Quarkus backend
  loadMembers(): void {
    this.loadingSubject.next(true);
    
    this.memberApiService.getAllMembers().pipe(
      tap(() => this.loadingSubject.next(false))
    ).subscribe({
      next: (members) => this.membersSubject.next(members),
      error: (error) => {
        this.loadingSubject.next(false);
        console.error('Failed to load members:', error);
      }
    });
  }

  // Get member by ID
  getMemberById(id: number): Observable<MemberTO> {
    this.loadingSubject.next(true);
    
    return this.memberApiService.getMemberById(id).pipe(
      tap(() => this.loadingSubject.next(false))
    );
  }

  // Create new member using MemberTO
  createMember(memberRequest: MemberTO): Observable<MemberTO> {
    this.loadingSubject.next(true);
    
    return this.memberApiService.createMember(memberRequest).pipe(
      tap(() => {
        this.loadingSubject.next(false);
        this.loadMembers(); // Refresh list
      })
    );
  }

  // Update existing member
  updateMember(id: number, memberRequest: MemberTO): Observable<MemberTO> {
    this.loadingSubject.next(true);
    
    return this.memberApiService.updateMember(id, memberRequest).pipe(
      tap(() => {
        this.loadingSubject.next(false);
        this.loadMembers(); // Refresh list
      })
    );
  }

  // Delete member
  deleteMember(id: number): Observable<void> {
    this.loadingSubject.next(true);
    
    return this.memberApiService.deleteMember(id).pipe(
      tap(() => {
        this.loadingSubject.next(false);
        this.loadMembers(); // Refresh list
      })
    );
  }

  // Update search filters
  updateSearchFilters(filters: Partial<MemberSearchFilters>): void {
    const currentFilters = this.searchFiltersSubject.value;
    this.searchFiltersSubject.next({ ...currentFilters, ...filters });
  }

  // Clear search filters
  clearSearchFilters(): void {
    this.searchFiltersSubject.next({});
  }

  // Private methods for filtering
  private applyFilters(members: MemberTO[], filters: MemberSearchFilters): MemberTO[] {
    let filtered = [...members];

    if (filters.name) {
      const name = filters.name.toLowerCase();
      filtered = filtered.filter(member => 
        member.name?.toLowerCase().includes(name)
      );
    }

    if (filters.email) {
      const email = filters.email.toLowerCase();
      filtered = filtered.filter(member => 
        member.email?.toLowerCase().includes(email)
      );
    }

    // Apply sorting
    if (filters.sortBy) {
      filtered.sort((a, b) => {
        const aVal = a[filters.sortBy as keyof MemberTO] as string || '';
        const bVal = b[filters.sortBy as keyof MemberTO] as string || '';
        const comparison = aVal.localeCompare(bVal);
        return filters.sortOrder === 'desc' ? -comparison : comparison;
      });
    }

    return filtered;
  }
}