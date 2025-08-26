package com.tuto.library.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.assertj.core.api.Assertions.assertThat;
import com.tuto.library.domain.Book;
import com.tuto.library.domain.Loan;
import com.tuto.library.domain.Member;
import com.tuto.library.repository.BookRepository;
import com.tuto.library.repository.LoanRepository;
import com.tuto.library.repository.MemberRepository;
import com.tuto.library.transferobjetcs.LoanRequestTO;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
class LoanResourceIT {

        @Inject
        BookRepository bookRepository;

        @Inject
        MemberRepository memberRepository;

        @Inject
        LoanRepository loanRepository;

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

        @Test
        void testGetAllLoans() {
                given()
                                .when().get("/loans")
                                .then()
                                .statusCode(200);
        }

        @Test
        void testCreateLoan() {

                Book book = createBook("Loan Test Book", "Loan Test Author", 5, 5);
                Member member = createMember("Loan Test Member", "loan.test@example.com");

                LoanRequestTO loanRequest = new LoanRequestTO(book.getId(), member.getId());

                given()
                                .contentType(ContentType.JSON)
                                .body(loanRequest)
                                .when().post("/loans")
                                .then()
                                .statusCode(201)
                                .body("book.title", is("Loan Test Book"))
                                .body("member.name", is("Loan Test Member"))
                                .body("status", is("ACTIVE"));

                Book updatedBook = bookRepository
                                .find("title = ?1 and author = ?2", "Loan Test Book", "Loan Test Author")
                                .firstResult();
                assertThat(updatedBook).isNotNull();
                assertThat(updatedBook.getAvailableCopies()).isEqualTo(4);
        }

        @Test
        void testCreateLoanWithInvalidBook() {
                Member member = createMember("Invalid Book Member", "invalid.book@example.com");

                LoanRequestTO loanRequest = new LoanRequestTO(-1L, member.getId());

                given()
                                .contentType(ContentType.JSON)
                                .body(loanRequest)
                                .when().post("/loans")
                                .then()
                                .statusCode(400);
        }

        @Test
        void testCreateLoanWithInvalidMember() {
                Book book = createBook("Invalid Member Book", "Invalid Member Author", 3, 3);

                LoanRequestTO loanRequest = new LoanRequestTO(book.getId(), -1L);

                given()
                                .contentType(ContentType.JSON)
                                .body(loanRequest)
                                .when().post("/loans")
                                .then()
                                .statusCode(400);
        }

        @Test
        void testGetLoanById() {
                Book book = createBook("Get Loan Book", "Get Loan Author", 2, 2);
                Member member = createMember("Get Loan Member", "get.loan@example.com");
                Loan persistedLoan = createLoan(book, member);

                given()
                                .when().get("/loans/{id}", persistedLoan.getId())
                                .then()
                                .statusCode(200)
                                .body("status", is("ACTIVE"));
        }

        @Test
        void testReturnLoan() {
                Book book = createBook("Return Book", "Return Author", 5, 4);
                Member member = createMember("Return Member", "return@example.com");
                Loan persistedLoan = createLoan(book, member);

                given()
                                .when().put("/loans/{id}/return", persistedLoan.getId())
                                .then()
                                .statusCode(200)
                                .body("status", is("RETURNED"));

                Book updatedBook = bookRepository.find("title = ?1 and author = ?2", "Return Book", "Return Author")
                                .firstResult();
                assertThat(updatedBook).isNotNull();
                assertThat(updatedBook.getAvailableCopies()).isEqualTo(5);
        }

        @Test
        void testReturnNonExistentLoan() {
                given()
                                .when().put("/loans/999/return")
                                .then()
                                .statusCode(400);
        }
}