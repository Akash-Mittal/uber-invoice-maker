package com.uber.invoice.controller;

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

	private final UberInvoiceService uberInvoiceService;

	private final StorageService storageService;

	@Autowired
	public FileUploadController(StorageService storageService, UberInvoiceService uberInvoiceService) {
		this.storageService = storageService;
		this.uberInvoiceService = uberInvoiceService;
	}

	@GetMapping("/")
	public String listUploadedFiles(Model model) throws IOException {

		model.addAttribute("files",
				storageService.loadAll()
						.map(path -> MvcUriComponentsBuilder
								.fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
								.build().toString())
						.collect(Collectors.toList()));

		return "index";
	}

	@GetMapping("/cover")
	public String cover(Model model) throws IOException {

		return "cover";
	}

	@PostMapping("/")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes)
			throws IOException {
		String fileExtension[] = file.getOriginalFilename().split("\\.");
		if (fileExtension[fileExtension.length - 1].equalsIgnoreCase("csv")) {
			storageService.store(file);
			log.info("Request Recieved for CSVFile {} ", file.getOriginalFilename());
			UberInvoiceServiceResponseBody uberInvoiceServiceResponseBody = uberInvoiceService
					.getInvoices(file.getOriginalFilename(), "uber-template-V1.png");
			BaseResponse<UberInvoiceServiceResponseBody> response = new BaseResponse<UberInvoiceServiceResponseBody>(
					uberInvoiceServiceResponseBody, HttpStatus.OK);
			log.info("Response {} ", response);
			redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + file.getOriginalFilename());
		}
		return "redirect:/";
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws IOException {
		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);

	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}

}
