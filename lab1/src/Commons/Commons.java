package Commons;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by Uzytkownik on 12.10.2016.
 */
public class Commons {

    public static int PORT = 9000;

    public static String readJson(BufferedReader reader){
        String msg;
        StringBuilder json = new StringBuilder();
        try {
            while((msg = reader.readLine())!=null) {
                json.append(msg);
                if(msg.contains("}"))
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
    public static boolean writeJson(BufferedWriter writer, String msg){
        try {
            writer.write(msg);
            writer.newLine();
            writer.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
