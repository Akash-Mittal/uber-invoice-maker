package com.uber.invoice.printer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.uber.invoice.domains.InvoiceItem;
import com.uber.invoice.response.TemplatePrinterResponseBody;
import com.uber.invoice.service.FileSystemStorageService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ImageTemplatePrinter implements IPrinter<String, List<InvoiceItem>, TemplatePrinterResponseBody> {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private FileSystemStorageService fileSystemStorageService;

    @Override
    public TemplatePrinterResponseBody print(String inputTemplate, List<InvoiceItem> inputData) throws IOException {
        int numberOfInvoicesPrinted = 0;
        BufferedImage image;
        InputStream inputStream = null;
        Graphics uberTemplateImage = null;
        try {

            log.info("Got Template Image , File Name {}", inputTemplate);
            for (InvoiceItem invoiceItem : inputData) {
                inputStream = resourceLoader.getResource("classpath:" + inputTemplate).getInputStream();
                image = ImageIO.read(inputStream);
                uberTemplateImage = image.getGraphics();
                Font font = new Font("Calibri", Font.PLAIN, 48);
                uberTemplateImage.setFont(font);
                uberTemplateImage.setColor(Color.BLACK);

                uberTemplateImage.drawString(invoiceItem.getDateTime(), 42, 312);
                uberTemplateImage.drawString(invoiceItem.getAmount(), 800, 312);
                font = new Font("Calibri Light", Font.PLAIN, 40);
                uberTemplateImage.setFont(font);
                uberTemplateImage.setColor(Color.GRAY);

                uberTemplateImage.drawString(invoiceItem.getCarType(), 42, 395);
                uberTemplateImage.drawString(IPrinter.DELIMITERSNSTUFF.TAB.getVal() + invoiceItem.getFromAddress1(), 42,
                        530);
                uberTemplateImage.drawString(IPrinter.DELIMITERSNSTUFF.TAB.getVal() + invoiceItem.getFromAddress2(), 42,
                        587);
                uberTemplateImage.drawString(IPrinter.DELIMITERSNSTUFF.TAB.getVal() + invoiceItem.getToAddress1(), 42,
                        700);
                uberTemplateImage.drawString(IPrinter.DELIMITERSNSTUFF.TAB.getVal() + invoiceItem.getToAddress2(), 42,
                        757);

                font = new Font("Calibri Light", Font.PLAIN, 52);
                uberTemplateImage.setFont(font);
                uberTemplateImage.setColor(Color.BLACK);

                uberTemplateImage.drawString("    Your Ride With " + invoiceItem.getDriverName(), 130, 945);

                uberTemplateImage.drawString("  uberGO Reciept", 43, 1311);
                uberTemplateImage.drawString("  Trip Fare", 43, 1433);
                uberTemplateImage.drawString(invoiceItem.getAmount(), 800, 1433);
                uberTemplateImage.drawString("  Sub Total", 43, 1533);
                uberTemplateImage.drawString(invoiceItem.getAmount(), 800, 1433);
                uberTemplateImage.drawString("  Total", 43, 1633);
                uberTemplateImage.drawString(invoiceItem.getAmount(), 800, 1633);
                uberTemplateImage.drawString("     Cash   ", 43, 1733);
                uberTemplateImage.drawString(invoiceItem.getAmount(), 800, 1733);

                String path = fileSystemStorageService.getRootLocation() + "/" + getInvoiceFileName(invoiceItem);
                ImageIO.write(image, IPrinter.FILEFORMAT.PNG.name(), new File(path));
                uberTemplateImage.dispose();
                log.info("Printed Image with Image Name {} ", path);
                numberOfInvoicesPrinted++;
            }
        } catch (IOException e) {
            log.error("Unknown Error Occured while Writing to Image file Error is {}", e);
        } finally {
            inputStream.close();

        }
        return TemplatePrinterResponseBody.builder().numberOfInvoicesPrinted(numberOfInvoicesPrinted).build();
    }

    private String getInvoiceFileName(InvoiceItem invoiceItem) {
        StringBuilder builder = new StringBuilder();
        builder.append(invoiceItem.getDriverName()).append(IPrinter.FILEFORMAT.PNG.name());
        return builder.toString();

    }

}
