package org.dulab.adapcompounddb.exceptions;

public class EmptySearchResultException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EmptySearchResultException() {
        super("The search result is empty.");
    }

    public EmptySearchResultException(long id) {
        super("Could not find entry wirth ID = " + id);
    }

    public EmptySearchResultException(String message) {
        super(message);
    }

    public EmptySearchResultException(Throwable cause) {
        super(cause);
    }
}
