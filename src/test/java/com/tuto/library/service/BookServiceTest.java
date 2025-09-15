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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

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
        Book book = new Book("book1", "Clean Code", "Robert Martin", 5);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // WHEN
        boolean result = bookService.isAvailable("book1");

        // THEN
        assertThat(result).isTrue();
    }

    @Test
    void shouldDecreaseAvailableCopies_whenBorrowingBook() {
        // GIVEN
        Book book = new Book("book1", "Clean Code", "Robert Martin", 5);
        book.setAvailableCopies(2); // Modifier les copies disponibles après création
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));
        given(bookRepository.save(book)).willReturn(book);

        // WHEN
        bookService.borrowBook("book1");

        // THENgit
        assertThat(book.getAvailableCopies()).isEqualTo(1);
    }

    @Test
    void shouldThrowBookNotAvailableException_whenBorrowingBookWithNoCopies() {
        // GIVEN
        Book book = new Book("book1", "Clean Code", "Robert Martin", 5);
        book.setAvailableCopies(0); // Aucune copie disponible
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // WHEN
        Throwable thrown = catchThrowable(() -> bookService.borrowBook("book1"));

        // THEN
        assertThat(thrown)
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessageContaining("Book with ID book1 is not available");
    }

    @Test
    void shouldIncreaseAvailableCopies_whenReturningBook() {
        // GIVEN
        Book book = new Book("book1", "Clean Code", "Robert Martin", 5);
        book.setAvailableCopies(2); // Disponible mais pas au maximum
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));
        given(bookRepository.save(book)).willReturn(book);

        // WHEN
        bookService.returnBook("book1");

        // THEN
        assertThat(book.getAvailableCopies()).isEqualTo(3);
    }

    @Test
    void shouldThrowInvalidLoanOperationException_whenReturningBookWithMaxCopies() {
        // GIVEN
        Book book = new Book("book1", "Clean Code", "Robert Martin", 5);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // WHEN
        Throwable thrown = catchThrowable(() -> bookService.returnBook("book1"));

        // THEN
        assertThat(thrown)
                .isInstanceOf(InvalidLoanOperationException.class)
                .hasMessageContaining("Cannot return book with ID book1, already at max copies");
    }
}
