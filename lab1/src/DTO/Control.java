package DTO;

import Commons.Encoding;

/**
 * Created by Uzytkownik on 12.10.2016.
 */
public class Control {
    public Control(String encoding) {
        this.encoding = encoding;
    }

    private String encoding;

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
