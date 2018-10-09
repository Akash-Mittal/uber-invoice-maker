package com.uber.invoice.exceptions;

public class BusinessException extends BaseException {

    private static final long serialVersionUID = -4437092401451236312L;

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public BusinessException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

}
