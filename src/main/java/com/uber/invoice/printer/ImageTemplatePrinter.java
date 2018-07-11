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

@Component
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
        for (InvoiceItem inoiceItem : inputData) {
            Graphics uberTemplateImage = image.getGraphics();
            Font font = new Font("Calibri", Font.PLAIN, 48);
            uberTemplateImage.setFont(font);
            uberTemplateImage.setColor(Color.BLACK);

            uberTemplateImage.drawString(inoiceItem.getDateTime(), 42, 312);
            uberTemplateImage.drawString(inoiceItem.getAmount(), 800, 312);
            font = new Font("Calibri Light", Font.PLAIN, 40);
            uberTemplateImage.setFont(font);
            uberTemplateImage.setColor(Color.GRAY);

            uberTemplateImage.drawString(inoiceItem.getCarType(), 42, 395);
            uberTemplateImage.drawString(inoiceItem.getFromAddress(), 42, 530);
            uberTemplateImage.drawString(inoiceItem.getFromAddress(), 42, 587);
            uberTemplateImage.drawString(inoiceItem.getToAddress(), 42, 700);
            uberTemplateImage.drawString(inoiceItem.getToAddress(), 42, 757);

            font = new Font("Calibri Light", Font.PLAIN, 52);
            uberTemplateImage.setFont(font);
            uberTemplateImage.setColor(Color.BLACK);

            uberTemplateImage.drawString("    Your Ride With " + inoiceItem.getDriverName(), 130, 945);

            uberTemplateImage.drawString("  uberGO Reciept", 43, 1311);
            uberTemplateImage.drawString("  Trip Fare", 43, 1433);
            uberTemplateImage.drawString(inoiceItem.getAmount(), 800, 1433);
            uberTemplateImage.drawString("  Sub Total", 43, 1533);
            uberTemplateImage.drawString(inoiceItem.getAmount(), 800, 1433);
            uberTemplateImage.drawString("  Total", 43, 1633);
            uberTemplateImage.drawString(inoiceItem.getAmount(), 800, 1633);
            uberTemplateImage.drawString("     Cash   ", 43, 1733);
            uberTemplateImage.drawString(inoiceItem.getAmount(), 800, 1733);

            uberTemplateImage.dispose();

            ImageIO.write(image, "png",
                    new File(fileSystemStorageService.getRootLocation() + "/" + inoiceItem.getInvoiceFileName()));
            numberOfInvoicesPrinted++;
        }
        return TemplatePrinterResponseBody.builder().numberOfInvoicesPrinted(numberOfInvoicesPrinted).build();
    }

}
