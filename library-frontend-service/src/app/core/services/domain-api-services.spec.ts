import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BookApiService } from './book-api.service';
import { MemberApiService } from './member-api.service';
import { LoanApiService } from './loan-api.service';
import { BookTO, MemberTO, LoanTO, LoanRequestTO } from '../models';

describe('Domain API Services', () => {
  let bookService: BookApiService;
  let memberService: MemberApiService;
  let loanService: LoanApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BookApiService, MemberApiService, LoanApiService]
    });

    bookService = TestBed.inject(BookApiService);
    memberService = TestBed.inject(MemberApiService);
    loanService = TestBed.inject(LoanApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('Book Operations', () => {
    it('should fetch all books', () => {
      const mockBooks: BookTO[] = [
        { title: 'Test Book 1', author: 'Author 1', totalCopies: 5, availableCopies: 3 }
      ];

      bookService.getAllBooks().subscribe(books => {
        expect(books).toEqual(mockBooks);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/books');
      expect(req.request.method).toBe('GET');
      req.flush(mockBooks);
    });

    it('should create a book', () => {
      const newBook: BookTO = { 
        title: 'New Book', 
        author: 'New Author', 
        totalCopies: 10, 
        availableCopies: 10 
      };

      bookService.createBook(newBook).subscribe(book => {
        expect(book).toEqual(newBook);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/books');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(newBook);
      req.flush(newBook);
    });
  });

  describe('Member Operations', () => {
    it('should fetch all members', () => {
      const mockMembers: MemberTO[] = [
        { name: 'John Doe', email: 'john@example.com' }
      ];

      memberService.getAllMembers().subscribe(members => {
        expect(members).toEqual(mockMembers);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/members');
      expect(req.request.method).toBe('GET');
      req.flush(mockMembers);
    });
  });

  describe('Loan Operations', () => {
    it('should create a loan', () => {
      const loanRequest: LoanRequestTO = { bookId: 1, memberId: 1 };
      const mockLoan: LoanTO = {
        book: { title: 'Test Book', author: 'Test Author', totalCopies: 5, availableCopies: 4 },
        member: { name: 'John Doe', email: 'john@example.com' },
        loanDate: new Date('2024-01-01'),
        dueDate: new Date('2024-01-15'),
        returnDate: undefined,
        status: 'ACTIVE' as any
      };

      loanService.createLoan(loanRequest).subscribe(loan => {
        expect(loan).toEqual(mockLoan);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/loans');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(loanRequest);
      req.flush(mockLoan);
    });

    it('should get overdue loans', () => {
      const mockOverdueLoans: LoanTO[] = [];

      loanService.getOverdueLoans().subscribe(loans => {
        expect(loans).toEqual(mockOverdueLoans);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/loans/overdue');
      expect(req.request.method).toBe('GET');
      req.flush(mockOverdueLoans);
    });
  });

});