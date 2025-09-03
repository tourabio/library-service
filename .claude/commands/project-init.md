---
allowed-tools: Bash(ng new:*), Bash(npm install:*), Edit(src/**), Edit(*.json), Edit(*.md), Bash(ng generate:*), Bash(ng add:*), Bash(mkdir:*)
description: Initialize new Angular library management project with complete setup
---

Initialize a new Angular library management system project with all required configurations:

## PROJECT INITIALIZATION

### **1. Create Angular Project**
```bash
ng new library-frontend-service --routing --style=scss --skip-git --package-manager=npm
cd library-frontend-service
```

### **2. Install Required Dependencies**
```bash
# Core dependencies
npm install @angular/material @angular/cdk @angular/animations
npm install @angular/flex-layout
npm install rxjs

# Development dependencies
npm install --save-dev jest @types/jest jest-preset-angular
npm install --save-dev @angular-eslint/schematics
npm install --save-dev prettier eslint-config-prettier eslint-plugin-prettier
npm install --save-dev husky lint-staged

# Testing utilities
npm install --save-dev @angular/testing
```

### **3. Configure Jest Testing**
Create `jest.config.js`:
```javascript
const { pathsToModuleNameMapper } = require('ts-jest');
const { compilerOptions } = require('./tsconfig');

module.exports = {
  preset: 'jest-preset-angular',
  roots: ['<rootDir>/src/'],
  testMatch: ['**/+(*.)+(spec).+(ts)'],
  setupFilesAfterEnv: ['<rootDir>/src/test.ts'],
  collectCoverage: true,
  coverageReporters: ['html', 'text-summary', 'lcov'],
  coverageDirectory: 'coverage',
  collectCoverageFrom: [
    'src/**/*.ts',
    '!src/**/*.d.ts',
    '!src/**/*.module.ts',
    '!src/**/*.spec.ts',
    '!src/main.ts',
    '!src/polyfills.ts'
  ],
  coverageThreshold: {
    global: {
      statements: 80,
      branches: 75,
      functions: 85,
      lines: 80
    }
  },
  moduleNameMapper: pathsToModuleNameMapper(compilerOptions.paths || {}, {
    prefix: '<rootDir>/'
  })
};
```

### **4. Setup ESLint Configuration**
```bash
ng add @angular-eslint/schematics
```

Update `.eslintrc.json`:
```json
{
  "root": true,
  "ignorePatterns": ["projects/**/*"],
  "overrides": [
    {
      "files": ["*.ts"],
      "extends": [
        "@angular-eslint/recommended",
        "@angular-eslint/template/process-inline-templates",
        "@typescript-eslint/recommended",
        "prettier"
      ],
      "rules": {
        "@typescript-eslint/explicit-function-return-type": "error",
        "@typescript-eslint/no-explicit-any": "error",
        "@typescript-eslint/no-unused-vars": "error",
        "@angular-eslint/component-class-suffix": "error",
        "@angular-eslint/directive-class-suffix": "error",
        "@angular-eslint/no-input-rename": "error",
        "@angular-eslint/no-output-native": "error"
      }
    },
    {
      "files": ["*.html"],
      "extends": ["@angular-eslint/template/recommended", "@angular-eslint/template/accessibility"],
      "rules": {}
    }
  ]
}
```

### **5. Create Project Structure**
```bash
# Core directories
mkdir -p src/app/core/services
mkdir -p src/app/core/guards
mkdir -p src/app/core/interceptors
mkdir -p src/app/core/models
mkdir -p src/app/shared/components
mkdir -p src/app/shared/pipes
mkdir -p src/app/shared/directives
mkdir -p src/app/shared/utils
mkdir -p src/app/features/books
mkdir -p src/app/features/members
mkdir -p src/app/features/loans
mkdir -p src/app/features/dashboard
mkdir -p src/app/layout/header
mkdir -p src/app/layout/sidebar
mkdir -p src/app/layout/footer
mkdir -p src/app/theme

# Feature subdirectories
mkdir -p src/app/features/books/components
mkdir -p src/app/features/books/services
mkdir -p src/app/features/books/models
mkdir -p src/app/features/members/components
mkdir -p src/app/features/members/services
mkdir -p src/app/features/members/models
mkdir -p src/app/features/loans/components
mkdir -p src/app/features/loans/services
mkdir -p src/app/features/loans/models
mkdir -p src/app/features/dashboard/components
mkdir -p src/app/features/dashboard/services

