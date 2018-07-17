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
    private String fromAddress1;
    private String fromAddress2;
    private String toAddress1;
    private String toAddress2;
    private String amount;
    private String dateTime;
    private String carType;
    private String driverName;
    private String invoiceFileName;

}
