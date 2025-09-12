package com.tuto.library.service;

import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.mockito.BDDMockito.*;
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


//TODO 2: implement the tests below using TDD approach
@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void shouldThrowBookNotFoundException_whenReturningBookByIdThatDoesNotExist() {
        // GIVEN
        given(bookRepository.findById("book1")).willReturn(Optional.empty());

        // WHEN
        Throwable thrown = catchThrowable(() -> bookService.returnBook("book1"));

        // THEN
        assertThat(thrown)
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("Book with ID book1 not found");

    }

    @Test
    void shouldReturnTrue_whenBookIsAvailable() {
        // GIVEN
        Book book = new Book("book1", "Title1", "Author1", 3);
        book.setAvailableCopies(2);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // WHEN
        boolean available = bookService.isBookAvailable("book1");

        // THEN
        assertThat(available).isTrue();
    }

    @Test
    void shouldDecreaseAvailableCopies_whenBorrowingBook() {
        // GIVEN
        Book book = new Book("book1", "Title1", "Author1", 3);
        book.setAvailableCopies(2);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // WHEN
        bookService.borrowBook("book1");

        // THEN
        assertThat(book.getAvailableCopies()).isEqualTo(1);
        then(bookRepository).should().save(book);
    }

    @Test
    void shouldThrowBookNotAvailableException_whenBorrowingBookWithNoCopies() {
        // GIVEN
        Book book = new Book("book1", "Title1", "Author1", 1);
        book.setAvailableCopies(0);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // WHEN
        Throwable thrown = catchThrowable(() -> bookService.borrowBook("book1"));

        // THEN
        assertThat(thrown)
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessageContaining("No copies available");
    }

    @Test
    void shouldIncreaseAvailableCopies_whenReturningBook() {
        // GIVEN
        Book book = new Book("book1", "Title1", "Author1", 3);
        book.setAvailableCopies(1);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // WHEN
        bookService.returnBook("book1");

        // THEN
        assertThat(book.getAvailableCopies()).isEqualTo(2);
        then(bookRepository).should().save(book);
    }

    @Test
    void shouldThrowInvalidLoanOperationException_whenReturningBookWithMaxCopies() {
        // GIVEN
        Book book = new Book("book1", "Title1", "Author1", 3);
        book.setAvailableCopies(3); // already max
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // WHEN
        Throwable thrown = catchThrowable(() -> bookService.returnBook("book1"));

        // THEN
        assertThat(thrown)
                .isInstanceOf(InvalidLoanOperationException.class)
                .hasMessageContaining("All copies already returned");
    }
}
