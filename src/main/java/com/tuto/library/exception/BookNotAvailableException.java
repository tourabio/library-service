package com.tuto.library.exception;

public class BookNotAvailableException extends LibraryException {
    
    public BookNotAvailableException(String message) {
        super(message);
    }
}