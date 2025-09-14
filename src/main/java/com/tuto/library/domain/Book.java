package com.tuto.library.domain;

import com.tuto.library.exception.BookNotAvailableException;
import com.tuto.library.exception.InvalidLoanOperationException;

public class Book {
    private final String id;
    private final String title;
    private final String author;
    private final int totalCopies;
    private int availableCopies;

    public Book(String id, String title, String author, int totalCopies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    // ✅ for tests
    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public void borrow() {
        if (availableCopies <= 0) {
            throw new BookNotAvailableException("Book not available: " + id);
        }
        availableCopies--;
    }

    public void returnBook() {
        if (availableCopies >= totalCopies) {
            throw new InvalidLoanOperationException("Book already at max copies: " + id);
        }
        availableCopies++;
    }
}
