package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.List;

import com.sun.tools.javac.code.Attribute.Array;

import client.HTSPClient;

import shared.HTSMsg;
import shared.HTSPMonitor;



public class HTSPServer extends Thread{
	
	ServerSocket serverSocket;
	List<Socket> sockets;
	List<HTSPClient> clients;
	BufferedOutputStream os;
	BufferedInputStream is;

	HTSPMonitor monitor;
	
	public HTSPServer(int port, HTSPMonitor monitor){
		this.monitor = monitor;
		
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public class HTSPServerConnection extends Thread{
		private InputStream is;
		private BufferedOutputStream os;
		public HTSPServerConnection(Socket socket, HTSPServer server) {
			
			try {
				this.is = socket.getInputStream();
				this.os = new BufferedOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public HTSMsg rcv() throws IOException{
			byte[] lenBytes = new byte[4];
			while (is.available() < 4) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
			}
			is.read(lenBytes, 0, 4);
			long len = HTSMsg.deserializeS64(lenBytes, 4);
			byte[] msg = new byte[(int)len];
			while (is.available() < len){
				try {
					Thread.sleep(10);
					//TODO check wait time, 100 was too long!
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			is.read(msg, 0, (int) len);
			HTSMsg htsMsg = new HTSMsg(msg);
			System.out.println("Server recived " + htsMsg.get("method") + " " + htsMsg);
			handleHTSMsg(htsMsg);
			
			return htsMsg; 
		}
		
		public void send(HTSMsg msg) throws IOException{
			
			byte[] bytes = msg.serialize();
			System.out.println("Server Sending " + msg.get("method") + " " + msg);
			os.write(bytes);
			os.flush();
		}
		
		private void handleHTSMsg(HTSMsg msg) throws IOException{
			String method = (String) msg.get("method");
			handleClientToServerMethod(msg, method); 
		}	
		
		private void handleClientToServerMethod(HTSMsg msg, String method) throws IOException {
			if(method.equals("hello")){
				MethodHandlers.handleHelloMethod(msg, this);
			} else if(method.equals("authenticate")){
				MethodHandlers.handleAuthenticateMethod(msg,this);
			} else if(method.equals("getDiskSpace")){
				MethodHandlers.handleGetDiskSpaceMethod(msg,this);
			} else if(method.equals("getSysTime")){
				MethodHandlers.handleGetSysTimeMethod(msg,this);
			} else if(method.equals("enableAsyncMetadata")){
				MethodHandlers.handleEnableAsyncMetadataMethod(msg,this,monitor);
			} else if(method.equals("getEvent")){
				MethodHandlers.handleGetEventMethod(msg,this,monitor);
			} else if(method.equals("getEvents")){
				MethodHandlers.handleGetEventsMethod(msg,this);
			} else if(method.equals("epgQuery")){
				MethodHandlers.handleEpgQueryMethod(msg,this);
			} else if(method.equals("getEpgObject")){
				MethodHandlers.handleGetEpgObjectMethod(msg,this);
			} else if(method.equals("getTicket")){
				MethodHandlers.handleGetTicketMethod(msg,this);
			} else if(method.equals("subscribe")){
				MethodHandlers.handleSubscribeMethod(msg,this);
			} else if(method.equals("unsubscribe")){
				MethodHandlers.handleUnsubscribeMethod(msg,this);
			} else if(method.equals("subscriptionChangeWeight")){
				MethodHandlers.handleSubscriptionChangeWeightMethod(msg,this);
			} else{
				System.out.println("unimplemented reply: " + method);
			}
		}
		
		public void run(){
				while(true){
					try {
						rcv();
					} catch (SocketException e) {
						try {
							os.close();
							is.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						System.out.println("Client disconnected...");
						break;
					} catch (IOException e) {
						
						e.printStackTrace();
						break;
					}
				}
		}
	}
	
	public void run(){
		while(true){
			try {
				new HTSPServerConnection(serverSocket.accept(), this).start();
			} catch (SocketException e) {
				System.out.println("Client disconnected...");
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	}
}
