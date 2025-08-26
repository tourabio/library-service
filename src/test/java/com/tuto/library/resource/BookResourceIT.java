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
        void testGetAllBooks() {
                given()
                                .when().get("/books")
                                .then()
                                .statusCode(200);
        }

        @Test
        void testCreateBook() {
                BookTO book = new BookTO("Test Book", "Test Author", 5, 5);

                given()
                                .contentType(ContentType.JSON)
                                .body(book)
                                .when().post("/books")
                                .then()
                                .statusCode(201)
                                .body("title", is("Test Book"))
                                .body("author", is("Test Author"))
                                .body("totalCopies", is(5))
                                .body("availableCopies", is(5));
        }

        @Test
        void testCreateBookWithMissingTitle() {
                BookTO book = new BookTO(null, "Test Author", 5, 5);

                given()
                                .contentType(ContentType.JSON)
                                .body(book)
                                .when().post("/books")
                                .then()
                                .statusCode(400);
        }

        @Test
        void testGetBookById() {
                Book persistedBook = createBook("Another Test Book", "Another Test Author", 3, 3);

                given()
                                .when().get("/books/{id}", persistedBook.getId())
                                .then()
                                .statusCode(200)
                                .body("title", is("Another Test Book"))
                                .body("author", is("Another Test Author"));
        }

        @Test
        void testGetBookByIdNotFound() {
                given()
                                .when().get("/books/999")
                                .then()
                                .statusCode(404);
        }

        @Test
        void testUpdateBook() {
                Book persistedBook = createBook("Original Title", "Original Author", 2, 2);

                BookTO updatedBook = new BookTO("Updated Title", "Updated Author", 4, 4);

                given()
                                .contentType(ContentType.JSON)
                                .body(updatedBook)
                                .when().put("/books/{id}", persistedBook.getId())
                                .then()
                                .statusCode(200)
                                .body("title", is("Updated Title"))
                                .body("author", is("Updated Author"))
                                .body("totalCopies", is(4));
        }

        @Test
        void testDeleteBook() {
                Book persistedBook = createBook("Book to Delete", "Delete Author", 1, 1);

                given()
                                .when().delete("/books/{id}", persistedBook.getId())
                                .then()
                                .statusCode(204);

                given()
                                .when().get("/books/{id}", persistedBook.getId())
                                .then()
                                .statusCode(404);
        }
}