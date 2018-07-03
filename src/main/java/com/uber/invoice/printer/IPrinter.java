package com.uber.invoice.printer;

import java.io.IOException;

public interface IPrinter<I1, I2, O> {
	O print(I1 inputTemplate, I2 inputData) throws IOException;
}
