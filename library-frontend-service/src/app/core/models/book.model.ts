// Book entity matching backend Book.java
export interface Book {
  readonly id: number;
  readonly title: string;
  readonly author: string;
  readonly totalCopies: number;
  readonly availableCopies: number;
}

// BookTO matching backend BookTO record
export interface BookTO {
  readonly title: string;
  readonly author: string;
  readonly totalCopies: number;
  readonly availableCopies: number;
}

// Request DTOs for API communication
export interface CreateBookRequest {
  readonly title: string;
  readonly author: string;
  readonly totalCopies: number;
}

export interface UpdateBookRequest {
  readonly title?: string;
  readonly author?: string;
  readonly totalCopies?: number;
}

// Frontend-specific interfaces for enhanced functionality
export interface BookWithAvailability extends Book {
  readonly isAvailable: boolean;
  readonly availabilityStatus: 'available' | 'limited' | 'unavailable';
}

// Search and filtering (frontend-only, not backend supported yet)
export interface BookSearchCriteria {
  readonly title?: string;
  readonly author?: string;
  readonly availableOnly?: boolean;
}