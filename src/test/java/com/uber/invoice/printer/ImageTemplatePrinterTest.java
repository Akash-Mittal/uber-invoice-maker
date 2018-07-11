package com.uber.invoice.printer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.uber.invoice.domains.InvoiceItem;
import com.uber.invoice.response.TemplatePrinterResponseBody;
import com.uber.invoice.service.FileSystemStorageService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class ImageTemplatePrinterTest {

    public static final String TEMPLATE_FILE_NAME = "uber-template-V1.png";

    @Autowired
    private ImageTemplatePrinter imageTemplatePrinter;

    @Autowired
    private FileSystemStorageService fileSystemStorageService;

    private List<InvoiceItem> invoiceItems;

    @Before
    public void setUp() throws Exception {
        InvoiceItem invoiceItem = InvoiceItem.builder().carType("s").amount("s").dateTime("s").driverName("s")
                .fromAddress("s").toAddress("s").invoiceFileName("invoice.png").build();
        invoiceItems = new ArrayList<>();
        invoiceItems.add(invoiceItem);
        fileSystemStorageService.deleteAll();
        fileSystemStorageService.init();

    }

    @Test
    public void testPrint() throws Exception {
        assertThat(imageTemplatePrinter).isNotNull();

        TemplatePrinterResponseBody templatePrinterResponseBody = imageTemplatePrinter.print(TEMPLATE_FILE_NAME,
                invoiceItems);
        assertThat(templatePrinterResponseBody).isNotNull();
        assertThat(templatePrinterResponseBody.getNumberOfInvoicesPrinted()).isEqualTo(1);
    }

}
