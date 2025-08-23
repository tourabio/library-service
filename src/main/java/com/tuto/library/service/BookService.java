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

    public Book findBookById(String id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found."));
    }

    public Book updateBook(Book book) {
        checkBookExists(book.getId());
        return bookRepository.save(book);
    }

    public void deleteBook(String id) {
        checkBookExists(id);
        bookRepository.deleteById(id);
    }

    private void checkBookExists(String id) {
        if (bookRepository.findById(id).isEmpty()) {
            throw new BookNotFoundException("Book with ID " + id + " not found.");
        }
    }

    public boolean isBookAvailable(String id) {
        Book book = findBookById(id);
        return isBookAvailable(book);
    }

    public boolean isBookAvailable(Book book) {
        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("Book with ID " + book.getId() + " is not available.");
        }
        return true;
    }

    public Book borrowBook(Book book) {
        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("No copies available for book: " + book.getTitle());
        }
        Book updatedBook = new Book(book.getId(), book.getTitle(), book.getAuthor(), book.getTotalCopies());
        updatedBook.setAvailableCopies(book.getAvailableCopies() - 1);
        updateBook(updatedBook);
        return updatedBook;
    }

    public void returnBook(Book book) {
        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new InvalidLoanOperationException(
                    "Cannot return more copies than total for book: " + book.getTitle());
        }
        Book updatedBook = new Book(book.getId(), book.getTitle(), book.getAuthor(), book.getTotalCopies());
        updatedBook.setAvailableCopies(book.getAvailableCopies() + 1);
        updateBook(updatedBook);
    }

    public void returnBook(String bookId) {
        Book book = findBookById(bookId);
        returnBook(book);
    }
}
