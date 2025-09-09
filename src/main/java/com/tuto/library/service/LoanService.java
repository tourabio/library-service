package com.tuto.library.service;

import com.tuto.library.domain.Loan;
import com.tuto.library.domain.LoanStatus;
import com.tuto.library.exception.InvalidLoanOperationException;
import com.tuto.library.exception.LoanNotFoundException;
import com.tuto.library.repository.LoanRepository;
import java.time.LocalDate;

public class LoanService {
    private final LoanRepository loanRepository;
    private final BookService bookService;

    public LoanService(LoanRepository loanRepository, BookService bookService) {
        this.loanRepository = loanRepository;
        this.bookService = bookService;
    }

    public Loan createLoan(Loan loan) {
//        Book book = bookService.findBookById(loan.getBookId());
//        bookService.isBookAvailable(book);
//        bookService.borrowBook(book);
        return loanRepository.save(loan);
    }

    public Loan findLoanById(String id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Loan with ID " + id + " not found."));
    }

    public Loan processLoanReturn(String id) {
        Loan loan = findLoanById(id);
        validateLoanIsActive(id, loan);
        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);
//        bookService.returnBook(loan.getBookId());9
        return loanRepository.save(loan);
    }

    private void validateLoanIsActive(final String id, final Loan loan) {
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new InvalidLoanOperationException("Loan with ID " + id + " is not active.");
        }
    }

}
