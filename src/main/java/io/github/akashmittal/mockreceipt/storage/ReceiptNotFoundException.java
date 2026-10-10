package io.github.akashmittal.mockreceipt.storage;

public class ReceiptNotFoundException extends RuntimeException {

    public ReceiptNotFoundException(String name) {
        super("No receipt named " + name);
    }
}
