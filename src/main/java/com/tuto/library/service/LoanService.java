package com.tuto.library.service;

import com.tuto.library.domain.Book;
import com.tuto.library.domain.Loan;
import com.tuto.library.domain.LoanStatus;
import com.tuto.library.domain.Member;
import com.tuto.library.exception.InvalidLoanOperationException;
import com.tuto.library.exception.LoanNotFoundException;
import com.tuto.library.repository.LoanRepository;
import com.tuto.library.transferobjetcs.BookTO;
import com.tuto.library.transferobjetcs.LoanRequestTO;
import com.tuto.library.transferobjetcs.LoanTO;
import com.tuto.library.transferobjetcs.MemberTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
@Transactional
public class LoanService {
    private final LoanRepository loanRepository;
    private final BookService bookService;
    private final MemberService memberService;

    public LoanService(
            LoanRepository loanRepository, BookService bookService,
            MemberService memberService) {
        this.loanRepository = loanRepository;
        this.bookService = bookService;
        this.memberService = memberService;
    }

    public Loan findLoanById(Long id) {
        return loanRepository.findByIdOptional(id)
                .orElseThrow(() -> new LoanNotFoundException("Loan with ID " + id + " not found."));
    }

    public LoanTO processLoanReturn(Long id) {
        Loan loan = findLoanById(id);
        validateLoanIsActive(id, loan);
        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);
        bookService.returnBook(loan.getBook().getId());
        return toTO(loan);
    }

    public List<LoanTO> getAllLoans() {
        return loanRepository.listAll().stream().map(this::toTO).toList();
    }

    public LoanTO getLoanById(Long id) {
        return loanRepository.findByIdOptional(id).map(this::toTO).orElse(null);
    }

    public LoanTO createLoan(LoanRequestTO request) {
        Book book = bookService.findBookById(request.bookId());
        Member member = memberService.findMemberById(request.memberId());
        bookService.ValidateBookIsAvailable(book);
        Loan loan = new Loan(book, member, java.time.LocalDate.now());
        loanRepository.persist(loan);
        bookService.borrowBook(book.getId());
        return toTO(loan);
    }

    private LoanTO toTO(Loan loan) {
        if (loan == null) {
            return null;
        }
        return new LoanTO(
                toBookTO(loan.getBook()),
                toMemberTO(loan.getMember()),
                loan.getLoanDate(),
                loan.getDueDate(),
                loan.getReturnDate(),
                loan.getStatus());
    }

    private BookTO toBookTO(Book book) {
        if (book == null) {
            return null;
        }
        return new BookTO(book.getTitle(), book.getAuthor(), book.getTotalCopies(), book.getAvailableCopies());
    }

    private MemberTO toMemberTO(Member member) {
        if (member == null) {
            return null;
        }
        return new MemberTO(member.getName(), member.getEmail());
    }

    private void validateLoanIsActive(final Long id, final Loan loan) {
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new InvalidLoanOperationException("Loan with ID " + id + " is not active.");
        }
    }

    public List<LoanTO> updateOverdueLoans() {
        List<Loan> overdueLoans = loanRepository.findOverdueLoans();
        
        overdueLoans.forEach(loan -> loan.setStatus(LoanStatus.OVERDUE));
        
        return overdueLoans.stream()
                .map(this::toTO)
                .toList();
    }

}
