package com.tuto.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;
import com.tuto.library.domain.Loan;
import com.tuto.library.domain.LoanStatus;
import com.tuto.library.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {
    @Mock
    LoanRepository loanRepository;
    @Mock
    BookService bookService;
    @InjectMocks
    LoanService loanService;

    @Test
    void shouldReturnLoanWithReturnedStatus_whenReturningActiveLoan() {
        // GIVEN
        Loan loan = new Loan("loan1", "book1", "member1", LocalDate.now());
        loan.setStatus(LoanStatus.ACTIVE);

        given(loanRepository.findById("loan1")).willReturn(Optional.of(loan));
        given(loanRepository.save(loan)).willReturn(loan);

        // WHEN
        Loan result = loanService.processLoanReturn("loan1");

        // THEN
        assertThat(result.getStatus()).isEqualTo(LoanStatus.RETURNED);
    }

    @Test
    void shouldThrowInvalidLoanOperationException_whenReturningNonActiveLoan() {
        //TODO 1 : implement this test
    }
}
