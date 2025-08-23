package com.tuto.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.*;
import com.tuto.library.domain.Book;
import com.tuto.library.exception.BookNotAvailableException;
import com.tuto.library.exception.BookNotFoundException;
import com.tuto.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import java.util.Optional;

class BookServiceTest {
    private BookRepository bookRepository;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookRepository = BDDMockito.mock(BookRepository.class);
        bookService = new BookService(bookRepository);
    }

    @Test
    void shouldReturnTrue_whenBookIsAvailable() {
        // GIVEN
        Book book = new Book("book1", "Title", "Author", 5);
        book.setAvailableCopies(3);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // WHEN/THEN
        var result = bookService.isBookAvailable(book);

        assertThat(result).isTrue();
    }

    @Test
    void shouldDecreaseAvailableCopies_whenBorrowingBook() {
        // GIVEN
        Book book = new Book("book1", "Title", "Author", 5);
        book.setAvailableCopies(3);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // WHEN
        Book borrowedBook = bookService.borrowBook(book);

        // THEN
        assertThat(borrowedBook.getAvailableCopies()).isEqualTo(2);
    }

    @Test
    void shouldThrowBookNotAvailableException_whenBorrowingBookWithNoCopies() {
        // GIVEN
        Book book = new Book("book1", "Title", "Author", 5);
        book.setAvailableCopies(0);

        // WHEN
        Throwable thrown = catchThrowable(() -> bookService.borrowBook(book));

        // THEN
        assertThat(thrown)
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessageContaining("Book with ID " + book.getId() + " is not available.");
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void shouldIncreaseAvailableCopies_whenReturningBook() {
        // GIVEN
        Book book = new Book("book1", "Title", "Author", 5);
        book.setAvailableCopies(2);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));
        given(bookRepository.save(any(Book.class))).willAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        Book returnedBook = bookService.returnBook(book);

        // THEN
        assertThat(returnedBook.getAvailableCopies()).isEqualTo(book.getAvailableCopies() + 1);
    }

    @Test
    void shouldThrowInvalidLoanOperationException_whenReturningBookWithMaxCopies() {
        // GIVEN
        Book book = new Book("book1", "Title", "Author", 5);
        book.setAvailableCopies(5);

        // WHEN
        Throwable thrown = catchThrowable(() -> bookService.returnBook(book));

        // THEN
        assertThat(thrown)
                .isInstanceOf(com.tuto.library.exception.InvalidLoanOperationException.class)
                .hasMessageContaining("Cannot return more copies than total for book");
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void shouldThrowBookNotFoundException_whenReturningBookByIdThatDoesNotExist() {
        // GIVEN
        given(bookRepository.findById("book1")).willReturn(Optional.empty());

        // WHEN
        Throwable thrown = catchThrowable(() -> bookService.returnBook("book1"));

        // THEN
        assertThat(thrown)
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("book1");
    }
}
