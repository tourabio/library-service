package com.tuto.library.service;

import com.tuto.library.exception.BookNotFoundException;
import com.tuto.library.exception.BookNotAvailableException;
import com.tuto.library.exception.InvalidLoanOperationException;
import com.tuto.library.domain.Book;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void shouldThrowBookNotFoundException_whenFindingBookByIdThatDoesNotExist() {
        // Given
        String nonExistentBookId = "non-existent-id";
        given(bookRepository.findById(nonExistentBookId)).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> bookService.findBookById(nonExistentBookId));

        // Then
        assertThat(thrown)
                .isInstanceOf(BookNotFoundException.class)
                .hasMessage("Book with id " + nonExistentBookId + " not found");
    }

    @Test
    void shouldReturnTrue_whenBookIsAvailable() {
        // Given
        String bookId = "available-book";
        Book availableBook = new Book(bookId, "Available Book", "Author", 5);
        availableBook.setAvailableCopies(3); // Some copies available
        given(bookRepository.findById(bookId)).willReturn(Optional.of(availableBook));

        // When
        boolean isAvailable = bookService.isBookAvailable(bookId);

        // Then
        assertThat(isAvailable).isTrue();
    }

    @Test
    void shouldReturnFalse_whenBookIsNotAvailable() {
        // Given
        String bookId = "unavailable-book";
        Book unavailableBook = new Book(bookId, "Unavailable Book", "Author", 5);
        unavailableBook.setAvailableCopies(0); // No copies available
        given(bookRepository.findById(bookId)).willReturn(Optional.of(unavailableBook));

        // When
        boolean isAvailable = bookService.isBookAvailable(bookId);

        // Then
        assertThat(isAvailable).isFalse();
    }

    @Test
    void shouldDecreaseAvailableCopies_whenBorrowingBook() {
        // Given
        String bookId = "book-to-borrow";
        Book book = new Book(bookId, "Book Title", "Author", 5);
        book.setAvailableCopies(3);
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        given(bookRepository.save(any(Book.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        Book borrowedBook = bookService.borrowBook(bookId);

        // Then
        assertThat(borrowedBook.getAvailableCopies()).isEqualTo(2);
        verify(bookRepository).save(book);
    }

    @Test
    void shouldThrowBookNotAvailableException_whenBorrowingBookWithNoCopies() {
        // Given
        String bookId = "no-copies-book";
        Book book = new Book(bookId, "No Copies Book", "Author", 5);
        book.setAvailableCopies(0); // No copies available
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

        // When
        Throwable thrown = catchThrowable(() -> bookService.borrowBook(bookId));

        // Then
        assertThat(thrown)
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessage("Book with id " + bookId + " is not available for borrowing");
    }

    @Test
    void shouldIncreaseAvailableCopies_whenReturningBook() {
        // Given
        String bookId = "book-to-return";
        Book book = new Book(bookId, "Book Title", "Author", 5);
        book.setAvailableCopies(2);
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        given(bookRepository.save(any(Book.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        Book returnedBook = bookService.returnBook(bookId);

        // Then
        assertThat(returnedBook.getAvailableCopies()).isEqualTo(3);
        verify(bookRepository).save(book);
    }

    @Test
    void shouldThrowInvalidLoanOperationException_whenReturningBookWithMaxCopies() {
        // Given
        String bookId = "max-copies-book";
        Book book = new Book(bookId, "Max Copies Book", "Author", 5);
        book.setAvailableCopies(5); // Already at maximum copies
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

        // When
        Throwable thrown = catchThrowable(() -> bookService.returnBook(bookId));

        // Then
        assertThat(thrown)
                .isInstanceOf(InvalidLoanOperationException.class)
                .hasMessage("Cannot return book with id " + bookId + " - already has maximum copies");
    }
}