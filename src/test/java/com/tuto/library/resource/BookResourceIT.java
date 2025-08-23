package com.tuto.library.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import com.tuto.library.domain.Book;
import com.tuto.library.repository.BookRepository;
import com.tuto.library.transferobjetcs.BookTO;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

@QuarkusTest
class BookResourceIT {

        @Inject
        BookRepository bookRepository;

        @Transactional
        Book createBook(String title, String author, int totalCopies, int availableCopies) {
                Book book = new Book(title, author, totalCopies);
                book.setAvailableCopies(availableCopies);
                bookRepository.persist(book);
                return book;
        }

        @Test
        void shouldUpdateBook_whenBookExistsAndValidDataProvided() {
                //TODO 2: implement the tests below
        }
}