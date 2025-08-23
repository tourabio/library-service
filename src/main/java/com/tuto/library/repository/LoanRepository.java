package com.tuto.library.repository;

import com.tuto.library.domain.Loan;
import com.tuto.library.domain.LoanStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class LoanRepository implements PanacheRepository<Loan> {
    
    public List<Loan> findOverdueLoans() {
        return find("status = ?1 and dueDate < ?2", LoanStatus.ACTIVE, LocalDate.now()).list();
    }
}