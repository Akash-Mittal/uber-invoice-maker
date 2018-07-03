package com.uber.invoice.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uber.invoice.domains.InvoiceItem;
import com.uber.invoice.printer.ImageTemplatePrinter;
import com.uber.invoice.reader.CSVReader;
import com.uber.invoice.response.TemplatePrinterResponseBody;
import com.uber.invoice.response.UberInvoiceServiceResponseBody;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UberInvoiceService {
	@Autowired
	private CSVReader csvReader;
	@Autowired
	private ImageTemplatePrinter imageTemplatePrinter;

	public UberInvoiceServiceResponseBody getInvoices(String filePath, String inputTemplate) throws IOException {
		log.info("Request Recieved ");

		List<InvoiceItem> invoiceList = csvReader.read(filePath);
		TemplatePrinterResponseBody templatePrinterResponseBody = imageTemplatePrinter.print(inputTemplate,
				invoiceList);
		log.info("Response {} ", templatePrinterResponseBody);

		return UberInvoiceServiceResponseBody.builder().numberOfRecordsRead(invoiceList.size())
				.numberOvInvoicesPrinted(templatePrinterResponseBody.getNumberOfInvoicesPrinted()).build();
	}
}
