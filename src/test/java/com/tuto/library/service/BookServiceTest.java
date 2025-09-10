package com.tuto.library.service;

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
        //given
        String bookId = "no-id";
        org.mockito.Mockito.when(bookRepository.findById(bookId)).thenReturn(java.util.Optional.empty());

        //when & then
        org.junit.jupiter.api.Assertions.assertThrows(com.tuto.library.exception.BookNotFoundException.class, () -> {
            bookService.getBookById(bookId);
        });
    }

    @Test
    void shouldReturnTrue_whenBookIsAvailable() {
        //given
        String bookId = "book1";
        com.tuto.library.domain.Book book = new com.tuto.library.domain.Book(bookId, "Title", "Author", 5);
        book.setAvailableCopies(3);
        org.mockito.Mockito.when(bookRepository.findById(bookId)).thenReturn(java.util.Optional.of(book));

        //when
        boolean available = bookService.isBookAvailable(bookId);

        //then
        org.junit.jupiter.api.Assertions.assertTrue(available);
    }

    @Test
    void shouldDecreaseAvailableCopies_whenBorrowingBook() {
        //given
        String bookId = "book2";
        com.tuto.library.domain.Book book = new com.tuto.library.domain.Book(bookId, "Title", "Author", 5);
        book.setAvailableCopies(3);
        org.mockito.Mockito.when(bookRepository.findById(bookId)).thenReturn(java.util.Optional.of(book));
        org.mockito.Mockito.when(bookRepository.save(org.mockito.Mockito.any())).thenReturn(book);

        //when
        bookService.borrowBook(bookId);

        //then
        org.junit.jupiter.api.Assertions.assertEquals(2, book.getAvailableCopies());
    }

    @Test
    void shouldThrowBookNotAvailableException_whenBorrowingBookWithNoCopies() {
        //given
        String bookId = "book3";
        com.tuto.library.domain.Book book = new com.tuto.library.domain.Book(bookId, "Title", "Author", 5);
        book.setAvailableCopies(0);
        org.mockito.Mockito.when(bookRepository.findById(bookId)).thenReturn(java.util.Optional.of(book));

        //when & then
        org.junit.jupiter.api.Assertions.assertThrows(com.tuto.library.exception.BookNotAvailableException.class, () -> {
            bookService.borrowBook(bookId);
        });
    }

    @Test
    void shouldIncreaseAvailableCopies_whenReturningBook() {
        //given
        String bookId = "book4";
        com.tuto.library.domain.Book book = new com.tuto.library.domain.Book(bookId, "Title", "Author", 5);
        book.setAvailableCopies(3);
        org.mockito.Mockito.when(bookRepository.findById(bookId)).thenReturn(java.util.Optional.of(book));
        org.mockito.Mockito.when(bookRepository.save(org.mockito.Mockito.any())).thenReturn(book);

        //when
        bookService.returnBook(bookId);

        //then
        org.junit.jupiter.api.Assertions.assertEquals(4, book.getAvailableCopies());
    }

    @Test
    void shouldThrowInvalidLoanOperationException_whenReturningBookWithMaxCopies() {
        //given
        String bookId = "book5";
        com.tuto.library.domain.Book book = new com.tuto.library.domain.Book(bookId, "Title", "Author", 5);
        book.setAvailableCopies(5);
        org.mockito.Mockito.when(bookRepository.findById(bookId)).thenReturn(java.util.Optional.of(book));

        //when & then
        org.junit.jupiter.api.Assertions.assertThrows(com.tuto.library.exception.InvalidLoanOperationException.class, () -> {
            bookService.returnBook(bookId);
        });
    }
}
