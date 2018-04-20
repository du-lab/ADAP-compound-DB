package org.dulab.exceptions;

public class EmptySearchResultException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EmptySearchResultException() {
        super("The search result is empty.");
    }

    public EmptySearchResultException(String message) {
        super(message);
    }

    public EmptySearchResultException(Throwable cause) {
        super(cause);
    }
}
