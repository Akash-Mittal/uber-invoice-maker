package com.uber.invoice.exceptions;

public class ServiceException extends BaseException {

    /**
     * 
     */
    private static final long serialVersionUID = 8576582334455704149L;

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        // 
    }

    public ServiceException(String message) {
        super(message);
        // 
    }

}
