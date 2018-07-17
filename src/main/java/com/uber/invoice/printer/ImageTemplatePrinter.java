package com.uber.invoice.printer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

        final BufferedImage image = ImageIO
                .read(resourceLoader.getResource("classpath:" + inputTemplate).getInputStream());
        int numberOfInvoicesPrinted = 0;
        for (InvoiceItem invoiceItem : inputData) {
            Graphics uberTemplateImage = image.getGraphics();
            Font font = new Font("Calibri", Font.PLAIN, 48);
            uberTemplateImage.setFont(font);
            uberTemplateImage.setColor(Color.BLACK);

            uberTemplateImage.drawString(invoiceItem.getDateTime(), 42, 312);
            uberTemplateImage.drawString(invoiceItem.getAmount(), 800, 312);
            font = new Font("Calibri Light", Font.PLAIN, 40);
            uberTemplateImage.setFont(font);
            uberTemplateImage.setColor(Color.GRAY);

            uberTemplateImage.drawString(invoiceItem.getCarType(), 42, 395);
            uberTemplateImage.drawString(invoiceItem.getFromAddress1(), 42, 530);
            uberTemplateImage.drawString(invoiceItem.getFromAddress2(), 42, 587);
            uberTemplateImage.drawString(invoiceItem.getToAddress1(), 42, 700);
            uberTemplateImage.drawString(invoiceItem.getToAddress2(), 42, 757);

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

            uberTemplateImage.dispose();

            ImageIO.write(image, "png",
                    new File(fileSystemStorageService.getRootLocation() + "/" + getInvoiceFileName(invoiceItem)));
            numberOfInvoicesPrinted++;
        }

        return TemplatePrinterResponseBody.builder().numberOfInvoicesPrinted(numberOfInvoicesPrinted).build();
    }

    private String getInvoiceFileName(InvoiceItem invoiceItem) {
        StringBuilder builder = new StringBuilder();
        builder.append(invoiceItem.getDriverName()).append(".png");
        return builder.toString();

    }

}
