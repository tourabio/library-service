package com.tuto.library.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
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
        String bookId="book1";
        given(bookRepository.findById(bookId)).willReturn(Optional.empty());
        // WHEN
        Throwable thrown=catchThrowable(()-> bookService.findBookById(bookId));
        // THEN
        assertThat(thrown)
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("Book with ID "+ bookId+" not found");
    }

    @Test
    void shouldReturnTrue_whenBookIsAvailable() {
        // GIVEN
        String bookId="book1";
        Book book = new Book(bookId,"Test","Test",1);
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        // WHEN
        boolean available = bookService.isAvailable(bookId);
        // THEN
        assertThat(available).isTrue();
    }

    @Test
    void shouldDecreaseAvailableCopies_whenBorrowingBook() {
        // GIVEN
        String bookId="book1";
        Book book = new Book(bookId,"Test","Test",2);
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        given(bookRepository.save(book)).willReturn(book);
        // WHEN
        bookService.borrowBook(bookId);
        // THEN
        assertThat(book.getAvailableCopies()).isEqualTo(1);
        then(bookRepository).should().save(book);
    }

    @Test
    void shouldThrowBookNotAvailableException_whenBorrowingBookWithNoCopies() {
        // GIVEN
        String bookId = "book1";
        Book book = new Book(bookId,"Test","Test",0);
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        // WHEN
        Throwable thrown=catchThrowable(()->bookService.borrowBook(bookId));
        // THEN
        assertThat(thrown)
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessageContaining("No copies available for book");
    }

    @Test
    void shouldIncreaseAvailableCopies_whenReturningBook() {
        // GIVEN
        String bookId="book1";
        Book book= new Book(bookId,"Test","Test",1);
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        given(bookRepository.save(book)).willReturn(book);

        bookService.borrowBook(bookId);
        reset(bookRepository);
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        given(bookRepository.save(book)).willReturn(book);

        // WHEN
        bookService.returnBook(bookId);

        // THEN
        assertThat(book.getAvailableCopies()).isEqualTo(1);
        then(bookRepository).should().save(book);
    }

    @Test
    void shouldThrowInvalidLoanOperationException_whenReturningBookWithMaxCopies() {
        // GIVEN
        String bookId="book1";
        Book book= new Book(bookId,"Test","Test",3);
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

        // WHEN
        Throwable thrown=catchThrowable(()->bookService.returnBook(bookId));

        // THEN
        assertThat(thrown)
                .isInstanceOf(InvalidLoanOperationException.class)
                .hasMessageContaining("Cannot return book with ID "+bookId +" because all copies are already available.");
    }
}
