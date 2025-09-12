package com.tuto.library.service;

import com.tuto.library.domain.Book;
import com.tuto.library.exception.BookNotAvailableException;
import com.tuto.library.exception.BookNotFoundException;
import com.tuto.library.exception.InvalidLoanOperationException;
import com.tuto.library.repository.BookRepository;

public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void returnBook(final String book1) {
        Book book = bookRepository.findById(book1)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + book1 + " not found"));
        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new InvalidLoanOperationException("All copies already returned for book: " + book1);
        }

        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
    }

    public boolean isBookAvailable(final String book1) {
        Book book = bookRepository.findById(book1)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + book1 + " not found"));
        return book.getAvailableCopies() > 0;
    }

    public void borrowBook(final String book1) {
        Book book = bookRepository.findById(book1)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + book1 + " not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("No copies available for book: " + book1);
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
    }
}


