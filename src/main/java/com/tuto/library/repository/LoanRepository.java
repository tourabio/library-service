package com.tuto.library.repository;

import com.tuto.library.domain.Loan;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LoanRepository implements PanacheRepository<Loan> {
}