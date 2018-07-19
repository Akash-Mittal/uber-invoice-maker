package com.uber.invoice.reader;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.uber.invoice.domains.InvoiceItem;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Ignore
public class CSVReaderTest {
    public static final String CSV_FILE_NAME = "sample.csv";

    @Autowired
    private CSVReader csvReader;

    @Test
    public void testRead() throws Exception {
        List<InvoiceItem> invoiceItems = csvReader.read(CSV_FILE_NAME);
        assertThat(invoiceItems).isNotNull();

    }

}
