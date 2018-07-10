package com.uber.invoice.domains;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class InvoiceItem {
	private String fromAddress;
	private String toAddress;
	private String amount;
	private String dateTime;
	private String carType;
	private String driverName;
	private String invoiceFileName;

}
