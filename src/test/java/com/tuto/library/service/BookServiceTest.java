package com.tuto.library.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import com.tuto.library.domain.Book;
import com.tuto.library.exception.BookNotAvailableException;
import com.tuto.library.exception.InvalidLoanOperationException;
import com.tuto.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.tuto.library.domain.Book;

import java.util.Optional;

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
    }

    @Test
    void shouldReturnTrue_whenBookIsAvailable() {
        // Given
        String bookId = "book1";
        Book book = new Book(bookId, "Sample Book", 1);
        when(bookRepository.findById(bookId)).thenReturn(book);

        // When
        boolean result = bookService.isBookAvailable(bookId);

        // Then
        assertTrue(result, "Book should be available when copies > 0");
    }

    @Test
    void shouldDecreaseAvailableCopies_whenBorrowingBook() {
        Book book = new Book("book1","title1","author1",3);
        book.setAvailableCopies(2);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));
        given(bookRepository.save(book)).willReturn(book);

        //when
        Book borrowedBook = bookService.borrowBook("book1");

        //then
        assertThat(borrowedBook.getAvailableCopies()).isEqualTo(1);
    }

    @Test
    void shouldThrowBookNotAvailableException_whenBorrowingBookWithNoCopies() {
        Book book = new Book("book1","title1","author1",3);
        book.setAvailableCopies(0);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));
        //when
        Throwable thrown = catchThrowable(() ->
                bookService.borrowBookWithNoCopies("book1"));
        //then
        assertThat(thrown)
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessageContaining("No available copies for this book");
    }


    @Test
    void shouldIncreaseAvailableCopies_whenReturningBook() {
        Book book = new Book("book1", "Title1", "Author1", 3);
        book.setAvailableCopies(1);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));
        given(bookRepository.save(book)).willReturn(book);

        //when
        Book returneBook = bookService.returnedBook("book1");

        //then
        assertThat(returneBook.getAvailableCopies()).isEqualTo(2);
    }

    @Test
    void shouldThrowInvalidLoanOperationException_whenReturningBookWithMaxCopies() {

    }Book book = new Book("book1", "Title1", "Author1", 3);
        book.setAvailableCopies(3);
    given(bookRepository.findById("book1")).willReturn(Optional.of(book));
    //when
    Throwable thrown = catchThrowable(() ->
            bookService.returnBookWithMaxCopie("book1"));
    //then
    assertThat(thrown)
    .isInstanceOf(InvalidLoanOperationException.class)
                .hasMessageContaining("all copies are already available");
}
