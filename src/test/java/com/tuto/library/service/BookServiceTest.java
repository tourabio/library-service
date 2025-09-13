package com.tuto.library.service;

import com.tuto.library.domain.Book;
import com.tuto.library.exception.BookNotAvailableException;
import com.tuto.library.exception.BookNotFoundException;
import com.tuto.library.exception.InvalidLoanOperationException;
import com.tuto.library.repository.BookRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void shouldThrowBookNotFoundException_whenReturningBookByIdThatDoesNotExist() {
        // GIVEN
        String bookId = "book1";
        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        // WHEN
        Throwable thrown = catchThrowable(() -> bookService.findBookById(bookId));

        // THEN
        assertThat(thrown)
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("Book with ID " + bookId + " not found");
    }

    @Test
    void shouldReturnTrue_whenBookIsAvailable() {
        // GIVEN
        Book book = new Book("book1", "Domain-Driven Design", "fhegzgkze", 5);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // WHEN
        boolean available = bookService.isAvailable("book1");

        // THEN
        assertThat(available).isTrue();
    }

    @Test
    void shouldDecreaseAvailableCopies_whenBorrowingBook() {
        // GIVEN
        Book book = new Book("book1", "Effective Java", "fhegzgkze", 2);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));
        given(bookRepository.save(book)).willReturn(book);

        // WHEN
        bookService.borrowBook("book1");

        // THEN
        assertThat(book.getAvailableCopies()).isEqualTo(1);
        then(bookRepository).should().save(book);
    }

    @Test
    void shouldThrowBookNotAvailableException_whenBorrowingBookWithNoCopies() {
        // GIVEN
        Book book = new Book("book1", "Clean Code", "fhegzgkze", 0);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // WHEN
        Throwable thrown = catchThrowable(() -> bookService.borrowBook("book1"));

        // THEN
        assertThat(thrown)
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessageContaining("No copies available for book");
    }

    @Test
    void shouldIncreaseAvailableCopies_whenReturningBook() {
        // GIVEN
        Book book = new Book("book1", "Refactoring", "fhegzgkze", 3);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));
        given(bookRepository.save(book)).willReturn(book);

        bookService.borrowBook("book1");

        reset(bookRepository);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));
        given(bookRepository.save(book)).willReturn(book);

        // WHEN
        bookService.returnBook("book1");

        // THEN
        assertThat(book.getAvailableCopies()).isEqualTo(3);
        then(bookRepository).should().save(book);
    }

    @Test
    void shouldThrowInvalidLoanOperationException_whenReturningBookWithMaxCopies() {
        // GIVEN
        Book book = new Book("book1", "The Pragmatic Programmer", "fhegzgkze", 5); // already at max
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // WHEN
        Throwable thrown = catchThrowable(() -> bookService.returnBook("book1"));

        // THEN
        assertThat(thrown)
                .isInstanceOf(InvalidLoanOperationException.class)
                .hasMessageContaining("Cannot return book with ID book1 because all copies are already available");

    }
}
