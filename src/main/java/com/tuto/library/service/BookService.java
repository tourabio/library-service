package com.tuto.library.service;

import com.tuto.library.domain.Book;
import com.tuto.library.exception.BookNotAvailableException;
import com.tuto.library.exception.BookNotFoundException;
import com.tuto.library.exception.InvalidLoanOperationException;
import com.tuto.library.repository.BookRepository;
import java.util.Optional;

public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book findBookById(String id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found."));
    }

    public boolean isBookAvailable(Book book) {
        return book.getAvailableCopies() > 0;
    }

    public void borrowBook(Book book) {
        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("Book with ID " + book.getId() + " is not available.");
        }
        int updated = book.getAvailableCopies() - 1;
        book.setAvailableCopies(updated);
        bookRepository.save(book);
    }

    public void returnBook(String id) {
        Book book = findBookById(id);
        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new InvalidLoanOperationException("Book with ID " + id + " already has maximum available copies.");
        }
        int updated = book.getAvailableCopies() + 1;
        book.setAvailableCopies(updated);
        bookRepository.save(book);
    }
}