package com.tuto.library.service;

import com.tuto.library.exception.BookNotFoundException;
import com.tuto.library.repository.BookRepository;
import com.tuto.library.domain.Book;


public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    private void checkBookExists(final String book) {
        if (bookRepository.findById(book).isEmpty()) {
            throw new BookNotFoundException("book with ID " + book + " not found.");
        }
    }


    public Book returnBook(String id) {
        checkBookExists(id);
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found"));
    }


    public int added(String id) {
        checkBookExists(id);
        Book book = bookRepository.findById(id).get();
        book.setAvailableCopies (book.getAvailableCopies() + 1);
        return book.getAvailableCopies();
    }

    public int borrow(String id) {
        checkBookExists(id);
        Book book = bookRepository.findById(id).get();
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        return book.getAvailableCopies();
    }

    public boolean isBookAvailable(String bookId) {
        checkBookExists(bookId);
        Book book = bookRepository.findById(bookId).get();
        return book.getAvailableCopies() > 0;
    }

    public void availablebooktoborrow(String bookId){
        checkBookExists(bookId);
        Book book = bookRepository.findById(bookId).get();
        if (book.getAvailableCopies() <= 0){
            throw new BookNotFoundException("no copies are left of book with ID " + bookId + ".");
        }
    }

    public void maxcopies(String bookId){
        checkBookExists(bookId);
        Book book = bookRepository.findById(bookId).get();
        if (book.getAvailableCopies() == book.getTotalCopies()){
            throw new BookNotFoundException("can not loan book with ID " + bookId + " as it has maximum copies.");
        }
    }
}

