import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-loans',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="loans-container">
      <h2>Loans Management</h2>
      <p>Loans feature coming soon...</p>
    </div>
  `,
  styles: [`
    .loans-container {
      padding: 24px;
    }
  `]
})
export class LoansComponent {}