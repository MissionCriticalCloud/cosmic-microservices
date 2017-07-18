package com.github.missioncriticalcloud.cosmic.billingreporter.exceptions;

public class UnableToSendEmailException extends RuntimeException {
    public UnableToSendEmailException() {
        super("Failed to send an email");
    }

    public UnableToSendEmailException(final String message) {
        super(message);
    }

    public UnableToSendEmailException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UnableToSendEmailException(final Throwable cause) {
        super(cause);
    }
}
