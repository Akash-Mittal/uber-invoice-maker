package com.uber.invoice;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uber.invoice.response.BaseResponse;
import com.uber.invoice.response.UberInvoiceServiceResponseBody;
import com.uber.invoice.service.UberInvoiceService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class UberInvoiceController {

	@Autowired
	UberInvoiceService uberInvoiceService;

	@RequestMapping(value = "/print", method = RequestMethod.GET)
	public BaseResponse<UberInvoiceServiceResponseBody> getItem(
			@RequestHeader(value = "csvFilePath") String csvFilePath) throws IOException {
		log.info("Request Recieved for CSVFile {} ", csvFilePath);
		UberInvoiceServiceResponseBody uberInvoiceServiceResponseBody = uberInvoiceService.getInvoices(csvFilePath,
				"uber-template-V1.png");

		BaseResponse<UberInvoiceServiceResponseBody> response = new BaseResponse<UberInvoiceServiceResponseBody>(
				uberInvoiceServiceResponseBody, HttpStatus.OK);
		log.info("Response {} ", response);

		return response;

	}

}
