package com.tuto.library.repository;

import com.tuto.library.domain.Book;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BookRepository implements PanacheRepository<Book> {
    
    public Book findByTitleAndAuthor(String title, String author) {
        return find("title = ?1 and author = ?2", title, author).firstResult();
    }
}