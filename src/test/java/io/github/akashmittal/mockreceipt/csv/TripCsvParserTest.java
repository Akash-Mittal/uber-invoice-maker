package io.github.akashmittal.mockreceipt.csv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.akashmittal.mockreceipt.config.ReceiptProperties;
import io.github.akashmittal.mockreceipt.domain.Trip;

class TripCsvParserTest {

    private static final String HEADER = "fromAddress1,fromAddress2,toAddress1,toAddress2,amount,dateTime,carType,driverName\n";

    private final TripCsvParser parser = new TripCsvParser(new ReceiptProperties(Path.of("unused"), "INR", 3));

    @Test
    void parsesSampleFile() throws Exception {
        try (InputStream sample = getClass().getResourceAsStream("/sample.csv")) {
            List<Trip> trips = parser.parse(sample);

            assertThat(trips).hasSize(2);
            Trip first = trips.getFirst();
            assertThat(first.fromAddress1()).isEqualTo("12 Example Street");
            assertThat(first.toAddress2()).isEqualTo("Sampletown 100002");
            assertThat(first.amount()).isEqualByComparingTo("1200");
            assertThat(first.driverName()).isEqualTo("Alex Driver");
            assertThat(trips.get(1).amount()).isEqualByComparingTo(new BigDecimal("1250.50"));
        }
    }

    @Test
    void headersAreCaseInsensitiveAndExtraColumnsIgnored() {
        String csv = "FROMADDRESS1,fromaddress2,toAddress1,toAddress2,Amount,dateTime,carType,driverName,invoiceFileName\n"
                + "a,b,c,d,10,today,car,Pat,old.png\n";

        assertThat(parser.parse(stream(csv))).singleElement()
                .satisfies(trip -> assertThat(trip.driverName()).isEqualTo("Pat"));
    }

    @Test
    void reportsMissingColumns() {
        assertThatThrownBy(() -> parser.parse(stream("fromAddress1,amount\nx,1\n")))
                .isInstanceOf(InvalidCsvException.class)
                .hasMessageContaining("toAddress1")
                .hasMessageContaining("driverName")
                .hasMessageNotContaining("amount");
    }

    @Test
    void rejectsNonNumericAmountWithRowNumber() {
        assertThatThrownBy(() -> parser.parse(stream(HEADER + "a,b,c,d,10,t,car,x\na,b,c,d,ten,t,car,y\n")))
                .isInstanceOf(InvalidCsvException.class)
                .hasMessage("Row 2: amount 'ten' is not a number.");
    }

    @Test
    void rejectsNegativeAmount() {
        assertThatThrownBy(() -> parser.parse(stream(HEADER + "a,b,c,d,-5,t,car,x\n")))
                .isInstanceOf(InvalidCsvException.class)
                .hasMessageContaining("must not be negative");
    }

    @Test
    void rejectsShortRows() {
        assertThatThrownBy(() -> parser.parse(stream(HEADER + "a,b,c\n")))
                .isInstanceOf(InvalidCsvException.class)
                .hasMessageContaining("fewer columns");
    }

    @Test
    void rejectsHeaderOnlyFile() {
        assertThatThrownBy(() -> parser.parse(stream(HEADER)))
                .isInstanceOf(InvalidCsvException.class)
                .hasMessageContaining("no data rows");
    }

    @Test
    void enforcesRowLimit() {
        String row = "a,b,c,d,1,t,car,x\n";
        assertThat(parser.parse(stream(HEADER + row.repeat(3)))).hasSize(3);
        assertThatThrownBy(() -> parser.parse(stream(HEADER + row.repeat(4))))
                .isInstanceOf(InvalidCsvException.class)
                .hasMessageContaining("at most 3");
    }

    @Test
    void wrapsMalformedCsv() {
        assertThatThrownBy(() -> parser.parse(stream(HEADER + "a,\"unterminated,c,d,1,t,car,x\n")))
                .isInstanceOf(InvalidCsvException.class)
                .hasMessageStartingWith("The file is not valid CSV");
    }

    private static InputStream stream(String csv) {
        return new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));
    }
}
