package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import client.HTSPClient;

import shared.HTSMsg;
import shared.Events;
import shared.Subscriptions;
import shared.TVChannels;
import shared.Tags;



public class HTSPServer extends Thread{
	
	ServerSocket serverSocket;
	List<Socket> sockets;
	List<HTSPClient> clients;
	BufferedOutputStream os;
	BufferedInputStream is;
	TVChannels chan;
	Tags tags;
	Events events;
	Subscriptions subscriptions;
	
	public HTSPServer(int port, List<HTSPClient> clients){
		this.clients = clients;
		this.chan = new TVChannels();
		this.tags = new Tags();
		this.events = new Events(this);
		this.subscriptions = new Subscriptions();
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Server constructed");
	}
	
	public void addChannel(HTSMsg channel){
		chan.add(channel);
	}
	
	public void updateChannel(HTSMsg msg) {
		chan.update(msg);
	}
	
	public void deleteChannel(HTSMsg msg) {
		chan.remove(msg);
	}
	public void addTag(HTSMsg tag) {
		tags.add(tag);		
	}
	
	public void updateTag(HTSMsg msg) {
		tags.update(msg);
	}
	
	public void removeTag(HTSMsg msg) {
		tags.remove(msg);
	}
	
	public void addEvent(HTSMsg msg) {
		events.add(msg);
	}
	
	public void updateEvent(HTSMsg msg) {
		events.update(msg);
	}
	
	public void removeEvent(HTSMsg msg) {
		events.remove(msg);
	}
	
	public HTSPClient getClient(int id){
		return clients.get(id);
	}
	
	public class HTSPServerConnection extends Thread{
		private BufferedInputStream is;
		private BufferedOutputStream os;
		private HTSPServer server;
		private Socket socket;
		
		public HTSPServerConnection(Socket socket, HTSPServer server) {
			
			this.server=server;
			this.socket = socket;
			
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
			System.out.println("Server recived " + htsMsg);
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
				System.out.println("");
				MethodHandlers.handleHelloMethod(msg, this);
			} else if(method.equals("authenticate")){
				MethodHandlers.handleAuthenticateMethod(msg,this);
			} else if(method.equals("getDiskSpace")){
				MethodHandlers.handleGetDiskSpaceMethod(msg,this);
			} else if(method.equals("getSysTime")){
				MethodHandlers.handleGetSysTimeMethod(msg,this);
			} else if(method.equals("enableAsyncMetadata")){
				MethodHandlers.handleEnableAsyncMetadataMethod(msg,this,server);
			} else if(method.equals("getEvent")){
				MethodHandlers.handleGetEventMethod(msg,this,server);
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
					} catch (IOException e) {
						// TODO Auto-generated catch block
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
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
