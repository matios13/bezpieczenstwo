package DTO;

import Commons.Encoding;

/**
 * Created by Uzytkownik on 12.10.2016.
 */
public class Control {
    public Control(Encoding encoding) {
        this.encoding = encoding;
    }

    private Encoding encoding;

    public Encoding getEncoding() {
        return encoding;
    }

    public void setEncoding(Encoding encoding) {
        this.encoding = encoding;
    }
}
