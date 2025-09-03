import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { BookTO } from '../../../core/models';

export interface BookDetailsData {
  book: BookTO;
}

@Component({
  selector: 'app-book-details-modal',
  imports: [
    CommonModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatDividerModule
  ],
  templateUrl: './book-details-modal.html',
  styleUrl: './book-details-modal.scss'
})
export class BookDetailsModal {
  constructor(
    public dialogRef: MatDialogRef<BookDetailsModal>,
    @Inject(MAT_DIALOG_DATA) public data: BookDetailsData
  ) {}

  get book(): BookTO {
    return this.data.book;
  }

  getAvailabilityStatus(): 'high' | 'medium' | 'low' {
    const ratio = this.book.availableCopies / this.book.totalCopies;
    if (ratio > 0.6) return 'high';
    if (ratio > 0.3) return 'medium';
    return 'low';
  }

  getAvailabilityLabel(): string {
    const status = this.getAvailabilityStatus();
    switch (status) {
      case 'high': return 'Highly Available';
      case 'medium': return 'Limited Stock';
      case 'low': return 'Low Stock';
      default: return 'Available';
    }
  }

  onBorrowBook(): void {
    this.dialogRef.close({ action: 'borrow', book: this.book });
  }

  onClose(): void {
    this.dialogRef.close();
  }
}
