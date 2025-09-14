package com.tuto.library.service;

import com.tuto.library.domain.Book;
import com.tuto.library.domain.Loan;
import com.tuto.library.domain.LoanStatus;
import com.tuto.library.exception.BookNotAvailableException;
import com.tuto.library.exception.InvalidLoanOperationException;
import com.tuto.library.exception.MemberNotFoundException;
import com.tuto.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.tuto.library.exception.BookNotFoundException;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.mockito.BDDMockito.given;

//TODO 2: implement the tests below using TDD approach
@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void shouldThrowBookNotFoundException_whenReturningBookByIdThatDoesNotExist() {
        //TODO 2.0: @Mohamed sayed, implement this test
        //given
        given(bookRepository.findById("book1")).willReturn(Optional.empty());
        //when
        Throwable thrown = catchThrowable(() -> bookService.findBookById("book1"));
        //then
        assertThat(thrown)
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("Book with ID book1 not found");
    }

    @Test
    void shouldReturnTrue_whenBookIsAvailable() {
        //given
        Book book = new Book("book1", "petit", "mariem", 5);
        book.setAvailableCopies(3);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));
        //when
        Boolean result = bookService.isAvailable("book1");
        //then
        assertThat(result).isTrue();

    }

    @Test
    void shouldDecreaseAvailableCopies_whenBorrowingBook() {
        // GIVEN
        Book book = new Book("book1", "fantine", "victor", 5);
        book.setAvailableCopies(3);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));
        given(bookRepository.save(book)).willReturn(book);

        // WHEN
        bookService.borrowBook("book1");

        // THEN
        assertThat(book.getAvailableCopies()).isEqualTo(2);
    }

    @Test
    void shouldThrowBookNotAvailableException_whenBorrowingBookWithNoCopies() {
        // GIVEN

        Book book = new Book("book1", "fantine", "victor", 5);
        book.setAvailableCopies(0);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // WHEN
        Throwable thrown = catchThrowable(() -> bookService.borrowBook("book1"));

        // THEN
        assertThat(thrown)
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessageContaining("No copies available to borrow");
    }


    @Test
    void shouldIncreaseAvailableCopies_whenReturningBook() {
        // GIVEN
        Book book = new Book("book1", "fantine", "victor", 5);
        book.setAvailableCopies(2);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));
        given(bookRepository.save(book)).willAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        bookService.returnBook("book1");

        // THEN
        assertThat(book.getAvailableCopies()).isEqualTo(3);
    }

    @Test
    void shouldThrowInvalidLoanOperationException_whenReturningBookWithMaxCopies() {
        // GIVEN
        Book book = new Book("book1", "fantine", "victor", 5);
        book.setAvailableCopies(5);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // WHEN
        Throwable thrown = catchThrowable(() -> bookService.returnBook("book1"));

        // THEN
        assertThat(thrown)
                .isInstanceOf(InvalidLoanOperationException.class)
                .hasMessageContaining("All copies are already in the library");
    }
}
