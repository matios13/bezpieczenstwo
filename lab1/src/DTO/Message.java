package DTO;

/**
 * Created by Uzytkownik on 12.10.2016.
 */
public class Message {
    public Message(String message, String from) {
        this.message = message;
        this.from = from;
    }

    private String message;
    private String from;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
