package com.tuto.library.service;
import com.tuto.library.exception.BookNotFoundException;
import com.tuto.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.tuto.library.domain.Book;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
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
        //given
        given(bookRepository.findById("bookid")).willReturn(Optional.empty());
        //when
        Throwable thrown = catchThrowable(() -> {
            bookService.returnBook("bookid");
        });
        //then
        assertThat(thrown)
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("book with ID bookid not found.");
    }

    @Test
    void shouldReturnTrue_whenBookIsAvailable() {
        //given
        Book availablebook = new Book ( "bookid","hello","faf",10);
        given(bookRepository.findById("bookid")).willReturn( Optional.of(availablebook));
        //when
        boolean exist=bookService.isBookAvailable("bookid");

        //then
        assertThat(exist).isTrue();
    }

    @Test
    void shouldDecreaseAvailableCopies_whenBorrowingBook() {
        //given
        Book availablebook = new Book ( "bookid","hello","faf",10);
        given(bookRepository.findById("bookid")).willReturn( Optional.of(availablebook));
        //when
        int res=bookService.borrow("bookid");
        //then
        assertThat(res).isEqualTo(9);
        assertThat(availablebook.getAvailableCopies()).isEqualTo(9);

    }


    @Test
    void shouldIncreaseAvailableCopies_whenReturningBook() {
        Book availablebook = new Book ( "bookid","hello","faf",10);
        given(bookRepository.findById("bookid")).willReturn( Optional.of(availablebook));
        //when
        int res=bookService.added("bookid");
        //then
        assertThat(res).isEqualTo(11);
        assertThat(availablebook.getAvailableCopies()).isEqualTo(11);

    }

    @Test
    void shouldThrowBookNotAvailableException_whenBorrowingBookWithNoCopies() {
        //given
        Book availablebook = new Book ( "bookid","hello","faf",0);
        given(bookRepository.findById("bookid")).willReturn( Optional.of(availablebook));

        //when
        Throwable thrown = catchThrowable(() -> bookService.availablebooktoborrow("bookid"));
        //then
        assertThat(thrown)
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("no copies are left of book with ID bookid.");
    }

    @Test
    void shouldThrowInvalidLoanOperationException_whenReturningBookWithMaxCopies() {
        //given
        Book availablebook = new Book ( "bookid","hello","faf",10);
        given(bookRepository.findById("bookid")).willReturn( Optional.of(availablebook));

        //when
        Throwable thrown = catchThrowable(() -> bookService.maxcopies("bookid"));
        //then
        assertThat(thrown)
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("can not loan book with ID bookid as it has maximum copies.");
    }
}
