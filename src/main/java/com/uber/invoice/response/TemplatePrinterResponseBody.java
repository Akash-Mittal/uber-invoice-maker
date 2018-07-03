package com.uber.invoice.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@SuppressWarnings("unused")
@Builder
@ToString
public class TemplatePrinterResponseBody {
	private int numberOfInvoicesPrinted;

}
