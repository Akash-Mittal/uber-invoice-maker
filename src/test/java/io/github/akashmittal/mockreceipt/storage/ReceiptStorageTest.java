package io.github.akashmittal.mockreceipt.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.akashmittal.mockreceipt.config.ReceiptProperties;

class ReceiptStorageTest {

    @TempDir
    Path dir;

    private ReceiptStorage storage() {
        return new ReceiptStorage(new ReceiptProperties(dir, "INR", 10));
    }

    @Test
    void savesAndLoadsGeneratedName() throws Exception {
        ReceiptStorage storage = storage();

        String name = storage.save(new BufferedImage(4, 4, BufferedImage.TYPE_INT_RGB), "Sam O'Placeholder");

        assertThat(name).matches("sam-o-placeholder-[0-9a-f]{8}\\.png");
        assertThat(storage.list()).containsExactly(name);
        assertThat(storage.load(name).contentLength()).isPositive();
    }

    @Test
    void clearsOldReceiptsOnStartupButKeepsOtherFiles() throws Exception {
        Files.writeString(dir.resolve("old-12345678.png"), "x");
        Files.writeString(dir.resolve("notes.txt"), "keep me");

        assertThat(storage().list()).isEmpty();
        assertThat(dir.resolve("notes.txt")).exists();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "../secret.png", "..\\secret.png", "a/b.png", "missing.png", "notes.txt", "" })
    void rejectsUnsafeOrUnknownNames(String name) {
        ReceiptStorage storage = storage();

        assertThatThrownBy(() -> storage.load(name)).isInstanceOf(ReceiptNotFoundException.class);
    }

    @Test
    void slugFallsBackWhenHintHasNoUsableCharacters() {
        assertThat(ReceiptStorage.slug("../../")).isEqualTo("receipt-");
        assertThat(ReceiptStorage.slug(null)).isEqualTo("receipt-");
        assertThat(ReceiptStorage.slug("a".repeat(100))).hasSize(41);
    }
}
