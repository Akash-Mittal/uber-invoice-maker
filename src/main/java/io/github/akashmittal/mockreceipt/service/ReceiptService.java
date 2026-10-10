package io.github.akashmittal.mockreceipt.service;

import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.github.akashmittal.mockreceipt.csv.TripCsvParser;
import io.github.akashmittal.mockreceipt.domain.Trip;
import io.github.akashmittal.mockreceipt.render.ReceiptRenderer;
import io.github.akashmittal.mockreceipt.storage.ReceiptStorage;

/** Turns an uploaded CSV into stored mock receipt images. */
@Service
public class ReceiptService {

    private static final Logger log = LoggerFactory.getLogger(ReceiptService.class);

    private final TripCsvParser parser;
    private final ReceiptRenderer renderer;
    private final ReceiptStorage storage;

    public ReceiptService(TripCsvParser parser, ReceiptRenderer renderer, ReceiptStorage storage) {
        this.parser = parser;
        this.renderer = renderer;
        this.storage = storage;
    }

    /** @return the file names of the generated receipts, in CSV order */
    public List<String> generate(InputStream csv) {
        List<Trip> trips = parser.parse(csv);
        List<String> names = trips.stream()
                .map(trip -> storage.save(renderer.render(trip), trip.driverName()))
                .toList();
        log.info("Generated {} mock receipt(s)", names.size());
        return names;
    }
}
