package com.tuto.library.exception;

public class BookNotFoundException extends LibraryException {
    
    public BookNotFoundException(String message) {
        super(message);
    }
}