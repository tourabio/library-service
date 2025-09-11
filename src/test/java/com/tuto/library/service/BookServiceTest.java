package com.tuto.library.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
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
        //GIVEN
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
        //Given
        Book book = new Book("book1","atomic habits","James Clear",5);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));
        //when
        boolean available = bookService.isAvailable("book1");
        //then
        assertThat(available).isTrue();

    }

    @Test
    void shouldDecreaseAvailableCopies_whenBorrowingBook() {
        //Given
        Book book = new Book("book1","atomic habits","James Clear",5);
        given(bookRepository.findById("book1")).willReturn(Optional.of(book));
        //when
        bookService.borrowBook("book1");
        //then
        assertThat(book.getAvailableCopies()).isEqualTo(4);
    }

    @Test
    void shouldThrowBookNotAvailableException_whenBorrowingBookWithNoCopies() {
        Book book = new Book("book1", "Atomic Habits", "James Clear", 1);
        book.setAvailableCopies(0);

        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // when
        Throwable thrown = catchThrowable(() -> bookService.borrowBook("book1"));

        // then
        assertThat(thrown)
                .isInstanceOf(BookNotAvailableException.class)
                .hasMessage("No copies available for book1");
    }

    @Test
    void shouldIncreaseAvailableCopies_whenReturningBook() {
        //Given
        Book book = new Book("book1", "Atomic Habits", "James Clear", 5);
        book.setAvailableCopies(3);
        when(bookRepository.findById("book1")).thenReturn(Optional.of(book));
        //when
        bookService.returnBook("book1");
        //then
        assertThat(book.getAvailableCopies()).isEqualTo(4);
        verify(bookRepository).save(book);
    }

    @Test
    void shouldThrowInvalidLoanOperationException_whenReturningBookWithMaxCopies() {
        // GIVEN
        Book book = new Book("book1", "Atomic Habits", "James Clear", 3);
        book.setAvailableCopies(3);

        given(bookRepository.findById("book1")).willReturn(Optional.of(book));

        // When
        Throwable thrown = catchThrowable(() -> bookService.returnBook("book1"));

        // Then
        assertThat(thrown)
                .isInstanceOf(InvalidLoanOperationException.class)
                .hasMessageContaining("Cannot return book1, all copies already available");
    }
}
