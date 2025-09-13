package com.tuto.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.catchThrowable;
import static org.mockito.BDDMockito.*;
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
        given(bookRepository.findById("book1")).willReturn(Optional.empty());
        //when
        //Throwable thrown = catchThrowable(() ->{ bookService.deleteMember("book1");});
        //then
        //assertThat(thrown)
               // .isInstanceOf(BookNotFoundException.class)
                //.hasMessageContaining("Book with ID book1 not found.");
    }

    @Test
    void shouldReturnTrue_whenBookIsAvailable() {
        //given
        //Book book = new Book("book1","title","author");

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
