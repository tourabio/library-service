package com.tuto.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import com.tuto.library.domain.Book;
import com.tuto.library.domain.Loan;
import com.tuto.library.domain.LoanStatus;
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
    }

    @Test
    void shouldThrowInvalidLoanOperationException_whenReturningNonActiveLoan() {
    }
}
