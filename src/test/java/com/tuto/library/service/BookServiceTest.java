package com.tuto.library.service;

import com.tuto.library.exception.BookNotFoundException;
import com.tuto.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void shouldThrowBookNotFoundException_whenReturningBookByIdThatDoesNotExist() {
        // Given
        Long nonExistentBookId = 1L;
        when(bookRepository.findById(nonExistentBookId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BookNotFoundException.class, () -> bookService.returnBook(nonExistentBookId));
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
}