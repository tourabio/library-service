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
    void shouldThrowBookNotFoundException_whenBookNotFound() {
        // GIVEN
        given(bookRepository.findById("book1")).willReturn(Optional.empty());

        // WHEN
        Throwable thrown = catchThrowable(() -> bookService.findBookById("book1"));

        // THEN
        assertThat(thrown)
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("book1");
    }

    @Test
    void shouldThrowBookNotFoundException_whenUpdatingNonExistentBook() {
        // GIVEN
        Book book = new Book("book1", "Title", "Author", 5);
        given(bookRepository.findById("book1")).willReturn(Optional.empty());

        // WHEN
        Throwable thrown = catchThrowable(() -> bookService.updateBook(book));

        // THEN
        assertThat(thrown)
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("book1");
    }

    @Test
    void shouldThrowBookNotFoundException_whenDeletingNonExistentBook() {
        // GIVEN
        given(bookRepository.findById("book1")).willReturn(Optional.empty());

        // WHEN
        Throwable thrown = catchThrowable(() -> bookService.deleteBook("book1"));

        // THEN
        assertThat(thrown)
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("book1");
    }

    @Test
    void shouldReturnTrue_whenBookIsAvailable() {
        // GIVEN
        Book book = new Book("book1", "Title", "Author", 5);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));
        // WHEN/THEN
        assertThat(bookService.isBookAvailable("book1")).isTrue();
    }

    @Test
    void shouldThrowBookNotAvailableException_whenBookNotAvailable() {
        // GIVEN
        Book book = new Book("book1", "Title", "Author", 0);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // WHEN
        Throwable thrown = catchThrowable(() -> bookService.isBookAvailable("book1"));

        // THEN
        assertThat(thrown)
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessageContaining("book1");
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
                .hasMessageContaining("No copies available");
        verify(bookRepository, never()).save(any(Book.class));
    }
}
