package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import shared.HTSMsg;



public class HTSPServer extends Thread{
	
	ServerSocket serverSocket;
	List<Socket> sockets;
	BufferedOutputStream os;
	BufferedInputStream is;
	ServerTVChannels chan;
	ServerTags tags;
	ServerEvents events;
	ServerSubscriptions subscriptions;
	
	public HTSPServer(int port){
		this.chan = new ServerTVChannels();
		this.tags = new ServerTags();
		this.events = new ServerEvents();
		this.subscriptions = new ServerSubscriptions();
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void run(){
		try {
			new HTSPServerClient(serverSocket.accept(), this).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private class HTSPServerClient extends Thread{
		private Socket socket;
		private BufferedInputStream is;
		private BufferedOutputStream os;
		private HTSPServer server;
		
		public HTSPServerClient(Socket socket, HTSPServer server) {
			this.socket = socket;
			this.server=server;
			try {
				this.is = new BufferedInputStream(socket.getInputStream());
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
			long len = HTSMsg.getS64(lenBytes, 4);
			byte[] msg = new byte[(int)len];
			while (is.available() < len){
				try {
					Thread.sleep(100);
					//TODO check wait time
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			is.read(msg, 0, (int) len);
			HTSMsg htsMsg = new HTSMsg(msg);
			System.out.println("Recived " + htsMsg);
			handleHTSMsg(htsMsg);
			
			return htsMsg; 
		}
		
		private void handleHTSMsg(HTSMsg msg){
			String method = (String) msg.get("method");
			handleClientToServerMethod(msg, method); 
		}
		
		private void handleClientToServerMethod(HTSMsg msg, String method) {
			if(method.equals("hello")){
				MethodHandlers.handleHelloMethod(msg, server);
			} else if(method.equals("authenticate")){
				MethodHandlers.handleAuthenticateMethod(msg,server);
			} else if(method.equals("getDiskSpace")){
				MethodHandlers.handleGetDiskSpaceMethod(msg,server);
			} else if(method.equals("getSysTime")){
				MethodHandlers.handleGetSysTimeMethod(msg,server);
			} else if(method.equals("enableAsyncMetadata")){
				MethodHandlers.handleEnableAsyncMetadataMethod(msg,server);
			} else if(method.equals("getEvent")){
				MethodHandlers.handleGetEventMethod(msg,server);
			} else if(method.equals("getEvents")){
				MethodHandlers.handleGetEventsMethod(msg,server);
			} else if(method.equals("epgQuery")){
				MethodHandlers.handleEpgQueryMethod(msg,server);
			} else if(method.equals("getEpgObject")){
				MethodHandlers.handleGetEpgObjectMethod(msg,server);
			} else if(method.equals("getTicket")){
				MethodHandlers.handleGetTicketMethod(msg,server);
			} else if(method.equals("subscribe")){
				MethodHandlers.handleSubscribeMethod(msg,server);
			} else if(method.equals("unsubscribe")){
				MethodHandlers.handleUnsubscribeMethod(msg,server);
			} else if(method.equals("subscriptionChangeWeight")){
				MethodHandlers.handleSubscriptionChangeWeightMethod(msg,server);
			} else{
				System.out.println("unimplemented reply: " + method);
			}
		}
		
		public void run(){
			try {
				while(true){
					rcv();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
