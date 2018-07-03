package com.uber.invoice.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

public class BaseResponse<T> extends ResponseEntity<T> {

	public BaseResponse(HttpStatus status) {
		super(status);
	}

	public BaseResponse(@Nullable T body, HttpStatus status) {
		super(body, null, status);
	}

}
