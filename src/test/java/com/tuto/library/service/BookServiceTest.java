package com.tuto.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.tuto.library.domain.Book;
import com.tuto.library.exception.BookNotAvailableException;
import com.tuto.library.exception.BookNotFoundException;
import com.tuto.library.exception.InvalidLoanOperationException;
import com.tuto.library.repository.BookRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@Nested
@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void shouldThrowBookNotFoundException_whenReturningBookByIdThatDoesNotExist() {
        // Given
        given(bookRepository.findById("book1")).willReturn(Optional.empty());

        // When
        Throwable thrown = catchThrowable(() -> bookService.returnBook("book1"));

        // Then
        assertThat(thrown)
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("book with ID book1 not found");
    }

    @Test
    void shouldReturnTrue_whenBookIsAvailable() {
        // Given
        Book availableBook = new Book("book1","Test Title","Test Author",5);  // Assume constructor or default sets isAvailable to true, or set it explicitly
        given(bookRepository.findById("book1")).willReturn(Optional.of(availableBook));

        // When
        boolean result = bookService.isBookAvailable("book1");

        // Then
        assertThat(result).isTrue();

    }
    @Test
    void shouldDecreaseAvailableCopies_whenBorrowingBook() {
        // Given
        Book book = new Book("book1", "Test Title", "Test Author", 5);  // 5 available copies
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // When
        bookService.borrowBook("book1");

        // Then
        assertThat(book.getAvailableCopies()).isEqualTo(4);  // Decreased from 5 to 4
        verify(bookRepository).save(book);  // Ensure save is called to persist the change
    }
    @Test
    void shouldThrowBookNotAvailableException_whenBorrowingBookWithNoCopies() {
        // Given
        Book unavailableBook = new Book("book1", "Test Title", "Test Author", 5);
        unavailableBook.setAvailableCopies(0);  // No copies available
        given(bookRepository.findById("book1")).willReturn(Optional.of(unavailableBook));

        // When
        Throwable thrown = catchThrowable(() -> bookService.borrowBook("book1"));

        // Then
        assertThat(thrown)
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessageContaining("book with ID book1 has no available copies");
    }

    @Test
    void shouldIncreaseAvailableCopies_whenReturningBook() {
        // Given
        Book book = new Book("book1", "Test Title", "Test Author", 5);
        book.setAvailableCopies(4);  // 4 available, 1 borrowed
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // When
        bookService.returnBook("book1");

        // Then
        assertThat(book.getAvailableCopies()).isEqualTo(5);  // Increased from 4 to 5
        verify(bookRepository).save(book);  // Ensure save is called

    }
    @Test
    void shouldThrowInvalidLoanOperationException_whenReturningBookWithMaxCopies() {
        // Given
        Book book = new Book("book1", "Test Title", "Test Author", 5);
        book.setAvailableCopies(5);  // All copies available
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // When
        Throwable thrown = catchThrowable(() -> bookService.returnBook("book1"));

        // Then
        assertThat(thrown)
                .isInstanceOf(InvalidLoanOperationException.class)
                .hasMessageContaining("Cannot return book with ID book1: all copies are already available");
    }
}




