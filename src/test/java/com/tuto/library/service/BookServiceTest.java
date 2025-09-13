package com.tuto.library.service;

import com.tuto.library.exception.BookNotFoundException;
import com.tuto.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.mockito.BDDMockito.given;

//TODO 2: implement the tests below using TDD approach
@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;
    LoanService loanService;

    @Test
    void shouldThrowBookNotFoundException_whenReturningBookByIdThatDoesNotExist() {
        //TODO 2.0: @Mohamed sayed, implement this test
        //given
        given(bookRepository.findById("book1")).willReturn(Optional.empty());
        //when
        Throwable thrown = catchThrowable(() -> loanService.processLoanReturn("loan1"));
        assertThat(thrown)
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("book1");


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
