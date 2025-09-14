package com.tuto.library.service;

import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;

import com.tuto.library.domain.Book;
import com.tuto.library.exception.BookNotAvailableException;
import com.tuto.library.exception.BookNotFoundException;
import com.tuto.library.exception.InvalidLoanOperationException;
import com.tuto.library.repository.BookRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void shouldThrowBookNotFoundException_whenReturningBookByIdThatDoesNotExist() {
        given(bookRepository.findById("nonexistent")).willReturn(Optional.empty());
        Throwable thrown = catchThrowable(() -> bookService.returnBook("nonexistent"));
        assertThat(thrown)
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("Book with ID nonexistent not found.");
    }

    @Test
    void shouldReturnTrue_whenBookIsAvailable() {
        Book book = mock(Book.class);
        given(book.getAvailableCopies()).willReturn(3);
        boolean available = bookService.isBookAvailable(book);
        assertThat(available).isTrue();
    }

    @Test
    void shouldDecreaseAvailableCopies_whenBorrowingBook() {
        Book book = mock(Book.class);
        given(book.getAvailableCopies()).willReturn(2); // used by borrowBook()

        bookService.borrowBook(book);

        verify(book).setAvailableCopies(1);
        verify(bookRepository).save(book);
    }

    @Test
    void shouldThrowBookNotAvailableException_whenBorrowingBookWithNoCopies() {
        Book book = mock(Book.class);
        given(book.getAvailableCopies()).willReturn(0);
        given(book.getId()).willReturn("book-2");
        Throwable thrown = catchThrowable(() -> bookService.borrowBook(book));
        assertThat(thrown)
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessageContaining("Book with ID book-2 is not available.");
    }

    @Test
    void shouldIncreaseAvailableCopies_whenReturningBook() {
        Book book = mock(Book.class);

        given(book.getAvailableCopies()).willReturn(1);
        given(book.getTotalCopies()).willReturn(3);
        given(bookRepository.findById("book-3")).willReturn(Optional.of(book));
        bookService.returnBook("book-3");
        verify(book).setAvailableCopies(2);
        verify(bookRepository).save(book);

    }

    @Test
    void shouldThrowInvalidLoanOperationException_whenReturningBookWithMaxCopies() {
        Book book = mock(Book.class);
        given(book.getAvailableCopies()).willReturn(5);
        given(book.getTotalCopies()).willReturn(5);
        given(bookRepository.findById("book-4")).willReturn(Optional.of(book));
        Throwable thrown = catchThrowable(() -> bookService.returnBook("book-4"));
        assertThat(thrown)
                .isInstanceOf(InvalidLoanOperationException.class)
                .hasMessageContaining("Book with ID book-4 already has maximum available copies.");
    }
}
