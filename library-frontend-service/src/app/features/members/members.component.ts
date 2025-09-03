import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-members',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="members-container">
      <h2>Members Management</h2>
      <p>Members feature coming soon...</p>
    </div>
  `,
  styles: [`
    .members-container {
      padding: 24px;
    }
  `]
})
export class MembersComponent {}