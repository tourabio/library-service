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
import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.mockito.BDDMockito.*;
import com.tuto.library.exception.MemberNotFoundException;
import com.tuto.library.repository.MemberRepository;
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
        // giving

        given(bookRepository.findById("book1")).willReturn(Optional.empty());

        //when
        Throwable thrown = catchThrowable(() -> {
            bookService.returnBook("book1");
        });

        //then
        assertThat(thrown)
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("Book with ID book1 not found");
    }
    @Test
    void shouldReturnTrue_whenBookIsAvailable() {
        //given
        Book book = new Book("book1","Asher Black","PARKER S. HUNTINGTON",3);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // When
        boolean result = bookService.isBookAvailable("book1");

        // Then
        assertThat(result).isTrue();

    }
    @Test
    void shouldDecreaseAvailableCopies_whenBorrowingBook() {
        Book book = new Book("book1","Asher Black","PARKER S. HUNTINGTON",3);
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
        Book book = new Book("book1","Asher Black","PARKER S. HUNTINGTON",3);
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
        Book book = new Book("book1", "Asher Black", "PARKER S. HUNTINGTON", 3);
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
        Book book = new Book("book1", "Asher Black", "PARKER S. HUNTINGTON", 3);
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





}






