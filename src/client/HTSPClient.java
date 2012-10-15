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
	ClientEvents events;
	ClientSubscriptions subscriptions;
	
	private int clientid;
	
	private ClientTags tags;
	
	public static String HTSPVERSION = "htspversion"; 
	public static String SERVERNAME = "servername"; 
	public static String SERVERVERSION = "serverversion";
	public static String SERVERCAPABILITY = "servercapability";
	public static String CHALLENGE = "challenge";
	
	public static Collection<String> helloReplySet = Arrays.asList(new String[]{"htspversion","servername","serverversion","servercapability","challenge"});
	
	private MagicSequence sequence;
	
	public HTSPClient(ServerInfo serverInfo){
		this.serverInfo = serverInfo;
		this.chan = new ClientTVChannels();
		this.tags = new ClientTags();
		this.events = new ClientEvents();
		this.subscriptions = new ClientSubscriptions();
		this.sequence = new MagicSequence();
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
		HTSMsg msg = new HTSMsg(method);
		msg.put("htspversion", new Long(6));
		msg.put("clientname", "HTSPProxy");
		msg.put("clientversion", "alpha");
		msg.put("seq", sequence.pop(method));
		if (serverInfo.needAuth()) {
			msg.put("username", serverInfo.getUsername());
		}
		send(msg);
	}
	
	public void authenticate() throws IOException {
		String method= "authenticate";
		HTSMsg msg = new HTSMsg(method);
		msg.put("username", serverInfo.getUsername());
		msg.put("digest", serverInfo.getDigest());
		msg.put("seq", sequence.pop(method));
		send(msg);
	}
	
	public void enableAsyncMetadata() throws IOException{
		String method="enableAsyncMetadata";
		HTSMsg msg = new HTSMsg(method);
		msg.put("seq", sequence.pop(method));
		send(msg);
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
	
	private void handleReply(HTSMsg reply){
		if (reply.get("seq") != null) {
			Long seq = (Long) reply.get("seq");
			System.out.println("Got seq: " + seq + ", sender-method=" + sequence.giveBack(seq));
		}
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
		} else if(method.equals("eventAdd")){
			events.add(reply);
		} else if(method.equals("eventUpdate")){
			events.update(reply);
		} else if(method.equals("eventDeleted")){
			events.remove(reply);
		} else if(method.equals("initialSyncCompleted")){
			//TODO do something maybe.
		} else if(method.equals("subscriptionStart")){
			subscriptions.start(reply);
		} else if(method.equals("subscriptionStop")){
			subscriptions.stop(reply);
		} else if(method.equals("subscriptionStatus")){
			subscriptions.status(reply);
		} else if(method.equals("queueStatus")){
			subscriptions.queueStatus(reply);
		} else if(method.equals("signalStatus")){
			subscriptions.signalStatus(reply);
		} else if(method.equals("muxpkt")){
			subscriptions.muxpkt(reply);
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
