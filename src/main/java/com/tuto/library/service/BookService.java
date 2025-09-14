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

    public void findBookById(String id) {
        if (bookRepository.findById(id).isEmpty()) {
            throw new BookNotFoundException("Book with ID "+ id +" not found");
        }
    }
    public boolean isAvailable(String id) {
        return bookRepository.findById(id)
                .map(book -> book.getAvailableCopies() > 0)
                .orElse(false); // boolean, pas Optional<Boolean>
    }
    public void borrowBook(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("No copies available to borrow");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
    }
    public void returnBook(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found"));
        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new InvalidLoanOperationException("All copies are already in the library");
        }
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
    }
}


