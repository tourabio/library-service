package com.tuto.library.service;

import com.tuto.library.domain.Book;
import com.tuto.library.exception.BookNotFoundException;
import com.tuto.library.exception.InvalidLoanOperationException;
import com.tuto.library.repository.BookRepository;

public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book findBookById(String id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found."));
    }

    public Book updateBook(Book book) {
        return bookRepository.save(book);
    }


    public Book returnBook(Book book) {
        Book updatedBook = new Book(book.getId(), book.getTitle(), book.getAuthor(), book.getTotalCopies());
        updatedBook.setAvailableCopies(book.getAvailableCopies() + 1);
        return updateBook(updatedBook);
    }

    public void returnBook(String bookId) {
        Book book = findBookById(bookId);
        checkTotalCopiesLessThanAvailableCopies(book);
        returnBook(book);
    }

    private void checkTotalCopiesLessThanAvailableCopies(final Book book) {
        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new InvalidLoanOperationException(
                    "Cannot return more copies than total for book: " + book.getTitle());
        }
    }
}
