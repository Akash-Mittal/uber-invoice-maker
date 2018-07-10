package com.uber.invoice.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class UberInvoiceServiceResponseBody {

	private int numberOfRecordsRead;
	private int numberOvInvoicesPrinted;

}
