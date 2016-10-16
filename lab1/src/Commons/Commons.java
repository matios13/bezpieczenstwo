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
        return received;
    }
    public static boolean writeJson(OutputStream output, String msg){
        byte[] message = Base64.getEncoder().encode(msg.getBytes());
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
    public static int calcualteSecret(int A, int b , int p){
        return (int)(pow(A,b)%p);
    }

    public static String encodeMsg(Encoding encoding,String msg,Integer secret){
        msg = msg.toLowerCase();
        if(encoding.equals(Encoding.cezar)){
            msg=cipher(msg,secret%27);
        }else if(encoding.equals(Encoding.xor)){
            msg=new String(xorWithKey(msg.getBytes(),intToByteArray(secret)));
        }
        return new String(Base64.getEncoder().encode(msg.getBytes()));
    }

    public static String decodeMsg(Encoding encoding,String msg,Integer secret){
        msg = new String(Base64.getDecoder().decode(msg.getBytes()));
        if(encoding.equals(Encoding.cezar)){
            msg=cipher(msg,-(secret%27));
        }else if(encoding.equals(Encoding.xor)){
            msg=new String(xorWithKey(msg.getBytes(),intToByteArray(secret)));
        }
        return msg;
    }

    private static String cipher(String msg, int shift){

        char[] buffer = msg.toCharArray();

        // Loop over characters.
        for (int i = 0; i < buffer.length; i++) {

            // Shift letter, moving back or forward 26 places if needed.
            char letter = buffer[i];
            if((letter > 'a' && letter <'b')) {
                letter = (char) (letter + shift);
                if (letter > 'z') {
                    letter = (char) (letter - 26);
                } else if (letter < 'a') {
                    letter = (char) (letter + 26);
                }
            }
            buffer[i] = letter;
        }
        // Return final string.
        return new String(buffer);
    }
    private static byte[] xorWithKey(byte[] a, byte[] key) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ key[i%key.length]);
        }
        return out;
    }

    public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }
}
