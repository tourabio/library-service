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

    /**
     * Retourne un livre par son ID.
     * Si le livre n'existe pas, lève une BookNotFoundException.
     */
    public Book returnBook(String bookId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isEmpty()) {
            throw new BookNotFoundException("book with ID " + bookId + " not found");
        }
        Book book = bookOpt.get();
        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new InvalidLoanOperationException("Cannot return book with ID " + bookId + ": all copies are already available");
        }
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        return book;
            }
    /**
     * Vérifie si un livre est disponible par son ID.
     * Retourne true si le livre existe et est disponible, sinon false ou lève une exception si non trouvé.
     */
    public boolean isBookAvailable(String bookId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isEmpty()) {
            throw new BookNotFoundException("book with ID " + bookId + " not found");
        }
        return bookOpt.get().isAvailable();  // Now uses the corrected method
    }

    /**
     * Emprunte un livre par son ID, réduisant le nombre de copies disponibles.
     * Lève une BookNotFoundException si le livre n'existe pas.
     */
    public void borrowBook(String bookId) {
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        if (bookOpt.isEmpty()) {
            throw new BookNotFoundException("book with ID " + bookId + " not found");
        }
        Book book = bookOpt.get();
        if (!book.isAvailable()) {
            throw new BookNotAvailableException("book with ID " + bookId + " has no available copies");
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
    }



}



