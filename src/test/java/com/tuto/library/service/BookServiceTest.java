package com.tuto.library.service;

import com.tuto.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;

class BookServiceTest {
    private BookRepository bookRepository;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookRepository = BDDMockito.mock(BookRepository.class);
        bookService = new BookService(bookRepository);
    }

    @Test
    void shouldReturnTrue_whenBookIsAvailable() {
    }

    @Test
    void shouldDecreaseAvailableCopies_whenBorrowingBook() {
    }

    @Test
    void shouldThrowBookNotAvailableException_whenBorrowingBookWithNoCopies() {
    }

    @Test
    void shouldIncreaseAvailableCopies_whenReturningBook() {
    }

    @Test
    void shouldThrowInvalidLoanOperationException_whenReturningBookWithMaxCopies() {
    }

    @Test
    void shouldThrowBookNotFoundException_whenReturningBookByIdThatDoesNotExist() {
    }
}
