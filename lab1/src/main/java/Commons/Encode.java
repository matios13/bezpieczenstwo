package Commons;

import java.util.Base64;

/**
 * Created by matio on 17.10.2016.
 */
public class Encode {

    public static String encodeMsg(Encoding encoding,String msg,Integer secret){
        msg = msg.toLowerCase();
        if(encoding.equals(Encoding.cezar)){
            msg=cipher(msg,secret%26);
        }else if(encoding.equals(Encoding.xor)){
            msg=new String(xorWithKey(msg.getBytes(),intToByteArray(secret)));
        }
        return new String(Base64.getEncoder().encode(msg.getBytes()));
    }

    public static String decodeMsg(Encoding encoding,String msg,Integer secret){
        msg = new String(Base64.getDecoder().decode(msg.getBytes()));
        if(encoding.equals(Encoding.cezar)){
            msg=cipher(msg,-(secret%26));
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
            out[i] = (byte) (a[i] ^ key[key.length-1]);
        }
        System.out.println("xor "+ new String(out));
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
