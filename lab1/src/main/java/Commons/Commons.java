package Commons;

import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static java.lang.StrictMath.pow;

/**
 * Created by Uzytkownik on 12.10.2016.
 */
public class Commons {

    public static int PORT = 10586;
    public static String HOST = "localhost";

    public static String readJsonAndSendOne(InputStream input, OutputStream output, String sendMsg) throws SocketException {
        List<Byte> bytes = new ArrayList<>();
        BufferedReader br= new BufferedReader(new InputStreamReader(input));
        String msg;
        StringBuilder json = new StringBuilder();
        try {
            boolean received = false;
            while(!(received&&input.available()==0)){
                if(sendMsg!=null){
                    writeJson(output,sendMsg);
                    sendMsg=null;
                }
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
        return received;
    }
    public static boolean writeJson(OutputStream output, String msg){
        msg=msg+"\r\n";
        byte[] message = msg.getBytes();
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
        return new String(list);
    }
    public static int calcualteSecret(int A, int b , int p){
        return (int)(pow(A,b)%p);
    }


}
