package com.uber.invoice.reader;

import java.io.IOException;

public interface IReader<I, O> {

    // Takes some input I and give some output O
    O read(I input) throws IOException;

    default String[] getCSVHeader(String csvHeader) {
        String headers[] = csvHeader.trim().split("\\|");
        return headers;
    }

}
