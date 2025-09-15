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

    // Vérifie si un livre est disponible (au moins 1 copie)
    public boolean isAvailable(String bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + bookId + " not found"));
        return book.getAvailableCopies() > 0;
    }

    // Permet d'emprunter un livre (diminue le nombre de copies disponibles)
    public Book borrowBook(String bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + bookId + " not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("Book with ID " + bookId + " is not available");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        return bookRepository.save(book);
    }

    // Permet de rendre un livre (augmente le nombre de copies disponibles)
    public Book returnBook(String bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + bookId + " not found"));

        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new InvalidLoanOperationException("Cannot return book with ID " + bookId + ", already at max copies");
        }

        book.setAvailableCopies(book.getAvailableCopies() + 1);
        return bookRepository.save(book);
    }
}
