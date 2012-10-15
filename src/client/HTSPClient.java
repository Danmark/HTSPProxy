package client;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.*;

import main.ServerInfo;

import shared.HTSMsg;


public class HTSPClient extends Thread {
	Socket socket;
	BufferedOutputStream os;
	BufferedInputStream is;
	ClientTVChannels chan;
	ServerInfo serverInfo;
	
	private int clientid;
	
	private ClientTags tags;
	
	public static String HTSPVERSION = "htspversion"; 
	public static String SERVERNAME = "servername"; 
	public static String SERVERVERSION = "serverversion";
	public static String SERVERCAPABILITY = "servercapability";
	public static String CHALLENGE = "challenge";
	
	public static Collection<String> helloReplySet = Arrays.asList(new String[]{"htspversion","servername","serverversion","servercapability","challenge"});
	
	public HTSPClient(ServerInfo serverInfo){
		this.serverInfo = serverInfo;
		this.chan = new ClientTVChannels();
		this.tags = new ClientTags();
		try {
			System.out.println("Connecting to: " + serverInfo.getIP() + ":" + serverInfo.getPort());
			socket = new Socket(serverInfo.getIP(), serverInfo.getPort());
			os = new BufferedOutputStream(socket.getOutputStream());
			is = new BufferedInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setClientid(int clientid) {
		this.clientid = clientid;
	}
	
	public void hello() throws IOException{
		
		String method="hello";
		Map<String, Object> map=new HashMap<String,Object>();
		map.put("htspversion", new Long(6));
		map.put("clientname", "HTSPProxy");
		map.put("clientversion", "alpha");
		if (serverInfo.needAuth()) {
			map.put("username", serverInfo.getUsername());
		}
		HTSMsg hello = new HTSMsg(method, map);
		send(hello);
	}
	
	public void authenticate() throws IOException {
		HTSMsg msg = new HTSMsg("authenticate");
		msg.put("username", serverInfo.getUsername());
		msg.put("digest", serverInfo.getDigest());
		send(msg);
	}
	
	public void enableAsyncMetadata() throws IOException{
		String method="enableAsyncMetadata";
		HTSMsg enableAsyncMetadata = new HTSMsg(method);
		send(enableAsyncMetadata);
	}
	
	private void send(HTSMsg msg) throws IOException{
		byte[] bytes = msg.serialize();
		System.out.println("Sending " + msg.get("method") + " " + msg);
		os.write(bytes);
		os.flush();
	}
	
	public HTSMsg rcv() throws IOException{
		byte[] lenBytes = new byte[4];
		while (is.read(lenBytes, 0, 4) != 4) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		
		long len = ByteBuffer.wrap(lenBytes,0,4).getInt();
		byte[] msg = new byte[(int)len];
		while (is.read(msg,0,(int)len)!=len){
			try {
				wait(100);
				//TODO check wait time
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		HTSMsg htsMsg = new HTSMsg(msg);
		System.out.println("Recived " + htsMsg);
		handleReply(htsMsg);
		
		return htsMsg; 
	}
	
	public void handleReply(HTSMsg reply){
		String method = (String) reply.get("method");
		if (method == null){
			if (reply.keySet().containsAll(helloReplySet)){
				for(String s : reply.keySet()){
					if(s.equals(HTSPVERSION)){
						serverInfo.setHtspversion((Long) reply.get(s));
					}
					else if (s.equals(SERVERNAME)){
						serverInfo.setServername((String) reply.get(s));
					}
					else if (s.equals(SERVERVERSION)){
						serverInfo.setServerversion((String) reply.get(s));
					}
					else if (s.equals(SERVERCAPABILITY)){
						serverInfo.setServercapability((List<String>) reply.get(s));
					}
					else if (s.equals(CHALLENGE)){
						serverInfo.setChallenge((byte[]) reply.get(s));
						try {
							authenticate();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}		
		} else if(method.equals("channelAdd")){
			chan.add(reply);
		} else if(method.equals("channelUpdate")){
			chan.update(reply);
		} else if (method.equals("channelDelete")){
			chan.remove(reply);
		} else if (method.equals("tagAdd")) {
			System.out.println("Got new tag: " + reply.get("tagName"));
			tags.add(reply);
		} else if (method.equals("tagUpdate")) {
			tags.update(reply);
		} else if (method.equals("tagDelete")) {
			tags.remove(reply);
		} else{
			//TODO something is wrong. Do something about it.
		}
	}
	
	public void run(){
		try {
			while(true){
				rcv();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
}
