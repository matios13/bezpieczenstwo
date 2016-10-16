package Server;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;
import Commons.Encoding;
import DTO.*;
import com.google.gson.Gson;

import static Commons.Commons.*;

/**

 */
public class WorkerRunnable implements Runnable{

    protected Socket clientSocket = null;
    protected String serverText   = null;
    private BigInteger b;
    private BigInteger secret;
    private boolean running = true;
    private Encoding encoding = Encoding.none;

    public WorkerRunnable(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
        Random generator = new Random();
        b=BigInteger.valueOf(generator.nextInt(40));
    }

    public void run() {
        try {
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            Step1 step1=null;
            Step2A step2A =null;
            while(step1==null){
                step1=step1(input);
            }
            Step2B step2B= new Step2B(step1.getG().modPow(b,step1.getP()));

            while (step2A==null){
                step2A=step2(input);
            }
            Gson gson = new Gson();
            writeJson(output,gson.toJson(step2B));
            secret=step2A.getA().modPow(b,step1.getP());
            System.out.println("Secret na serwerze " + secret);
            while(running){
                String msg =readJson(input);
                Message message =  gson.fromJson(msg, Message.class);
                if(message==null||message.getMessage()==null){
                    Control control = gson.fromJson(msg,Control.class);
                    if(control!=null&&control.getEncoding()!=null){
                        encoding = control.getEncoding();
                        System.out.println("Zmiana kodowania : "+encoding);
                    }else{
                        System.out.println("Blad");
                    }

                }else{
                    message.setMessage(decodeMsg(encoding,message.getMessage(),secret.intValue()));
                    System.out.println("Przyszlo " +message);
                    message.setMessage(encodeMsg(encoding,message.getMessage(),secret.intValue()));
                    System.out.println("Po kodowaniu " +message);
                    writeJson(output,gson.toJson(message));

                }
            }

        } catch(SocketException exception) {
            System.out.println("Socket Stop, Client disconect");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Step1 step1(InputStream input) throws IOException {
        String json = readJson(input);
        if(json.isEmpty())
            return null;
        System.out.println("Received : "+json.toString());
        Gson gson = new Gson();
        Step1 step1 = gson.fromJson(json.toString(), Step1.class);
        if(step1!=null)
            return step1;

        return null;

    }
    public Step2A step2(InputStream input) throws SocketException{
        String json = readJson(input);
        if(json.isEmpty())
            return null;
        System.out.println("Received : "+json.toString());
        Gson gson = new Gson();
        Step2A step2A = gson.fromJson(json.toString(), Step2A.class);
        if(step2A!=null)
            return step2A;

        return null;

    }


}