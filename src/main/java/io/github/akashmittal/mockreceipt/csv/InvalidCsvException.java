package io.github.akashmittal.mockreceipt.csv;

/** The uploaded CSV could not be turned into trips; the message is safe to show to the user. */
public class InvalidCsvException extends RuntimeException {

    public InvalidCsvException(String message) {
        super(message);
    }

    public InvalidCsvException(String message, Throwable cause) {
        super(message, cause);
    }
}
