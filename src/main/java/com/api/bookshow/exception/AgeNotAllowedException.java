package com.api.bookshow.exception;

public class AgeNotAllowedException extends Exception {
    private String message;

    public AgeNotAllowedException(String message) {
        super(message);
    }
}
