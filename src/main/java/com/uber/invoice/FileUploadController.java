package com.uber.invoice;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uber.invoice.exceptions.StorageFileNotFoundException;
import com.uber.invoice.response.BaseResponse;
import com.uber.invoice.response.UberInvoiceServiceResponseBody;
import com.uber.invoice.service.StorageService;
import com.uber.invoice.service.UberInvoiceService;

import lombok.extern.slf4j.Slf4j;



@Controller
@Slf4j
public class FileUploadController {
	@Autowired
	private UberInvoiceService uberInvoiceService;
	
    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping("/dump/files/{filename:.+}")
    @ResponseBody
    public BaseResponse<UberInvoiceServiceResponseBody> serveFile(@PathVariable String filename) throws IOException {

		log.info("Request Recieved for CSVFile {} ", filename);
		UberInvoiceServiceResponseBody uberInvoiceServiceResponseBody = uberInvoiceService.getInvoices(filename,
				"uber-template-V1.png");

		BaseResponse<UberInvoiceServiceResponseBody> response = new BaseResponse<UberInvoiceServiceResponseBody>(
				uberInvoiceServiceResponseBody, HttpStatus.OK);
		log.info("Response {} ", response);

		return response;
       
    }

    
    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource>  serveFileSilently(@PathVariable String filename) throws IOException {
    	 Resource file = storageService.loadAsResource(filename);
         return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                 "attachment; filename=\"" + file.getFilename() + "\"").body(file);

    }

    
    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        storageService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");
        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
