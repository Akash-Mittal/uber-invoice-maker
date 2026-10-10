package io.github.akashmittal.mockreceipt.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.support.RequestContextUtils;

import io.github.akashmittal.mockreceipt.csv.InvalidCsvException;
import io.github.akashmittal.mockreceipt.storage.ReceiptNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

/** Maps expected failures to a user-facing message on the home page (or a plain 404). */
@ControllerAdvice
class WebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebExceptionHandler.class);

    private final DataSize maxFileSize;

    WebExceptionHandler(@Value("${spring.servlet.multipart.max-file-size}") DataSize maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    @ExceptionHandler(InvalidCsvException.class)
    String invalidCsv(InvalidCsvException e, HttpServletRequest request) {
        log.info("Rejected CSV upload: {}", e.getMessage());
        return redirectHomeWithError(request, e.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    String tooLarge(HttpServletRequest request) {
        return redirectHomeWithError(request, "That file is too large. The upload limit is " + maxFileSize.toKilobytes() + " KB.");
    }

    @ExceptionHandler(ReceiptNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    void notFound() {
        // empty 404 body
    }

    private static String redirectHomeWithError(HttpServletRequest request, String message) {
        RequestContextUtils.getOutputFlashMap(request).put(ReceiptController.ERROR, message);
        return "redirect:/";
    }
}
