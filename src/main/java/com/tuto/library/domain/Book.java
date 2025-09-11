package com.tuto.library.domain;

public class Book {
    private final String id;
    private final String title;
    private final String author;
    private final int totalCopies;
    private int availableCopies;

    // Parameterized constructor
    public Book(String id, String title, String author, int totalCopies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;  // Initially all copies available
    }

    // Default constructor (for testing/flexibility, with minimal defaults)
    public Book() {
        this(null, "Unknown Title", "Unknown Author", 1);  // Sensible defaults
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
        if (availableCopies < 0 || availableCopies > totalCopies) {
            throw new IllegalArgumentException("Available copies must be between 0 and total copies");
        }
        this.availableCopies = availableCopies;
    }

    /**
     * Checks if the book is available (has at least one copy available).
     * @return true if availableCopies > 0, false otherwise.
     */
    public boolean isAvailable() {
        return availableCopies > 0;
    }


}