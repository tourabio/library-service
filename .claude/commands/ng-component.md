---
allowed-tools: Bash(ng generate:*), Edit(src/**), Read
argument-hint: [component-name] [feature-module]
description: Generate new Angular component with library theme and proper structure
model: claude-sonnet-4-20250514
---
Generate a new Angular component following library management system standards:

**Component**: $1
**Feature Module**: $2

GENERATION PROCESS:
1. Create component using Angular CLI: `ng generate component features/$2/components/$1 --skip-tests=false`
2. Update component with OnPush change detection
3. Add proper TypeScript interfaces
4. Implement brown library theme styling
5. Create comprehensive unit tests
6. Add JSDoc documentation

BACKEND INTEGRATION:
- Use DTOs from core/models: BookTO, MemberTO, LoanTO
- Entity IDs as number type (Long backend → number frontend)
- LoanStatus enum: ACTIVE, RETURNED, OVERDUE only
- Date handling for LocalDate fields (ISO string to Date)

REQUIREMENTS:
- Must use ChangeDetectionStrategy.OnPush
- All @Input()/@Output() must have proper typing with backend DTOs
- SCSS must use theme variables from theme/variables.scss
- Template must include ARIA attributes
- Unit tests must achieve >80% coverage with DTO mocking
- Follow smart/dumb component pattern

THEME INTEGRATION:
- Use brown color palette (#3E2723, #5D4037, #8D6E63, #EFEBE9, #FFB300)
- Add library aesthetic elements (book spines, wooden textures)
- Implement smooth hover transitions
- Ensure WCAG 2.1 AA contrast compliance

QUARKUS BACKEND CONTEXT:
- Service injection for backend communication
- Loading states for API calls
- Error handling for backend exceptions
- Proper data transformation from DTOs