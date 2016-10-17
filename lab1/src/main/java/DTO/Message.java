package DTO;

/**
 * Created by Uzytkownik on 12.10.2016.
 */
public class Message {
    public Message(String msg, String from) {
        this.msg = msg;
        this.from = from;
    }

    private String msg;
    private String from;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        result.append("Message : \n");
        result.append(msg);
        result.append("\n from : \n");
        result.append(from);
        return result.toString();
    }
}
