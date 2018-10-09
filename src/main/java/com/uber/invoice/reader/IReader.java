package com.uber.invoice.reader;

import java.io.IOException;

public interface IReader<I, O> {
    String DELIMITER_PIPE = "\\|";

    // Takes some input I and give some output O
    O read(I input) throws IOException;

    default String[] getCSVHeader(String csvHeader) {
        String headers[] = csvHeader.trim().split(DELIMITER_PIPE);
        return headers;
    }

}
