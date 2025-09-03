import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog } from '@angular/material/dialog';
import { BookApiService } from '../../../core/services/book-api.service';
import { BookTO } from '../../../core/models';
import { BookDetailsModal } from '../book-details-modal/book-details-modal';

@Component({
  selector: 'app-available-books',
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatChipsModule
  ],
  templateUrl: './available-books.html',
  styleUrl: './available-books.scss'
})
export class AvailableBooks implements OnInit {
  books = signal<BookTO[]>([]);
  loading = signal<boolean>(true);
  error = signal<string | null>(null);

  constructor(
    private bookApiService: BookApiService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadAvailableBooks();
  }

  private loadAvailableBooks(): void {
    this.loading.set(true);
    this.error.set(null);

    this.bookApiService.getAvailableBooks().subscribe({
      next: (books: BookTO[]) => {
        this.books.set(books);
        this.loading.set(false);
      },
      error: (error) => {
        this.error.set('Failed to load available books. Please try again.');
        this.loading.set(false);
        console.error('Error loading available books:', error);
      }
    });
  }

  onBorrowBook(book: BookTO): void {
    console.log('Borrow book:', book);
  }

  onViewDetails(book: BookTO): void {
    const dialogRef = this.dialog.open(BookDetailsModal, {
      width: '800px',
      maxWidth: '90vw',
      maxHeight: '90vh',
      panelClass: 'book-details-dialog',
      data: { book }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result?.action === 'borrow') {
        this.onBorrowBook(result.book);
      }
    });
  }

  getAvailabilityStatus(book: BookTO): 'high' | 'medium' | 'low' {
    const ratio = book.availableCopies / book.totalCopies;
    if (ratio > 0.6) return 'high';
    if (ratio > 0.3) return 'medium';
    return 'low';
  }

  retryLoading(): void {
    this.loadAvailableBooks();
  }

  trackByTitle(index: number, book: BookTO): string {
    return book.title;
  }
}
