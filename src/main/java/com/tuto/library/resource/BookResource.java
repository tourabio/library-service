package com.tuto.library.resource;

import com.tuto.library.service.BookService;
import com.tuto.library.transferobjetcs.BookTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class BookResource {

    private final BookService bookService;

    @Inject
    public BookResource(BookService bookService) {
        this.bookService = bookService;
    }

    @GET
    public List<BookTO> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GET
    @Path("/{id}")
    public Response getBookById(@PathParam("id") Long id) {
        BookTO bookTO = bookService.getBookById(id);
        if (bookTO == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(bookTO).build();
    }

    @POST
    public Response createBook(BookTO bookTO) {
        if (bookTO.title() == null || bookTO.title().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Book title is required")
                    .build();
        }
        if (bookTO.author() == null || bookTO.author().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Book author is required")
                    .build();
        }
        if (bookTO.totalCopies() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Total copies must be greater than 0")
                    .build();
        }
        BookTO created = bookService.createBook(bookTO);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateBook(@PathParam("id") Long id, BookTO bookTO) {
        BookTO updated = bookService.updateBook(id, bookTO);
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteBook(@PathParam("id") Long id) {
        boolean deleted = bookService.deleteBook(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}