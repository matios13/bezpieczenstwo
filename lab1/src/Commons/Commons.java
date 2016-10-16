package Commons;

import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * Created by Uzytkownik on 12.10.2016.
 */
public class Commons {

    public static int PORT = 9000;

    public static String readJson(InputStream input) throws SocketException {
        List<Byte> bytes = new ArrayList<>();
        BufferedReader br= new BufferedReader(new InputStreamReader(input));
        String msg;
        StringBuilder json = new StringBuilder();
        try {
            boolean received = false;
            while(!(received&&input.available()==0)){
                int oneByte = input.read();
                if(oneByte>0){
                    received=true;
                    bytes.add((byte)oneByte);
                }else {
                    break;
                }
            }
        } catch(SocketException e) {
            throw new SocketException();
        }catch (IOException e){
            e.printStackTrace();
        }
        String received = decodeListOfBytes(bytes.toArray());
        System.out.println("Received : "+received);
        return received;
    }
    public static boolean writeJson(OutputStream output, String msg){
        byte[] message = Base64.getEncoder().encode(msg.getBytes());
        System.out.println("Send : "+msg+" base64 "+new String(message));
        try {
            output.write(message);
            output.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    private static String decodeListOfBytes(Object[] bytes){
        byte[] list = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            list[i]=(Byte)bytes[i];
        }
        return new String(Base64.getDecoder().decode(list));
    }
}
