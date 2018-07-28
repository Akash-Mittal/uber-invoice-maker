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
import org.springframework.stereotype.Component;

import com.uber.invoice.domains.InvoiceItem;
import com.uber.invoice.service.StorageService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CSVReader implements IReader<String, List<InvoiceItem>> {

    @Value("${com.uber.invoice.cv.headers}")
    private String csvHeader;

    private final StorageService storageService;

    @Autowired
    public CSVReader(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    public List<InvoiceItem> read(String filePath) throws IOException {
        Resource resource = storageService.loadAsResource(filePath);

        List<InvoiceItem> invoiceItemList = new ArrayList<>();
        CSVParser csvParser = null;
        try (Reader reader = new InputStreamReader(resource.getInputStream());) {
            csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withFirstRecordAsHeader()
                    .withIgnoreHeaderCase().withTrim());
            String headers[] = getCSVHeader();
            for (CSVRecord csvRecord : csvParser) {
                invoiceItemList.add(InvoiceItem.builder().fromAddress1(csvRecord.get(headers[0]))
                        .fromAddress2(csvRecord.get(headers[1])).toAddress1(csvRecord.get(headers[2]))
                        .toAddress2(csvRecord.get(headers[3])).amount(csvRecord.get(headers[4]))
                        .dateTime(csvRecord.get(headers[5])).carType(csvRecord.get(headers[6]))
                        .driverName(csvRecord.get(headers[7])).invoiceFileName(csvRecord.get(headers[8])).build());

            }
            ;
            log.info("invoiceItemList size {}", invoiceItemList.size());

        } finally {
            // CSV Parser implements closeable dont know why editor is warning
            // on this
            if (csvParser != null)
                csvParser.close();
        }

        invoiceItemList.stream().forEach(invoiceItem -> {
            log.info(invoiceItem.toString());
        });
        return invoiceItemList;

    }

    private String[] getCSVHeader() {
        String headers[] = csvHeader.trim().split("\\|");
        return headers;
    }

}
