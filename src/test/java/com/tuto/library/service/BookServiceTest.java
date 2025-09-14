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
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void shouldThrowBookNotFoundException_whenReturningBookByIdThatDoesNotExist() {
        // Given
        String bookId = "book1";
        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> bookService.returnBook(bookId));

        // Then
        assertThat(thrown)
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining(bookId);
    }

    @Test
    void shouldReturnTrue_whenBookIsAvailable() {
        // Given
        String bookId = "book2";
        Book book = new Book(bookId, "Clean Code", "Robert Martin", 3);
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

        // When
        boolean available = bookService.isAvailable(bookId);

        // Then
        assertThat(available).isTrue();
    }

    @Test
    void shouldDecreaseAvailableCopies_whenBorrowingBook() {
        // Given
        String bookId = "book3";
        Book book = new Book(bookId, "Effective Java", "Joshua Bloch", 2);
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

        // When
        bookService.borrowBook(bookId);

        // Then
        assertThat(book.getAvailableCopies()).isEqualTo(1);
        verify(bookRepository).save(book);
    }

    @Test
    void shouldThrowBookNotAvailableException_whenBorrowingBookWithNoCopies() {
        // Given
        String bookId = "book4";
        Book book = new Book(bookId, "Domain-Driven Design", "Eric Evans", 0);
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

        // When
        Throwable thrown = catchThrowable(() -> bookService.borrowBook(bookId));

        // Then
        assertThat(thrown)
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessageContaining(bookId);
    }

    @Test
    void shouldIncreaseAvailableCopies_whenReturningBook() {
        // Given
        String bookId = "book5";
        Book book = new Book(bookId, "Refactoring", "Martin Fowler", 3);
        // simulate borrowed copy
        book.setAvailableCopies(2);
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

        // When
        bookService.returnBook(bookId);

        // Then
        assertThat(book.getAvailableCopies()).isEqualTo(3);
        verify(bookRepository).save(book);
    }

    @Test
    void shouldThrowInvalidLoanOperationException_whenReturningBookWithMaxCopies() {
        // Given
        String bookId = "book6";
        Book book = new Book(bookId, "The Pragmatic Programmer", "Andy Hunt", 5);
        // already at max
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

        // When
        Throwable thrown = catchThrowable(() -> bookService.returnBook(bookId));

        // Then
        assertThat(thrown)
                .isInstanceOf(InvalidLoanOperationException.class)
                .hasMessageContaining(bookId);
    }
}
