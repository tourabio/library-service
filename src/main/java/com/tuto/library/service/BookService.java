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


    public Book findBookById(final String bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with ID "+bookId +" not found"));
    }

    public boolean isAvailable(final String bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with ID "+bookId +" not found"));
        return book.getAvailableCopies() > 0;
    }


    public void borrowBook(final String bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with ID "+bookId +" not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("No copies available for book with ID "+bookId);
        }
        book.setAvailableCopies(book.getAvailableCopies()-1 );
        bookRepository.save(book);
    }

    public void returnBook(final String bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with ID "+bookId +" not found."));

        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new InvalidLoanOperationException("Cannot return book with ID "+bookId +" because all copies are already available.");
        }

        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
    }



}
