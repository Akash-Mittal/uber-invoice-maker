package com.uber.invoice.exceptions;

public class BusinessException extends BaseException {

    private static final long serialVersionUID = -4437092401451236312L;

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        // 
    }

    public BusinessException(String message) {
        super(message);
        // 
    }

}
