package io.github.akashmittal.mockreceipt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MockReceiptApplication {

    public static void main(String[] args) {
        // Receipts are rendered with Java2D; never try to open a display.
        System.setProperty("java.awt.headless", "true");
        SpringApplication.run(MockReceiptApplication.class, args);
    }
}
