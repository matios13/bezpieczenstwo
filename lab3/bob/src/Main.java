import java.util.Arrays;
import java.util.Base64;

/**
 * Created by matio on 07.11.2016.
 */
public class Main {
    public static void main(String[] args) {
        String bobHash = "GCg7Ozs7Oy01e3oNMz4gP3ogP3ovPjs2NXoZM3opMz96KDUgKSAjPCg1LTs5" +
                "ei4/MSkueiA7KSAjPCg1LTs0I3oqNTA/PiM0OSAjN3o4OzAuPzd0ehQ1ej41" +
                "OCg7dno8Njs9O3ouNWB6CBUADRsWBSEJMzQ9Nj8CNSgYIy4/GTMqMj8oJw==";
        byte[] decoded = Base64.getDecoder().decode(bobHash.getBytes());
        for(int i =0;i<256;i++){
            byte[] temp = decoded.clone();
            String decodedMesssage = xor(temp,i);
            if(decodedMesssage.contains("ROZ")){
                System.err.println("TEST : "+decodedMesssage);
            }
            //System.out.println(decodedMesssage);
        }
    }
    private static String xor(byte[] message,int oneByte){

        for (int i = 0; i < message.length; i++) {
            message[i]=(byte)(message[i]^oneByte);
        }
        return new String(message);
    }
}