# Testing directory
mkdir -p src/testing
```

### **6. Create Theme Files**
Create `src/app/theme/variables.scss`:
```scss
// Brown Library Theme Variables
$library-deep-brown: #3E2723;
$library-medium-brown: #5D4037;
$library-light-brown: #8D6E63;
$library-warm-beige: #EFEBE9;
$library-accent-gold: #FFB300;

// Semantic color mapping
$primary-color: $library-deep-brown;
$secondary-color: $library-medium-brown;
$accent-color: $library-accent-gold;
$background-color: $library-warm-beige;
$text-color: $library-deep-brown;

// Typography
$font-family-serif: 'Georgia', 'Times New Roman', serif;
$font-family-sans: 'Roboto', 'Arial', sans-serif;

// Spacing
$spacing-xs: 4px;
$spacing-sm: 8px;
$spacing-md: 16px;
$spacing-lg: 24px;
$spacing-xl: 32px;

// Breakpoints
$breakpoint-mobile: 768px;
$breakpoint-tablet: 1024px;
$breakpoint-desktop: 1200px;

// Shadows
$shadow-light: 0 2px 4px rgba($library-deep-brown, 0.1);
$shadow-medium: 0 4px 8px rgba($library-deep-brown, 0.15);
$shadow-heavy: 0 8px 16px rgba($library-deep-brown, 0.2);

// Borders
$border-radius: 8px;
$border-color: rgba($library-medium-brown, 0.3);
```

Create `src/app/theme/mixins.scss`:
```scss
@import './variables';

// Library-themed button mixin
@mixin library-button($variant: primary) {
  border: none;
  border-radius: $border-radius;
  padding: 12px 24px;
  font-family: $font-family-sans;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  
  @if $variant == primary {
    background-color: $library-medium-brown;
    color: $library-warm-beige;
    
    &:hover {
      background-color: $library-accent-gold;
      transform: translateY(-2px);
      box-shadow: $shadow-medium;
    }
  }
  
  @if $variant == secondary {
    background-color: transparent;
    color: $library-medium-brown;
    border: 2px solid $library-medium-brown;
    
    &:hover {
      background-color: $library-medium-brown;
      color: $library-warm-beige;
    }
  }
}

// Card component mixin
@mixin library-card {
  background-color: $library-warm-beige;
  border: 1px solid $border-color;
  border-radius: $border-radius;
  box-shadow: $shadow-light;
  padding: $spacing-lg;
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: $shadow-medium;
    transform: translateY(-2px);
  }
}

// Responsive breakpoint mixin
@mixin respond-to($breakpoint) {
  @if $breakpoint == mobile {
    @media (max-width: $breakpoint-mobile) {
      @content;
    }
  }
  @if $breakpoint == tablet {
    @media (max-width: $breakpoint-tablet) {
      @content;
    }
  }
  @if $breakpoint == desktop {
    @media (min-width: $breakpoint-desktop) {
      @content;
    }
  }
}
```

### **7. Update Package.json Scripts**
Update `package.json` scripts section:
```json
{
  "scripts": {
    "ng": "ng",
    "start": "ng serve",
    "build": "ng build",
    "build:prod": "ng build --configuration production",
    "test": "jest",
    "test:watch": "jest --watch",
    "test:coverage": "jest --coverage",
    "lint": "ng lint",
    "lint:fix": "ng lint --fix",
    "e2e": "ng e2e",
    "format": "prettier --write \"src/**/*.{ts,html,scss}\"",
    "format:check": "prettier --check \"src/**/*.{ts,html,scss}\"",
    "prepare": "husky install"
  }
}
```

### **8. Setup Environment Files**
Update `src/environments/environment.ts`:
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api', // Quarkus backend with /api base path
  version: '1.0.0'
};
```

Update `src/environments/environment.prod.ts`:
```typescript
export const environment = {
  production: true,
  apiUrl: '/api', // Production backend with /api base path
  version: '1.0.0'
};
```

### **9. Create Core Models**
Create `src/app/core/models/book.model.ts` (Backend-aligned DTOs):
```typescript
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
```

Create `src/app/core/models/member.model.ts` (Backend-aligned DTOs):
```typescript
// Member entity matching backend Member.java
export interface Member {
  readonly id: number;
  readonly name: string;
  readonly email: string;
}

// MemberTO matching backend MemberTO record
export interface MemberTO {
  readonly name: string;
  readonly email: string;
}

// Request DTOs for API communication
export interface CreateMemberRequest {
  readonly name: string;
  readonly email: string;
}

export interface UpdateMemberRequest {
  readonly name?: string;
  readonly email?: string;
}
```

