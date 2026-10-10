package io.github.akashmittal.mockreceipt.domain;

import java.math.BigDecimal;
import java.util.Objects;

/** One row of the uploaded CSV: the data printed on a single mock receipt. */
public record Trip(
        String fromAddress1,
        String fromAddress2,
        String toAddress1,
        String toAddress2,
        BigDecimal amount,
        String dateTime,
        String carType,
        String driverName) {

    public Trip {
        Objects.requireNonNull(amount, "amount");
        fromAddress1 = Objects.requireNonNullElse(fromAddress1, "");
        fromAddress2 = Objects.requireNonNullElse(fromAddress2, "");
        toAddress1 = Objects.requireNonNullElse(toAddress1, "");
        toAddress2 = Objects.requireNonNullElse(toAddress2, "");
        dateTime = Objects.requireNonNullElse(dateTime, "");
        carType = Objects.requireNonNullElse(carType, "");
        driverName = Objects.requireNonNullElse(driverName, "");
    }
}
