package com.tuto.library.transferobjetcs;

import com.tuto.library.domain.LoanStatus;
import java.time.LocalDate;

public record LoanTO(BookTO book, MemberTO member, LocalDate loanDate, LocalDate dueDate, LocalDate returnDate,
        LoanStatus status) {
}
