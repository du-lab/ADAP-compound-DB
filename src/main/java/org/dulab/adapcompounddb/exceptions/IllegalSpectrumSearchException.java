package org.dulab.adapcompounddb.exceptions;

public class IllegalSpectrumSearchException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IllegalSpectrumSearchException() {
        super("Cannot search this spectrum");
    }

    public IllegalSpectrumSearchException(String message) {
        super(message);
    }

    public IllegalSpectrumSearchException(Throwable cause) {
        super(cause);
    }
}
