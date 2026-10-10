package io.github.akashmittal.mockreceipt.storage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import io.github.akashmittal.mockreceipt.config.ReceiptProperties;

/**
 * Keeps generated receipt images in a single flat directory.
 * <p>
 * File names are always generated here, never taken from user input, and every lookup is checked
 * against a strict pattern and the storage root so requests cannot escape the directory.
 */
@Component
public class ReceiptStorage {

    private static final String EXTENSION = ".png";
    private static final Pattern SAFE_NAME = Pattern.compile("[A-Za-z0-9-]{1,80}\\.png");
    private static final Pattern UNSAFE_CHARS = Pattern.compile("[^a-z0-9]+");
    private static final int MAX_SLUG_LENGTH = 40;

    private final Path root;

    public ReceiptStorage(ReceiptProperties properties) {
        this.root = properties.outputDir().toAbsolutePath().normalize();
        init();
    }

    /** Creates the directory and removes receipts left over from a previous run. */
    private void init() {
        try {
            Files.createDirectories(root);
            try (DirectoryStream<Path> stale = Files.newDirectoryStream(root, "*" + EXTENSION)) {
                for (Path file : stale) {
                    Files.deleteIfExists(file);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not initialise receipt storage at " + root, e);
        }
    }

    /**
     * Writes {@code image} as a PNG and returns the generated file name.
     *
     * @param hint human-readable text (e.g. the driver's name) used only to make the name recognisable
     */
    public String save(BufferedImage image, String hint) {
        String name = slug(hint) + UUID.randomUUID().toString().substring(0, 8) + EXTENSION;
        try {
            ImageIO.write(image, "png", root.resolve(name).toFile());
        } catch (IOException e) {
            throw new UncheckedIOException("Could not write receipt " + name, e);
        }
        return name;
    }

    /** Names of all stored receipts, newest first. */
    public List<String> list() {
        try (Stream<Path> files = Files.list(root)) {
            List<Path> receipts = new ArrayList<>(files
                    .filter(path -> SAFE_NAME.matcher(path.getFileName().toString()).matches())
                    .toList());
            receipts.sort(Comparator.comparing(ReceiptStorage::lastModified).reversed());
            return receipts.stream().map(path -> path.getFileName().toString()).toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Could not list receipts", e);
        }
    }

    public Resource load(String name) {
        if (name == null || !SAFE_NAME.matcher(name).matches()) {
            throw new ReceiptNotFoundException(name);
        }
        Path file = root.resolve(name).normalize();
        if (!file.getParent().equals(root) || !Files.isRegularFile(file)) {
            throw new ReceiptNotFoundException(name);
        }
        return new FileSystemResource(file);
    }

    static String slug(String hint) {
        String slug = UNSAFE_CHARS.matcher(hint == null ? "" : hint.toLowerCase(Locale.ROOT)).replaceAll("-");
        slug = slug.replaceAll("^-+|-+$", "");
        if (slug.length() > MAX_SLUG_LENGTH) {
            slug = slug.substring(0, MAX_SLUG_LENGTH).replaceAll("-+$", "");
        }
        return slug.isEmpty() ? "receipt-" : slug + "-";
    }

    private static FileTime lastModified(Path path) {
        try {
            return Files.getLastModifiedTime(path);
        } catch (IOException e) {
            return FileTime.fromMillis(0);
        }
    }
}
