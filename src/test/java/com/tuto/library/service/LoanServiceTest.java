package com.tuto.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.*;
import com.tuto.library.domain.Book;
import com.tuto.library.domain.Loan;
import com.tuto.library.domain.LoanStatus;
import com.tuto.library.exception.BookNotAvailableException;
import com.tuto.library.exception.InvalidLoanOperationException;
import com.tuto.library.exception.LoanNotFoundException;
import com.tuto.library.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Optional;

class LoanServiceTest {
    private LoanRepository loanRepository;
    private BookService bookService;
    private LoanService loanService;

    @BeforeEach
    void setUp() {
        loanRepository = mock(LoanRepository.class);
        bookService = mock(BookService.class);
        loanService = new LoanService(loanRepository, bookService);
    }

    @Test
    void shouldCreateLoanAndDecreaseBookCopies_whenBookIsAvailable() {
        // GIVEN
        Book book = new Book("book1", "Title", "Author", 2);
        book.setAvailableCopies(2);
        Loan loan = new Loan("loan1", "book1", "member1", LocalDate.now());

        given(bookService.findBookById("book1")).willReturn(book);
        given(loanRepository.save(loan)).willReturn(loan);

        // WHEN
        Loan result = loanService.createLoan(loan);

        // THEN
        assertThat(result).isEqualTo(loan);
        verify(bookService).findBookById("book1");
        verify(bookService).isBookAvailable(book);
        verify(bookService).borrowBook(book);
        verify(loanRepository).save(loan);
    }

    @Test
    void shouldThrowBookNotAvailableException_whenBookIsNotAvailable() {
        // GIVEN
        Book book = new Book("book1", "Title", "Author", 1);
        book.setAvailableCopies(0);
        Loan loan = new Loan("loan1", "book1", "member1", LocalDate.now());
        given(bookService.findBookById("book1")).willReturn(book);
        willThrow(new BookNotAvailableException("Book with ID book1 is not available.")).given(bookService)
                .isBookAvailable(book);

        // WHEN
        Throwable thrown = catchThrowable(() -> loanService.createLoan(loan));

        // THEN
        assertThat(thrown)
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessageContaining("book1");
        verify(bookService, never()).updateBook(any(Book.class));
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void shouldThrowLoanNotFoundException_whenLoanNotFound() {
        // GIVEN
        given(loanRepository.findById("loan1")).willReturn(Optional.empty());
        // WHEN
        Throwable thrown = catchThrowable(() -> loanService.findLoanById("loan1"));
        // THEN
        assertThat(thrown)
                .isInstanceOf(LoanNotFoundException.class)
                .hasMessageContaining("loan1");
    }

    @Test
    void shouldReturnLoanWithReturnedStatus_whenReturningActiveLoan() {
        // GIVEN
        Loan loan = new Loan("loan1", "book1", "member1", LocalDate.now());
        given(loanRepository.findById("loan1")).willReturn(Optional.of(loan));
        given(bookService.findBookById("book1")).willReturn(new Book("book1", "Title", "Author", 2));
        given(loanRepository.save(loan)).willReturn(loan);

        // WHEN
        Loan result = loanService.returnLoan("loan1");

        // THEN
        assertThat(result.getStatus()).isEqualTo(LoanStatus.RETURNED);
        verify(bookService).returnBook("book1");
        verify(loanRepository).save(loan);
    }

    @Test
    void shouldThrowInvalidLoanOperationException_whenReturningNonActiveLoan() {
        // GIVEN
        Loan loan = new Loan("loan1", "book1", "member1", LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnDate(LocalDate.now());
        given(loanRepository.findById("loan1")).willReturn(Optional.of(loan));
        // WHEN
        Throwable thrown = catchThrowable(() -> loanService.returnLoan("loan1"));
        // THEN
        assertThat(thrown)
                .isInstanceOf(InvalidLoanOperationException.class)
                .hasMessageContaining("not active");
    }
}
