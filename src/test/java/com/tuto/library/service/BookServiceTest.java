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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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

        // WHEN + THEN
        assertThatThrownBy(() -> bookService.returnBook("book1"))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("book1");
    }

    @Test
    void shouldReturnTrue_whenBookIsAvailable() {
        // GIVEN
        Book book = new Book("book1", "Clean Code", "Robert C. Martin", 3);

        // WHEN
        boolean available = book.isAvailable();

        // THEN
        assertThat(available).isTrue();
    }




    @Test
    void shouldDecreaseAvailableCopies_whenBorrowingBook() {
        // GIVEN
        Book book = new Book("book1", "Clean Code", "Robert C. Martin", 3);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // WHEN
        bookService.borrowBook("book1");

        // THEN
        assertThat(book.getAvailableCopies()).isEqualTo(2);
        verify(bookRepository).save(book);
    }


    @Test
    void shouldThrowBookNotAvailableException_whenBorrowingBookWithNoCopies() {
        // GIVEN
        Book book = new Book("book1", "Clean Code", "Robert C. Martin", 0); // 0 copies available
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // WHEN + THEN
        assertThatThrownBy(() -> bookService.borrowBook("book1"))
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessageContaining("book1");
    }


    @Test
    void shouldIncreaseAvailableCopies_whenReturningBook() {
        // GIVEN
        Book book = new Book("book1", "Clean Code", "Robert C. Martin", 3);
        book.setAvailableCopies(2); // currently 2 copies available
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // WHEN
        bookService.returnBook("book1");

        // THEN
        assertThat(book.getAvailableCopies()).isEqualTo(3);
        verify(bookRepository).save(book);
    }


    @Test
    void shouldThrowInvalidLoanOperationException_whenReturningBookWithMaxCopies() {
        // GIVEN
        Book book = new Book("book1", "Clean Code", "Robert C. Martin", 3);
        book.setAvailableCopies(3); // already at max copies
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // WHEN + THEN
        assertThatThrownBy(() -> bookService.returnBook("book1"))
                .isInstanceOf(InvalidLoanOperationException.class)
                .hasMessageContaining("book1");
    }

}
