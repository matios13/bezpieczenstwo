package Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static Commons.Commons.PORT;

public class Server {
	public static void main(String [] args){
		ServerThread server = new ServerThread(PORT);
		new Thread(server).start();
		while(!server.isStopped){
		}
		System.out.println("Stopping Server");
		server.stop();
	}
}