Create `src/app/core/models/loan.model.ts` (Backend-aligned DTOs):
```typescript
import { BookTO } from './book.model';
import { MemberTO } from './member.model';

// Loan entity matching backend Loan.java
export interface Loan {
  readonly id: number;
  readonly bookId: number;
  readonly memberId: number;
  readonly loanDate: Date;
  readonly dueDate: Date;
  readonly returnDate?: Date;
  readonly status: LoanStatus;
}

// LoanTO matching backend LoanTO record
export interface LoanTO {
  readonly book: BookTO;
  readonly member: MemberTO;
  readonly loanDate: Date;
  readonly dueDate: Date;
  readonly returnDate?: Date;
  readonly status: LoanStatus;
}

// LoanRequestTO matching backend LoanRequestTO record
export interface LoanRequestTO {
  readonly bookId: number;
  readonly memberId: number;
}

// LoanStatus enum matching backend LoanStatus.java
export enum LoanStatus {
  ACTIVE = 'ACTIVE',
  RETURNED = 'RETURNED',
  OVERDUE = 'OVERDUE'
}

// Request DTOs for API communication
export interface CreateLoanRequest {
  readonly bookId: number;
  readonly memberId: number;
}

export interface ReturnLoanRequest {
  readonly loanId: number;
  readonly returnDate: Date;
}
```

Create `src/app/core/models/index.ts`:
```typescript
export * from './book.model';
export * from './member.model';
export * from './loan.model';
```

### **10. Setup Git Hooks**
```bash
npx husky install
npx husky add .husky/pre-commit "npx lint-staged"
```

Create `.lintstagedrc.json`:
```json
{
  "src/**/*.{ts,html}": ["eslint --fix"],
  "src/**/*.{ts,html,scss,css,json,md}": ["prettier --write"],
  "src/**/*.ts": ["jest --bail --findRelatedTests --passWithNoTests"]
}
```

### **11. Create Initial Modules**
```bash
ng generate module core --flat
ng generate module shared --flat
ng generate module features/books --route books --module app
ng generate module features/members --route members --module app
ng generate module features/loans --route loans --module app
ng generate module features/dashboard --route dashboard --module app
```

### **12. Update Global Styles**
Update `src/styles.scss`:
```scss
@import './app/theme/variables';
@import './app/theme/mixins';

/* Global Styles */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: $font-family-sans;
  background-color: $background-color;
  color: $text-color;
  line-height: 1.6;
}

h1, h2, h3, h4, h5, h6 {
  font-family: $font-family-serif;
  color: $library-deep-brown;
  margin-bottom: $spacing-md;
}

a {
  color: $library-medium-brown;
  text-decoration: none;
  transition: color 0.3s ease;
  
  &:hover {
    color: $library-accent-gold;
  }
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: $spacing-lg;
}

.btn {
  @include library-button();
  
  &.btn-secondary {
    @include library-button(secondary);
  }
}

.card {
  @include library-card();
}

/* Utility Classes */
.text-center { text-align: center; }
.text-left { text-align: left; }
.text-right { text-align: right; }

.mb-xs { margin-bottom: $spacing-xs; }
.mb-sm { margin-bottom: $spacing-sm; }
.mb-md { margin-bottom: $spacing-md; }
.mb-lg { margin-bottom: $spacing-lg; }
.mb-xl { margin-bottom: $spacing-xl; }

.p-xs { padding: $spacing-xs; }
.p-sm { padding: $spacing-sm; }
.p-md { padding: $spacing-md; }
.p-lg { padding: $spacing-lg; }
.p-xl { padding: $spacing-xl; }
```

## VALIDATION CHECKLIST

### **Project Setup Verification**
- ✅ Angular CLI version 18+
- ✅ TypeScript strict mode enabled
- ✅ Jest testing configured
- ✅ ESLint with Angular rules
- ✅ Prettier formatting setup
- ✅ Git hooks configured
- ✅ Brown theme variables created
- ✅ Feature modules structure
- ✅ Environment configuration

### **Development Environment Ready**
- ✅ `npm start` runs successfully
- ✅ `npm run test` executes
- ✅ `npm run lint` validates code
- ✅ `npm run build` creates production bundle
- ✅ Git pre-commit hooks functional

### **Next Steps**
1. Implement core services (BookService, MemberService, LoanService)
2. Create shared UI components with brown theme
3. Build feature components with proper TypeScript typing
4. Add comprehensive unit tests
5. Implement responsive design
6. Add accessibility features

Your Angular library management frontend is now initialized with:
- 🎨 Brown library theme setup
- 🧪 Jest testing configuration
- 📝 ESLint strict validation  
- 🏗️ Feature-based architecture
- 🔧 Development tools configured
- 📱 Responsive design foundation