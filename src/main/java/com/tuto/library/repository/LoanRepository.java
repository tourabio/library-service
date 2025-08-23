package com.tuto.library.repository;

import com.tuto.library.domain.Loan;
import java.util.List;
import java.util.Optional;

public interface LoanRepository {
    
    Loan save(Loan loan);
    
    Optional<Loan> findById(String loanId);
    
    Optional<Loan> findActiveByBookAndMember(String bookId, String memberId);
}