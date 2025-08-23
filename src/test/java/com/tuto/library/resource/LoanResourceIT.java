package com.tuto.library.resource;

import static com.tuto.library.domain.LoanStatus.OVERDUE;
import static com.tuto.library.domain.LoanStatus.RETURNED;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import com.tuto.library.domain.Book;
import com.tuto.library.domain.Loan;
import com.tuto.library.domain.LoanStatus;
import com.tuto.library.domain.Member;
import com.tuto.library.repository.BookRepository;
import com.tuto.library.repository.LoanRepository;
import com.tuto.library.repository.MemberRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;

@QuarkusTest
class LoanResourceIT {

    @Inject
    BookRepository bookRepository;

    @Inject
    MemberRepository memberRepository;

    @Inject
    LoanRepository loanRepository;


    @Test
    void shouldCreateLoan_whenValidBookAndMemberProvided() {
        //TODO 1 : implement this test
    }

    @Test
    void shouldReturnLoanAndUpdateBookAvailability_whenReturningActiveLoan() {
        Book book = createBook("Return Book", "Return Author", 5, 4);
        Member member = createMember("Return Member", "return@example.com");
        Loan persistedLoan = createLoan(book, member);

        given()
                .when().put("/loans/{id}/return", persistedLoan.getId())
                .then()
                .statusCode(200)
                .body("status", is(RETURNED.name()));

        Book updatedBook = bookRepository.findByTitleAndAuthor("Return Book", "Return Author");
        assertThat(updatedBook).isNotNull();
        assertThat(updatedBook.getAvailableCopies()).isEqualTo(5);
    }

    @Test
    void shouldUpdateOverdueLoans_whenLoansArePastDueDate() {
        // TODO 3: implement this test (use createLoanWithDueDate helper method)
        // Arrange - Create 2 overdue books and 1 current book

        // Arrange - Create 2 members for overdue and current loans

        // Arrange - Create 2 overdue loans and 1 current loan (past dates for overdue, future for active)

        // Assert - Verify database state for 2 overdue and 1 current loan
    }


    @Transactional
    Book createBook(String title, String author, int totalCopies, int availableCopies) {
        Book book = new Book(title, author, totalCopies);
        book.setAvailableCopies(availableCopies);
        bookRepository.persist(book);
        return book;
    }

    @Transactional
    Member createMember(String name, String email) {
        Member member = new Member(name, email);
        memberRepository.persist(member);
        return member;
    }

    @Transactional
    Loan createLoan(Book book, Member member) {
        Loan loan = new Loan(book, member, java.time.LocalDate.now());
        loanRepository.persist(loan);
        return loan;
    }

    @Transactional
    Loan createLoanWithDueDate(Book book, Member member, LocalDate dueDate) {
        var loanDate = dueDate.minusDays(14);
        Loan loan = new Loan(book, member, loanDate);
        loan.setStatus(LoanStatus.ACTIVE);
        loanRepository.persist(loan);
        return loan;
    }
}