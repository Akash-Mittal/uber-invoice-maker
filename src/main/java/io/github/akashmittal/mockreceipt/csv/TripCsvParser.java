package io.github.akashmittal.mockreceipt.csv;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.csv.CSVException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import io.github.akashmittal.mockreceipt.config.ReceiptProperties;
import io.github.akashmittal.mockreceipt.domain.Trip;

/** Parses the upload format documented in the README into {@link Trip}s. */
@Component
public class TripCsvParser {

    enum Column {
        FROM_ADDRESS_1("fromAddress1"),
        FROM_ADDRESS_2("fromAddress2"),
        TO_ADDRESS_1("toAddress1"),
        TO_ADDRESS_2("toAddress2"),
        AMOUNT("amount"),
        DATE_TIME("dateTime"),
        CAR_TYPE("carType"),
        DRIVER_NAME("driverName");

        final String header;

        Column(String header) {
            this.header = header;
        }
    }

    private static final CSVFormat FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setIgnoreHeaderCase(true)
            .setIgnoreEmptyLines(true)
            .setTrim(true)
            .get();

    private final int maxRows;

    public TripCsvParser(ReceiptProperties properties) {
        this.maxRows = properties.maxRows();
    }

    public List<Trip> parse(InputStream input) {
        try (Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
                CSVParser parser = FORMAT.parse(reader)) {
            requireHeaders(parser);
            List<Trip> trips = new ArrayList<>();
            for (CSVRecord row : parser) {
                if (trips.size() == maxRows) {
                    throw new InvalidCsvException("Too many rows: at most " + maxRows + " are allowed per upload.");
                }
                trips.add(toTrip(row));
            }
            if (trips.isEmpty()) {
                throw new InvalidCsvException("The CSV has a header but no data rows.");
            }
            return trips;
        } catch (CSVException e) {
            throw invalid(e);
        } catch (UncheckedIOException e) {
            // record iteration wraps syntax errors (e.g. an unterminated quote) this way
            if (e.getCause() instanceof CSVException csvError) {
                throw invalid(csvError);
            }
            throw e;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // header problems such as duplicate or blank column names
            throw invalid(e);
        }
    }

    private static InvalidCsvException invalid(Exception e) {
        return new InvalidCsvException("The file is not valid CSV: " + e.getMessage(), e);
    }

    private static void requireHeaders(CSVParser parser) {
        Set<String> present = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        present.addAll(parser.getHeaderNames());
        List<String> missing = new ArrayList<>();
        for (Column column : Column.values()) {
            if (!present.contains(column.header)) {
                missing.add(column.header);
            }
        }
        if (!missing.isEmpty()) {
            throw new InvalidCsvException("Missing CSV column(s): " + String.join(", ", missing));
        }
    }

    private static Trip toTrip(CSVRecord row) {
        return new Trip(
                get(row, Column.FROM_ADDRESS_1),
                get(row, Column.FROM_ADDRESS_2),
                get(row, Column.TO_ADDRESS_1),
                get(row, Column.TO_ADDRESS_2),
                parseAmount(row),
                get(row, Column.DATE_TIME),
                get(row, Column.CAR_TYPE),
                get(row, Column.DRIVER_NAME));
    }

    private static String get(CSVRecord row, Column column) {
        if (!row.isSet(column.header)) {
            throw new InvalidCsvException("Row " + row.getRecordNumber() + " has fewer columns than the header.");
        }
        return row.get(column.header);
    }

    private static BigDecimal parseAmount(CSVRecord row) {
        String raw = get(row, Column.AMOUNT).replace(",", "");
        try {
            BigDecimal amount = new BigDecimal(raw);
            if (amount.signum() < 0) {
                throw new InvalidCsvException("Row " + row.getRecordNumber() + ": amount must not be negative.");
            }
            return amount;
        } catch (NumberFormatException e) {
            throw new InvalidCsvException(
                    "Row " + row.getRecordNumber() + ": amount '" + raw + "' is not a number.", e);
        }
    }
}
