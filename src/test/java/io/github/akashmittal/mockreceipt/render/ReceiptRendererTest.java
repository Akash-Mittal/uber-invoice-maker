package io.github.akashmittal.mockreceipt.render;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import io.github.akashmittal.mockreceipt.config.ReceiptProperties;
import io.github.akashmittal.mockreceipt.domain.Trip;

class ReceiptRendererTest {

    private final ReceiptRenderer renderer = new ReceiptRenderer(new ReceiptProperties(Path.of("unused"), "INR", 10));

    private final Trip trip = new Trip("12 Example Street", "Sampletown", "Demo Station", "Sampletown",
            new BigDecimal("1250.5"), "01/07/2025 at 19:56", "Hatchback", "Sam Placeholder");

    @Test
    void rendersFixedSizeImage() {
        BufferedImage image = renderer.render(trip);

        assertThat(image.getWidth()).isEqualTo(ReceiptRenderer.WIDTH);
        assertThat(image.getHeight()).isEqualTo(ReceiptRenderer.HEIGHT);
    }

    @Test
    void stampsHeaderAndDisclaimerBanner() {
        BufferedImage image = renderer.render(trip);

        assertThat(rgb(image, ReceiptRenderer.WIDTH - 5, 5)).isEqualTo(ReceiptRenderer.HEADER.getRGB());
        assertThat(rgb(image, 5, ReceiptRenderer.HEIGHT - 5)).isEqualTo(ReceiptRenderer.BANNER.getRGB());
    }

    @Test
    void tintsBodyWithWatermark() {
        BufferedImage image = renderer.render(trip);

        // The body background is plain BACKGROUND except where the translucent watermark is drawn.
        long tinted = 0;
        for (int y = 200; y < ReceiptRenderer.HEIGHT - ReceiptRenderer.FOOTER_HEIGHT; y += 4) {
            for (int x = 0; x < ReceiptRenderer.WIDTH; x += 4) {
                Color c = new Color(image.getRGB(x, y));
                if (c.getRed() > c.getGreen() + 10 && c.getRed() > c.getBlue() + 10) {
                    tinted++;
                }
            }
        }
        assertThat(tinted).as("pixels tinted by the red watermark").isGreaterThan(1000);
    }

    @Test
    void handlesEmptyAndVeryLongFields() {
        Trip awkward = new Trip("x".repeat(500), "", "", null, BigDecimal.ZERO, "", "", "y".repeat(500));

        assertThat(renderer.render(awkward)).isNotNull();
    }

    @Test
    void formatsAmountWithCurrencyAndGrouping() {
        assertThat(renderer.formatAmount(new BigDecimal("1234567.5"))).isEqualTo("INR 1,234,567.50");
    }

    private static int rgb(BufferedImage image, int x, int y) {
        return image.getRGB(x, y) & 0xFFFFFF | 0xFF000000;
    }
}
