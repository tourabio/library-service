package com.tuto.library.repository;

import com.tuto.library.domain.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepository {
    
    Optional<Book> findById(String bookId);
    
    Book save(Book book);
    
    List<Book> findAll();
    
    List<Book> findByAuthor(String author);
    
    void deleteById(String bookId);
    
    boolean existsById(String bookId);
}