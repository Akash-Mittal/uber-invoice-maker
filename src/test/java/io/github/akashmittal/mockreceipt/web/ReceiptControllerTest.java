package io.github.akashmittal.mockreceipt.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import io.github.akashmittal.mockreceipt.storage.ReceiptStorage;

@SpringBootTest
@AutoConfigureMockMvc
class ReceiptControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ReceiptStorage storage;

    @Test
    void homePageRenders() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Mock Receipt Maker")));
    }

    @Test
    void homePageRendersWhenServedWithoutController() throws Exception {
        // "/;jsessionid=..." bypasses the controller and hits Boot's welcome-page mapping, so the model is empty
        mvc.perform(get("/;jsessionid=abc"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Mock Receipt Maker")));
    }

    @Test
    void uploadGeneratesDownloadableReceipts() throws Exception {
        byte[] sample;
        try (InputStream in = getClass().getResourceAsStream("/sample.csv")) {
            sample = in.readAllBytes();
        }

        mvc.perform(multipart("/upload").file(new MockMultipartFile("file", "trips.csv", "text/csv", sample)))
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute(ReceiptController.MESSAGE, "Generated 2 mock receipt(s)."));

        String name = storage.list().getFirst();
        byte[] png = mvc.perform(get("/receipts/" + name))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"" + name + "\""))
                .andReturn().getResponse().getContentAsByteArray();
        assertThat(ImageIO.read(new ByteArrayInputStream(png))).isNotNull();

        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/receipts/" + name)));
    }

    @Test
    void rejectsNonCsvUpload() throws Exception {
        mvc.perform(multipart("/upload").file(new MockMultipartFile("file", "trips.xlsx", null, new byte[] { 1 })))
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute(ReceiptController.ERROR, "Only .csv files are accepted."));
    }

    @Test
    void showsCsvErrorsOnHomePage() throws Exception {
        byte[] csv = "amount\n5\n".getBytes(StandardCharsets.UTF_8);

        mvc.perform(multipart("/upload").file(new MockMultipartFile("file", "bad.csv", "text/csv", csv)))
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute(ReceiptController.ERROR,
                        startsWith("Missing CSV column(s)")));
    }

    @Test
    void unknownOrTraversingReceiptNamesAre404() throws Exception {
        mvc.perform(get("/receipts/missing.png")).andExpect(status().isNotFound());
        mvc.perform(get("/receipts/..%2Fpom.xml")).andExpect(status().isNotFound());
    }
}
