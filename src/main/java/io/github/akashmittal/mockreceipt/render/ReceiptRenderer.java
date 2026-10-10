package io.github.akashmittal.mockreceipt.render;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.Locale;

import org.springframework.stereotype.Component;

import io.github.akashmittal.mockreceipt.config.ReceiptProperties;
import io.github.akashmittal.mockreceipt.domain.Trip;

/**
 * Draws a generic, unbranded ride receipt for a {@link Trip}.
 * <p>
 * Every image carries a header label, a footer banner and a tiled diagonal watermark that mark it as a
 * mock-up, so the output cannot be mistaken for a genuine receipt from any real company.
 */
@Component
public class ReceiptRenderer {

    public static final int WIDTH = 1080;
    public static final int HEIGHT = 1500;
    public static final String BRAND = "SAMPLE RIDES";
    public static final String WATERMARK = "SAMPLE";
    public static final String DISCLAIMER = "MOCK-UP ONLY — NOT A REAL RECEIPT — NOT VALID FOR EXPENSES";

    static final Color BACKGROUND = new Color(0xF7F7F7);
    static final Color HEADER = new Color(0x263238);
    static final Color BANNER = new Color(0xC62828);
    static final int FOOTER_HEIGHT = 110;

    private static final int MARGIN = 60;
    private static final Color TEXT = new Color(0x212121);
    private static final Color MUTED = new Color(0x757575);
    private static final Color RULE = new Color(0xDDDDDD);

    private static final Font BRAND_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 52);
    private static final Font LABEL_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 26);
    private static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 44);
    private static final Font BODY_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 36);
    private static final Font WATERMARK_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 150);

    private final String currency;

    public ReceiptRenderer(ReceiptProperties properties) {
        this.currency = properties.currency();
    }

    public BufferedImage render(Trip trip) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(BACKGROUND);
            g.fillRect(0, 0, WIDTH, HEIGHT);

            drawHeader(g);
            drawSummary(g, trip);
            drawRoute(g, trip);
            drawFare(g, trip);
            drawWatermark(g);
            drawFooter(g);
        } finally {
            g.dispose();
        }
        return image;
    }

    String formatAmount(BigDecimal amount) {
        return String.format(Locale.ROOT, "%s %,.2f", currency, amount);
    }

    private static void drawHeader(Graphics2D g) {
        g.setColor(HEADER);
        g.fillRect(0, 0, WIDTH, 180);
        g.setColor(Color.WHITE);
        g.setFont(BRAND_FONT);
        g.drawString(BRAND, MARGIN, 95);
        g.setFont(LABEL_FONT);
        g.drawString("MOCK RECEIPT · FOR TESTING AND DESIGN MOCK-UPS", MARGIN, 145);
    }

    private void drawSummary(Graphics2D g, Trip trip) {
        g.setColor(TEXT);
        g.setFont(TITLE_FONT);
        String total = formatAmount(trip.amount());
        int totalWidth = g.getFontMetrics().stringWidth(total);
        drawRight(g, total, 280);
        drawFitted(g, trip.dateTime(), MARGIN, 280, WIDTH - 2 * MARGIN - totalWidth - 40);

        g.setColor(MUTED);
        g.setFont(BODY_FONT);
        drawFitted(g, trip.carType(), MARGIN, 340, WIDTH - 2 * MARGIN);
        drawRule(g, 390);
    }

    private static void drawRoute(Graphics2D g, Trip trip) {
        g.setColor(TEXT);
        g.fillOval(MARGIN, 448, 20, 20);
        g.fillRect(MARGIN, 618, 20, 20);
        g.setStroke(new BasicStroke(3));
        g.drawLine(MARGIN + 10, 476, MARGIN + 10, 610);

        int textX = MARGIN + 50;
        int maxWidth = WIDTH - textX - MARGIN;
        g.setFont(BODY_FONT);
        drawFitted(g, trip.fromAddress1(), textX, 470, maxWidth);
        drawFitted(g, trip.toAddress1(), textX, 640, maxWidth);
        g.setColor(MUTED);
        drawFitted(g, trip.fromAddress2(), textX, 520, maxWidth);
        drawFitted(g, trip.toAddress2(), textX, 690, maxWidth);
        drawRule(g, 750);

        g.setColor(TEXT);
        drawFitted(g, "Driver: " + trip.driverName(), MARGIN, 820, WIDTH - 2 * MARGIN);
        drawRule(g, 870);
    }

    private void drawFare(Graphics2D g, Trip trip) {
        String amount = formatAmount(trip.amount());
        g.setColor(TEXT);
        g.setFont(TITLE_FONT);
        g.drawString("Fare breakdown", MARGIN, 960);

        g.setFont(BODY_FONT);
        int y = 1040;
        for (String label : new String[] { "Trip fare", "Subtotal" }) {
            g.drawString(label, MARGIN, y);
            drawRight(g, amount, y);
            y += 70;
        }
        drawRule(g, y - 35);
        g.setFont(TITLE_FONT);
        g.drawString("Total", MARGIN, y + 20);
        drawRight(g, amount, y + 20);

        g.setFont(BODY_FONT);
        g.setColor(MUTED);
        g.drawString("Paid in cash", MARGIN, y + 90);
    }

    /** Tiles a translucent, rotated "SAMPLE" over the whole receipt so it cannot simply be cropped out. */
    private static void drawWatermark(Graphics2D g) {
        AffineTransform originalTransform = g.getTransform();
        Composite originalComposite = g.getComposite();
        try {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
            g.setColor(BANNER);
            g.setFont(WATERMARK_FONT);
            g.rotate(Math.toRadians(-30), WIDTH / 2.0, HEIGHT / 2.0);
            int step = g.getFontMetrics().stringWidth(WATERMARK) + 120;
            int row = 0;
            for (int y = -HEIGHT / 2; y < HEIGHT * 3 / 2; y += 320, row++) {
                int offset = row % 2 == 0 ? 0 : step / 2;
                for (int x = -WIDTH - offset; x < WIDTH * 2; x += step) {
                    g.drawString(WATERMARK, x, y);
                }
            }
        } finally {
            g.setTransform(originalTransform);
            g.setComposite(originalComposite);
        }
    }

    private static void drawFooter(Graphics2D g) {
        g.setColor(BANNER);
        g.fillRect(0, HEIGHT - FOOTER_HEIGHT, WIDTH, FOOTER_HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(LABEL_FONT);
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(DISCLAIMER, (WIDTH - metrics.stringWidth(DISCLAIMER)) / 2, HEIGHT - 45);
    }

    private static void drawRight(Graphics2D g, String text, int y) {
        g.drawString(text, WIDTH - MARGIN - g.getFontMetrics().stringWidth(text), y);
    }

    private static void drawRule(Graphics2D g, int y) {
        Color previous = g.getColor();
        g.setColor(RULE);
        g.setStroke(new BasicStroke(2));
        g.drawLine(MARGIN, y, WIDTH - MARGIN, y);
        g.setColor(previous);
    }

    /** Draws {@code text}, truncating it with an ellipsis if it would overflow {@code maxWidth}. */
    private static void drawFitted(Graphics2D g, String text, int x, int y, int maxWidth) {
        if (text.isEmpty()) {
            return;
        }
        FontMetrics metrics = g.getFontMetrics();
        String fitted = text;
        if (metrics.stringWidth(fitted) > maxWidth) {
            int end = fitted.length();
            while (end > 0 && metrics.stringWidth(fitted.substring(0, end) + "…") > maxWidth) {
                end--;
            }
            fitted = fitted.substring(0, end) + "…";
        }
        g.drawString(fitted, x, y);
    }
}
