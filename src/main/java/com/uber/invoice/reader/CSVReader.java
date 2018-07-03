package com.uber.invoice.reader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.uber.invoice.domains.InvoiceItem;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CSVReader implements IReader<String, List<InvoiceItem>> {
	@Autowired
	private ResourceLoader resourceLoader;
	@Value("${com.uber.invoice.cv.headers}")
	private String csvHeader;

	@Override
	public List<InvoiceItem> read(String filePath) throws IOException {
		Resource resource = resourceLoader.getResource(filePath);
		List<InvoiceItem> invoiceItemList = new ArrayList<>();
		CSVParser csvParser = null;
		try (Reader reader = new InputStreamReader(resource.getInputStream());) {
			csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withFirstRecordAsHeader()
					.withIgnoreHeaderCase().withTrim());
			String headers[] = getCSVHeader();
			for (CSVRecord csvRecord : csvParser) {
				invoiceItemList.add(InvoiceItem.builder().fromAddress(csvRecord.get(headers[0]))
						.toAddress(csvRecord.get(headers[1])).amount(csvRecord.get(headers[2]))
						.dateTime(csvRecord.get(headers[3])).carType(csvRecord.get(headers[4]))
						.driverName(csvRecord.get(headers[5])).invoiceFileName(csvRecord.get(headers[6])).build());

			}
			;
			log.info("invoiceItemList size {}", invoiceItemList.size());

		} finally {
			// CSV Parser implements closeable dont know why editor is warning on this
			if (csvParser != null)
				csvParser.close();
		}

		invoiceItemList.stream().forEach(invoiceItem -> {
			log.info(invoiceItem.toString());
		});
		return invoiceItemList;

	}

	private String[] getCSVHeader() {
		String headers[] = csvHeader.split("|");
		return headers;
	}
}
