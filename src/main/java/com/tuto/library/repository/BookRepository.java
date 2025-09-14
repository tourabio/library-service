package com.tuto.library.repository;

import com.tuto.library.domain.Book;
import java.util.Optional;

public interface BookRepository {

    Optional<Book> findById(Long bookId);

    Book save(Book book);
}