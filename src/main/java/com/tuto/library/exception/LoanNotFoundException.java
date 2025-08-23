package com.tuto.library.exception;

public class LoanNotFoundException extends LibraryException {
    
    public LoanNotFoundException(String message) {
        super(message);
    }
}