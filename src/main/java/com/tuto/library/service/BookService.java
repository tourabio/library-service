package com.tuto.library.service;

import com.tuto.library.domain.Book;
import com.tuto.library.exception.BookNotFoundException;
import com.tuto.library.repository.BookRepository;

public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void borrowBook(String bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found: " + bookId));

        book.borrow();
        bookRepository.save(book);
    }

    public Book returnBook(String bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found: " + bookId));

        book.returnBook();
        bookRepository.save(book);

        return book;
    }

    public void borrow(String book1) {
    }
}
