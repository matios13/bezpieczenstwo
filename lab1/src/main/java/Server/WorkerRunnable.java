package Server;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketException;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Random;
import Commons.Encoding;
import DTO.*;
import com.google.gson.Gson;

import javax.crypto.spec.DHParameterSpec;

import static Commons.Commons.*;
import static Commons.Encode.decodeMsg;
import static Commons.Encode.encodeMsg;

/**
Watek dla socketa
 obsługa protokołu
 */
public class WorkerRunnable implements Runnable{

    protected Socket clientSocket = null;
    protected String serverText   = null;
    private Step1 step1;
    private BigInteger b;
    private BigInteger secret;
    private boolean running = true;
    private Encoding encoding = Encoding.none;
    private Gson gson;

    public WorkerRunnable(Socket clientSocket, String serverText)  {
        System.out.println("Polaczono");
        this.clientSocket = clientSocket;
        this.serverText = serverText;
        gson = new Gson();
        generateBNumber();
        generateKeys();
    }

    public void run() {
        try {
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            waitForKeys(input);
            sendKeys(output,step1);
            Step2A step2A = sendBAndWaitForA(input, output);
            calculateSecret(step2A);
            waitForMessagesOrControl(input, output);

        } catch(SocketException exception) {
            System.out.println("Socket Stop, Client disconect");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void calculateSecret(Step2A step2A) {
        secret=step2A.getA().modPow(b,step1.getP());
        System.out.println("Secret na serwerze " + secret);
    }

    private Step2A sendBAndWaitForA(InputStream input, OutputStream output) throws SocketException {
        Step2A step2A=null;
        Step2B step2B= new Step2B(step1.getG().modPow(b,step1.getP()));
        while (step2A==null){
            step2A=step2(input,output,gson.toJson(step2B));
        }
        return step2A;
    }

    private void generateBNumber() {
        Random generator = new Random();
        b = BigInteger.valueOf(generator.nextInt(40));
    }
    private void generateKeys(){
        AlgorithmParameterGenerator paramGen = null;
        try {
            paramGen = AlgorithmParameterGenerator.getInstance("DH");
            paramGen.init(1024);
            AlgorithmParameters params = paramGen.generateParameters();
            DHParameterSpec dhSpec = (DHParameterSpec) params.getParameterSpec(DHParameterSpec.class);
            step1 = new Step1(dhSpec.getP(), dhSpec.getG());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        }

    }
    public void waitForKeys(InputStream input){
        while(true){
            try {
                if((gson.fromJson(readJsonAndSendOne(input,null,null),Step0.class)).getRequest().equals("keys"))
                    break;
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendKeys(OutputStream output, Step1 step1){
        writeJson(output,gson.toJson(step1));
    }
    public Step2A step2(InputStream input, OutputStream outputStream,String msg) throws SocketException{
        String json = readJsonAndSendOne(input,outputStream,msg);
        if(json.isEmpty())
            return null;
        System.out.println("Received : "+json.toString());
        Step2A step2A = gson.fromJson(json.toString(), Step2A.class);
        if(step2A!=null)
            return step2A;

        return null;

    }

    private void waitForMessagesOrControl(InputStream input, OutputStream output) throws SocketException {
        while(running){
            String msg = readJsonAndSendOne(input,null,null);
            System.out.println("Dostalem : " +msg);
            Message message =  gson.fromJson(msg, Message.class);
            if(message==null||message.getMsg()==null){
                handleControlMessage(msg);

            }else{
                handleMessage(output, message);

            }
        }
    }

    private void handleMessage(OutputStream output, Message message) {
        message.setMsg(decodeMsg(encoding,message.getMsg(),secret.intValue()));
        System.out.println("Przyszlo " +message);
        message.setMsg(encodeMsg(encoding,message.getMsg(),secret.intValue()));
        System.out.println("Po kodowaniu " +message);
        writeJson(output,gson.toJson(message));
    }

    private void handleControlMessage(String msg) {
        Control control = gson.fromJson(msg,Control.class);
        if(control!=null&&control.getEncoding()!=null){
            encoding = control.getEncoding();
            System.out.println("Zmiana kodowania : "+encoding);
        }else{
            System.out.println("Blad");
        }
    }




}