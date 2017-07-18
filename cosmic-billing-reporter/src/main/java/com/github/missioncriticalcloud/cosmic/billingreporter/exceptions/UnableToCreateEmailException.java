package com.github.missioncriticalcloud.cosmic.billingreporter.exceptions;

public class UnableToCreateEmailException extends RuntimeException {
    public UnableToCreateEmailException() {
        super("Failed to create an email");
    }

    public UnableToCreateEmailException(final String message) {
        super(message);
    }

    public UnableToCreateEmailException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UnableToCreateEmailException(final Throwable cause) {
        super(cause);
    }
}
