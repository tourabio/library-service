import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-books',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="books-container">
      <h2>Books Management</h2>
      <p>Books feature coming soon...</p>
    </div>
  `,
  styles: [`
    .books-container {
      padding: 24px;
    }
  `]
})
export class BooksComponent {}