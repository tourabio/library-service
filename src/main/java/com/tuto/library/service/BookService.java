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

    public Book getBookById(String id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isEmpty()) {
            throw new BookNotFoundException("Book with id " + id + " not found");
        }
        return book.get();
    }

    public boolean isBookAvailable(String id) {
        Book book = getBookById(id);
        return book.getAvailableCopies() > 0;
    }

    public void borrowBook(String id) {
        Book book = getBookById(id);
        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("Book with id " + id + " is not available for borrowing");
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
    }

    public void returnBook(String id) {
        Book book = getBookById(id);
        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new InvalidLoanOperationException("Cannot return book with id " + id + " as it already has maximum copies");
        }
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
    }
}
