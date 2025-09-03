import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule],
  template: `
    <div class="dashboard-container">
      <header class="dashboard-header">
        <h1>Library Frontend Service</h1>
        <p>Welcome to your modern library dashboard</p>
      </header>

      <div class="dashboard-grid">
        <mat-card class="dashboard-card">
          <mat-card-content>
            <div class="card-icon books">
              <mat-icon>book</mat-icon>
            </div>
            <h3>Books</h3>
            <p class="card-number">1,247</p>
            <p class="card-description">Total books in collection</p>
          </mat-card-content>
          <mat-card-actions>
            <button mat-raised-button color="primary">Manage Books</button>
          </mat-card-actions>
        </mat-card>

        <mat-card class="dashboard-card">
          <mat-card-content>
            <div class="card-icon members">
              <mat-icon>people</mat-icon>
            </div>
            <h3>Members</h3>
            <p class="card-number">325</p>
            <p class="card-description">Active library members</p>
          </mat-card-content>
          <mat-card-actions>
            <button mat-raised-button color="primary">Manage Members</button>
          </mat-card-actions>
        </mat-card>

        <mat-card class="dashboard-card">
          <mat-card-content>
            <div class="card-icon loans">
              <mat-icon>assignment</mat-icon>
            </div>
            <h3>Active Loans</h3>
            <p class="card-number">89</p>
            <p class="card-description">Currently borrowed books</p>
          </mat-card-content>
          <mat-card-actions>
            <button mat-raised-button color="primary">Manage Loans</button>
          </mat-card-actions>
        </mat-card>

        <mat-card class="dashboard-card">
          <mat-card-content>
            <div class="card-icon overdue">
              <mat-icon>schedule</mat-icon>
            </div>
            <h3>Overdue</h3>
            <p class="card-number">12</p>
            <p class="card-description">Books past due date</p>
          </mat-card-content>
          <mat-card-actions>
            <button mat-raised-button color="warn">View Overdue</button>
          </mat-card-actions>
        </mat-card>
      </div>

      <div class="recent-activity">
        <mat-card>
          <mat-card-header>
            <mat-card-title>Recent Activity</mat-card-title>
          </mat-card-header>
          <mat-card-content>
            <div class="activity-list">
              <div class="activity-item">
                <mat-icon>book</mat-icon>
                <div class="activity-content">
                  <p><strong>The Great Gatsby</strong> was borrowed by John Smith</p>
                  <span class="activity-time">2 minutes ago</span>
                </div>
              </div>
              <div class="activity-item">
                <mat-icon>person_add</mat-icon>
                <div class="activity-content">
                  <p>New member <strong>Sarah Johnson</strong> registered</p>
                  <span class="activity-time">15 minutes ago</span>
                </div>
              </div>
              <div class="activity-item">
                <mat-icon>assignment_return</mat-icon>
                <div class="activity-content">
                  <p><strong>1984</strong> was returned by Mike Wilson</p>
                  <span class="activity-time">1 hour ago</span>
                </div>
              </div>
            </div>
          </mat-card-content>
        </mat-card>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-container {
      padding: 24px;
      background: linear-gradient(135deg, #EFEBE9 0%, #F5F5F5 100%);
      min-height: 100vh;
    }

    .dashboard-header {
      text-align: center;
      margin-bottom: 32px;
    }

    .dashboard-header h1 {
      color: #3E2723;
      font-size: 2.5rem;
      margin-bottom: 8px;
    }

    .dashboard-header p {
      color: #5D4037;
      font-size: 1.1rem;
      opacity: 0.8;
    }

    .dashboard-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
      gap: 24px;
      margin-bottom: 32px;
    }

    .dashboard-card {
      transition: transform 0.3s ease, box-shadow 0.3s ease;
      border-radius: 12px;
    }

    .dashboard-card:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 25px rgba(62, 39, 35, 0.15);
    }

    .card-icon {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 16px;
    }

    .card-icon mat-icon {
      font-size: 28px;
      width: 28px;
      height: 28px;
      color: white;
    }

    .card-icon.books {
      background: linear-gradient(135deg, #5D4037, #3E2723);
    }

    .card-icon.members {
      background: linear-gradient(135deg, #FFB300, #FF8F00);
    }

    .card-icon.loans {
      background: linear-gradient(135deg, #388E3C, #2E7D32);
    }

    .card-icon.overdue {
      background: linear-gradient(135deg, #D32F2F, #C62828);
    }

    .card-number {
      font-size: 2.5rem;
      font-weight: 700;
      color: #3E2723;
      margin: 8px 0;
    }

    .card-description {
      color: #8D6E63;
      font-size: 0.9rem;
      margin-bottom: 16px;
    }

    .recent-activity {
      max-width: 800px;
      margin: 0 auto;
    }

    .activity-list {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }

    .activity-item {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 12px;
      border-radius: 8px;
      background: rgba(239, 235, 233, 0.3);
    }

    .activity-item mat-icon {
      color: #5D4037;
      width: 24px;
      height: 24px;
      font-size: 24px;
    }

    .activity-content {
      flex: 1;
    }

    .activity-content p {
      margin: 0 0 4px 0;
      color: #3E2723;
    }

    .activity-time {
      font-size: 0.85rem;
      color: #8D6E63;
    }

    @media (max-width: 768px) {
      .dashboard-container {
        padding: 16px;
      }

      .dashboard-grid {
        grid-template-columns: 1fr;
        gap: 16px;
      }

      .dashboard-header h1 {
        font-size: 2rem;
      }
    }
  `]
})
export class DashboardComponent {}