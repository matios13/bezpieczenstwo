package Server;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;
/**

 */
public class WorkerRunnable implements Runnable{

    protected Socket clientSocket = null;
    protected String serverText   = null;
    private Integer p = 23;
    private Integer g = 5;
    private Integer A;
    private Integer B;
    boolean step1 = false;

    public WorkerRunnable(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
    }

    public void run() {
        try {
            InputStream input  = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            
            //STEP 1
            while(!step1){
            	BufferedReader streamReader = new BufferedReader(new InputStreamReader(input, "UTF-8")); 
            	StringBuilder responseStrBuilder = new StringBuilder();

            	String inputStr;
            	while ((inputStr = streamReader.readLine()) != null)
            	    responseStrBuilder.append(inputStr);
            	//Gson pAndG =
            	//new Gson(responseStrBuilder.toString());
            }
            output.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " +
this.serverText + " - " +"").getBytes());
            output.close();
            input.close();
            System.out.println("Request processed: ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}