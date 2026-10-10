package io.github.akashmittal.mockreceipt.config;

import java.nio.file.Path;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Application settings, bound from {@code app.*} in application.properties.
 *
 * @param outputDir  directory the generated receipt images are written to
 * @param currency   ISO-4217 code printed next to every amount
 * @param maxRows    upper bound on rows accepted from a single CSV upload
 */
@Validated
@ConfigurationProperties(prefix = "app")
public record ReceiptProperties(
        @NotNull Path outputDir,
        @NotBlank String currency,
        @Positive @Max(1000) int maxRows) {
}
