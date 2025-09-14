package com.tuto.library.service;

import com.tuto.library.repository.BookRepository;

public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void borrowBook(final String book1) {
        if (availableCopies <= 0) {
            throw new IllegalStateException("No copies available");
        }
        availableCopies--;
    }
}
public void retBook(final String book1) {
    if (availableCopies < totalCopies )
    availableCopies++;
}
