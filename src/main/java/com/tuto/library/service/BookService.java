package com.tuto.library.service;

import com.tuto.library.repository.BookRepository;


import com.tuto.library.domain.Book;
import com.tuto.library.exception.BookNotAvailableException;
import com.tuto.library.exception.BookNotFoundException;
import com.tuto.library.exception.InvalidLoanOperationException;

public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public boolean isAvailable(String bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + bookId + " not found"));
        return book.getAvailableCopies() > 0;
    }

    public void borrowBook(String bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + bookId + " not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("No copies available for " + bookId);
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
    }




    public void returnBook(String bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + bookId + " not found"));

        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new InvalidLoanOperationException("Cannot return " + bookId + ", all copies already available");
        }

        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
    }
}
