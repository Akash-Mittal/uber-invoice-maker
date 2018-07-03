package com.uber.invoice.response;

import org.springframework.http.HttpStatus;

public class TemplatePrinterResponse extends BaseResponse<TemplatePrinterResponseBody> {

	public TemplatePrinterResponse(HttpStatus status) {
		super(status);
	}

	public TemplatePrinterResponse(TemplatePrinterResponseBody body, HttpStatus status) {
		super(body, status);
	}

}
