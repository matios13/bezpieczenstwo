package Server;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketException;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.util.Random;
import Commons.Encoding;
import DTO.*;
import com.google.gson.Gson;

import javax.crypto.spec.DHParameterSpec;

import static Commons.Commons.*;
import static Commons.Encode.decodeMsg;
import static Commons.Encode.encodeMsg;

/**

 */
public class WorkerRunnable implements Runnable{

    protected Socket clientSocket = null;
    protected String serverText   = null;
    private Step1 step1;
    private BigInteger b;
    private BigInteger secret;
    private boolean running = true;
    private Encoding encoding = Encoding.none;

    public WorkerRunnable(Socket clientSocket, String serverText)  {
        try {
            System.out.println("Polaczono");
            this.clientSocket = clientSocket;
            this.serverText = serverText;
            Random generator = new Random();
            b = BigInteger.valueOf(generator.nextInt(40));
            AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
            paramGen.init(1024);
            AlgorithmParameters params = paramGen.generateParameters();
            DHParameterSpec dhSpec = (DHParameterSpec) params.getParameterSpec(DHParameterSpec.class);
            step1 = new Step1(dhSpec.getP(), dhSpec.getG());
        }catch (Exception e){

        }
    }

    public void run() {
        try {
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();

            Step2A step2A =null;
            while(true){
                if((new Gson().fromJson(readJsonAndSendOne(input,null,null),Step0.class)).getRequest().equals("keys"))
                    break;
            }
            step1(output,step1);
            Step2B step2B= new Step2B(step1.getG().modPow(b,step1.getP()));

            Gson gson = new Gson();
            while (step2A==null){
                step2A=step2(input,output,gson.toJson(step2B));
            }

            secret=step2A.getA().modPow(b,step1.getP());
            System.out.println("Secret na serwerze " + secret);
            while(running){
                String msg = readJsonAndSendOne(input,null,null);
                System.out.println("Dosttalem : " +msg);
                Message message =  gson.fromJson(msg, Message.class);
                if(message==null||message.getMsg()==null){
                    Control control = gson.fromJson(msg,Control.class);
                    if(control!=null&&control.getEncoding()!=null){
                        encoding = control.getEncoding();
                        System.out.println("Zmiana kodowania : "+encoding);
                    }else{
                        System.out.println("Blad");
                    }

                }else{
                    message.setMsg(decodeMsg(encoding,message.getMsg(),secret.intValue()));
                    System.out.println("Przyszlo " +message);
                    message.setMsg(encodeMsg(encoding,message.getMsg(),secret.intValue()));
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
        String json = readJsonAndSendOne(input,null,null);
        if(json.isEmpty())
            return null;
        System.out.println("Received : "+json.toString());
        Gson gson = new Gson();
        Step1 step1 = gson.fromJson(json.toString(), Step1.class);
        if(step1!=null)
            return step1;

        return null;

    }
    public Step2A step2(InputStream input, OutputStream outputStream,String msg) throws SocketException{
        String json = readJsonAndSendOne(input,outputStream,msg);
        if(json.isEmpty())
            return null;
        System.out.println("Received : "+json.toString());
        Gson gson = new Gson();
        Step2A step2A = gson.fromJson(json.toString(), Step2A.class);
        if(step2A!=null)
            return step2A;

        return null;

    }

    public void step1(OutputStream output, Step1 step1){
        Gson gson = new Gson();
        writeJson(output,gson.toJson(step1));
    }


}