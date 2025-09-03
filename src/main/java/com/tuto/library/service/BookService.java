package com.tuto.library.service;

import com.tuto.library.domain.Book;
import com.tuto.library.exception.BookNotAvailableException;
import com.tuto.library.exception.BookNotFoundException;
import com.tuto.library.exception.InvalidLoanOperationException;
import com.tuto.library.repository.BookRepository;
import com.tuto.library.transferobjetcs.BookTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookTO> getAllBooks() {
        return bookRepository.listAll().stream().map(this::toTO).toList();
    }

    public List<BookTO> getAvailableBooks() {
        return bookRepository.findAvailableBooks().stream().map(this::toTO).toList();
    }

    public BookTO getBookById(Long id) {
        return bookRepository.findByIdOptional(id).map(this::toTO).orElse(null);
    }

    public BookTO createBook(BookTO bookTO) {
        Book book = toEntity(bookTO);
        bookRepository.persist(book);
        return toTO(book);
    }

    public BookTO updateBook(Long id, BookTO bookTO) {
        Book existingBook = bookRepository.findByIdOptional(id).orElse(null);
        if (existingBook == null) {
            return null;
        }
        existingBook.setTitle(bookTO.title());
        existingBook.setAuthor(bookTO.author());
        existingBook.setTotalCopies(bookTO.totalCopies());
        existingBook.setAvailableCopies(bookTO.availableCopies());
        bookRepository.getEntityManager().merge(existingBook);
        return toTO(existingBook);
    }

    public boolean deleteBook(Long id) {
        return bookRepository.deleteById(id);
    }

    private BookTO toTO(Book book) {
        if (book == null) {
            return null;
        }
        return new BookTO(book.getTitle(), book.getAuthor(), book.getTotalCopies(), book.getAvailableCopies());
    }

    private Book toEntity(BookTO bookTO) {
        Book book = new Book(bookTO.title(), bookTO.author(), bookTO.totalCopies());
        book.setAvailableCopies(bookTO.availableCopies());
        return book;
    }

    public boolean ValidateBookIsAvailable(Book book) {
        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("Book with ID " + book.getId() + " is not available.");
        }
        return true;
    }

    public Book borrowBook(Long bookId) {
        Book book = findBookById(bookId);
        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("No copies available for book: " + book.getTitle());
        }
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        return book;
    }

    public void returnBook(Long bookId) {
        Book book = findBookById(bookId);
        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new InvalidLoanOperationException(
                    "Cannot return more copies than total for book: " + book.getTitle());
        }
        book.setAvailableCopies(book.getAvailableCopies() + 1);
    }

    public Book findBookById(Long id) {
        return bookRepository.findByIdOptional(id)
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + id + " not found."));
    }

    public Optional<Book> findBookByTitleAndAuthor(String title, String author) {
        return bookRepository.find("title = ?1 and author = ?2", title, author)
                .firstResultOptional();
    }
}
