package com.tuto.library.service;

import com.tuto.library.domain.Book;
import com.tuto.library.exception.BookNotFoundException;
import com.tuto.library.exception.BookNotAvailableException;
import com.tuto.library.exception.InvalidLoanOperationException;
import com.tuto.library.repository.BookRepository;

public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book findBookById(String bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with id " + bookId + " not found"));
    }

    public boolean isBookAvailable(String bookId) {
        Book book = findBookById(bookId);
        return book.getAvailableCopies() > 0;
    }

    public Book borrowBook(String bookId) {
        Book book = findBookById(bookId);

        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("Book with id " + bookId + " is not available for borrowing");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        return bookRepository.save(book);
    }

    public Book returnBook(String bookId) {
        Book book = findBookById(bookId);

        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new InvalidLoanOperationException("Cannot return book with id " + bookId + " - already has maximum copies");
        }

        book.setAvailableCopies(book.getAvailableCopies() + 1);
        return bookRepository.save(book);
    }
}