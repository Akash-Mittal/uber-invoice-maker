package com.uber.invoice.printer;

import java.io.IOException;

public interface IPrinter<I1, I2, O> {

    O print(I1 inputTemplate, I2 inputData) throws IOException;

    // Ideally FILE format should be in a different enum but
    // to keep the pojo getter setter same they are in same enum
    // when the API grows they can be refactored
    enum DELIMITERSNSTUFF {
        SPACE(" "), TAB("    "), COLON(":"), PIPE("|"), DOT("."), CLASSPATH("classpath:"), PNG("png"), JPEG(
                "jpeg"), JPG("jpg");

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
