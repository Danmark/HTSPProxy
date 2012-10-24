package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

import shared.HTSMsg;
import shared.HTSPMonitor;
import client.HTSPClient;



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
		private BufferedInputStream is;
		private BufferedOutputStream os;
		private int serverConnectionId;
		private boolean isRunning;

		public HTSPServerConnection(Socket socket, HTSPServer server, int serverConnectionId) {
			this.serverConnectionId = serverConnectionId;
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
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
			}
			is.read(lenBytes, 0, 4);
			long len = HTSMsg.deserializeS64(lenBytes, 4);
			byte[] msg = new byte[(int)len];
			while (is.available() < len){
				try {
					Thread.sleep(1);
					//TODO check wait time, 100 was too long!
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			is.read(msg, 0, (int) len);
			HTSMsg htsMsg = new HTSMsg(msg);
			System.out.println("Server"+serverConnectionId+" recived " + htsMsg.get("method") + " " + htsMsg);
			handleHTSMsg(htsMsg);

			return htsMsg; 
		}

		public void send(HTSMsg msg) throws IOException{

			byte[] bytes;
			bytes = msg.serialize();
			System.out.println("Server"+serverConnectionId+" Sending " + msg.get("method") + " " + msg);
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
				MethodHandlers.handleSubscribeMethod(msg,this,monitor);
			} else if(method.equals("unsubscribe")){
				MethodHandlers.handleUnsubscribeMethod(msg,this,monitor);
			} else if(method.equals("subscriptionChangeWeight")){
				MethodHandlers.handleSubscriptionChangeWeightMethod(msg,this);
			} else{
				System.out.println("unimplemented reply: " + method);
			}
		}

		public int getServerConnectionId() {
			return serverConnectionId;
		}

		public void run(){
			isRunning=true;
			while(isRunning){
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
		int i=0;
		while(true){
			try {
				HTSPServerConnection sc = new HTSPServerConnection(serverSocket.accept(), this,i++);
				monitor.addServerConnection(sc);
				sc.start();
			} catch (SocketException e) {
				System.out.println("Client disconnected...");
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	}
}
