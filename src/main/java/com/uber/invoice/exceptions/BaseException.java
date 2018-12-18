package com.uber.invoice.exceptions;

public class BaseException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 2185973013321090643L;

    public BaseException(String message, Throwable cause) {
        super(message, cause);
        // 
    }

    public BaseException(String message) {
        super(message);
        // 
    }

}
