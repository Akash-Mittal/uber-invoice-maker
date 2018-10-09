package com.uber.invoice.exceptions;

public class StorageFileNotFoundException extends BaseException {

    private static final long serialVersionUID = 6914547968231879011L;

    public StorageFileNotFoundException(String message) {
        super(message);
    }

    public StorageFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}