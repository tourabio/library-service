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

// AuthenticationTO for authentication responses (includes member ID)
export interface AuthenticationTO {
  readonly id: number;
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

// Frontend-specific interfaces for enhanced functionality
export interface MemberWithLoanInfo extends Member {
  readonly currentLoansCount?: number;
  readonly hasOverdueLoans?: boolean;
  readonly lastLoanDate?: string;
}

// Search and filtering (frontend-only)
export interface MemberSearchCriteria {
  readonly name?: string;
  readonly email?: string;
}