package com.uber.invoice.printer;

import java.io.IOException;

public interface IPrinter<I1, I2, O> {

    O print(I1 inputTemplate, I2 inputData) throws IOException;

    enum FILEFORMAT {
        PNG, JPEG, JPG, TIFF;
    }

    enum DELIMITERSNSTUFF {
        SPACE(" "), TAB("    "), COLON(":"), PIPE("|"), CLASSPATH("classpath:");

        private String val;

        public String getVal() {
            return val;
        }

        public void setVal(String val) {
            this.val = val;
        }

        private DELIMITERSNSTUFF(String val) {
            this.val = val;
        }

    }
}
