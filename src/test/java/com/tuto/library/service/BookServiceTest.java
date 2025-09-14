package com.tuto.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.mockito.BDDMockito.*;
import com.tuto.library.domain.Book;
import com.tuto.library.exception.BookNotFoundException;
import com.tuto.library.exception.MemberNotFoundException;
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
        //Given
        given(bookRepository.findById("book1")).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(()->
        {
            bookService.deleteBook("book1");
        });
        assertThat(thrown)
                .isInstanceOF(BookNotFoundException.class)
                .hasMessageContaining(".can't find the book");
    }

    @Test
    void shouldReturnTrue_whenBookIsAvailable() {
        Book book1=  new Book('1', 'btiltle', 'bauthor', 3);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book1));
        //when
     Book res= bookService.findById("book1");

        assertThat(res).isNotNull();
        assertThat(res.getId()).isEqualTo("book1");

    }

    @Test
    void shouldDecreaseAvailableCopies_whenBorrowingBook() {
        // given
        Book book = new Book("book1", "titlee", "auth", 3);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // when
        bookService.borrowBook("book1");

        // then
        assertThat(book.getAvailableCopies()).isEqualTo(2);
    }


    @Test
    void shouldThrowBookNotAvailableException_whenBorrowingBookWithNoCopies() {
        Book book = new Book("book1", "Title", "Author", 1);
        book.setAvailableCopies(0);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        Throwable thrown = catchThrowable(() -> bookService.borrowBook("book1"));

        assertThat(thrown)
                .isInstanceOf(CopyNotFoundException.class)
                .hasMessageContaining("can't borrow the book");
    }
    }

    @Test
    void shouldIncreaseAvailableCopies_whenReturningBook() {
    Book book = new Book("book1", "titlee", "auth", 3);
    book.setAvailableCopies(2);
    given(bookRepository.findById("book1")).willReturn(Optional.of(book));
    bookService.retBook("book1");
    assertThat(book.getAvailableCopies()).isEqualTo(3);
    }

    @Test
    void shouldThrowInvalidLoanOperationException_whenReturningBookWithMaxCopies() {
        Book book = new Book("book1", "titlee", "auth", 3);
        book.setAvailableCopies(3);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));
        Throwable thrown = catchThrowable(() -> bookService.retBook("book1"));

        assertThat(thrown)
                .isInstanceOf(InvalidLoanOperationException.class)
                .hasMessageContaining("can't loan the book");

        assertThat(book.getAvailableCopies()).isEqualTo(3);


    }
}
