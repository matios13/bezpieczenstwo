package Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static boolean isRunning;
	public static void main(String [] args){
		ServerThread server = new ServerThread(9000);
		new Thread(server).start();
		isRunning=true;
		while(isRunning){
		}
		System.out.println("Stopping Server");
		server.stop();
	}
}
