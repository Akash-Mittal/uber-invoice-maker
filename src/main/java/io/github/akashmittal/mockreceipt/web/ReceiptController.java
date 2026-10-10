package io.github.akashmittal.mockreceipt.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.akashmittal.mockreceipt.service.ReceiptService;
import io.github.akashmittal.mockreceipt.storage.ReceiptStorage;

@Controller
public class ReceiptController {

    static final String MESSAGE = "message";
    static final String ERROR = "error";

    private final ReceiptService receiptService;
    private final ReceiptStorage storage;

    public ReceiptController(ReceiptService receiptService, ReceiptStorage storage) {
        this.receiptService = receiptService;
        this.storage = storage;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("receipts", storage.list());
        return "index";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, RedirectAttributes redirect) throws IOException {
        String name = file.getOriginalFilename();
        if (file.isEmpty()) {
            redirect.addFlashAttribute(ERROR, "Please choose a non-empty CSV file.");
        } else if (name == null || !name.toLowerCase(Locale.ROOT).endsWith(".csv")) {
            redirect.addFlashAttribute(ERROR, "Only .csv files are accepted.");
        } else {
            try (InputStream csv = file.getInputStream()) {
                List<String> generated = receiptService.generate(csv);
                redirect.addFlashAttribute(MESSAGE, "Generated " + generated.size() + " mock receipt(s).");
            }
        }
        return "redirect:/";
    }

    @GetMapping("/receipts/{name}")
    public ResponseEntity<Resource> download(@PathVariable String name) {
        Resource receipt = storage.load(name);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(name).build().toString())
                .body(receipt);
    }
}
