package client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import shared.HTSMsg;
import shared.HTSPMonitor;


public class HTSPClient extends Thread {
	Socket socket;
	BufferedOutputStream os;
	BufferedInputStream is;
	ClientTVChannels chan;
	ServerInfo serverInfo;
	ClientEvents events;
	ClientSubscriptions subscriptions;
	HTSPMonitor monitor;
	boolean waitingForReply;
	
	private int clientid;

	private ClientTags tags;
		
	private MagicSequence sequence;
	
	public HTSPClient(ServerInfo serverInfo, HTSPMonitor monitor){
		this.serverInfo = serverInfo;
		this.monitor=monitor;
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
	
	public int getClientid() {
		return clientid;
	}
		
	public void hello() throws IOException, InterruptedException{
		String method = "hello";
		HTSMsg msg = new HTSMsg(method);
		msg.put("htspversion", new Long(1));
		msg.put("clientname", "HTSPProxy");
		msg.put("clientversion", "alpha");
		msg.put("seq", sequence.pop(method));
		if (serverInfo.needAuth()) {
			msg.put("username", serverInfo.getUsername());
		}
		send(msg);
	}
	
	public void authenticate() throws IOException {
		String method = "authenticate";
		HTSMsg msg = new HTSMsg(method);
		msg.put("username", serverInfo.getUsername());
		msg.put("digest", serverInfo.getDigest());
		msg.put("seq", sequence.pop(method));
		send(msg);
	}
	
	public void getDiskSpace() throws IOException {
		String method = "getDiskSpace";
		HTSMsg msg = new HTSMsg(method);
		msg.put("seq", sequence.pop(method));
		send(msg);
	}
	
	public void getSysTime() throws IOException {
		String method = "getSysTime";
		HTSMsg msg = new HTSMsg(method);
		msg.put("seq", sequence.pop(method));
		send(msg);
	}
	
	public void enableAsyncMetadata() throws IOException{
		String method="enableAsyncMetadata";
		HTSMsg msg = new HTSMsg(method);
		msg.put("seq", sequence.pop(method));
		send(msg);
	}
	
	public void getEvent(long eventId, String language) throws IOException{
		String method="getEvent";
		HTSMsg msg = new HTSMsg(method);
		msg.put("eventId",eventId);
		msg.put("language", language);
		msg.put("seq", sequence.pop(method));
		send(msg);
	}
	
	public void getEvents(long eventId, long channelId, long numFollowing, long maxTime, String language) throws IOException{
		String method="getEvent";
		HTSMsg msg = new HTSMsg(method);
		msg.put("eventId",eventId);
		msg.put("channelId",channelId);
		msg.put("numFollowing",numFollowing);
		msg.put("maxTime",maxTime);
		msg.put("language", language);
		msg.put("seq", sequence.pop(method));
		send(msg);
	}
	
	public void epgQuery(String query, long channelId, long tagId, long contentType, String language, long full) throws IOException{
		String method="epgQuery";
		HTSMsg msg = new HTSMsg(method);
		msg.put("query", query);
		msg.put("channelId", channelId);
		msg.put("tagId", tagId);
		msg.put("contentType", contentType);
		msg.put("language", language);
		msg.put("full", full);
		msg.put("seq", sequence.pop(method));
		send(msg);
	}
	
	public void getEpgObject(long id, long type) throws IOException{
		String method="getEpgObject";
		HTSMsg msg = new HTSMsg(method);
		msg.put("id", id);
		msg.put("type", type);
		msg.put("seq", sequence.pop(method));
		send(msg);
	}
	
	public void getTicket(long channelId, long dvrId) throws IOException{
		String method="getTicket";
		HTSMsg msg = new HTSMsg(method);
		msg.put("channelId", channelId);
		msg.put("dvrId", dvrId);
		msg.put("seq", sequence.pop(method));
		send(msg);
	}
	
	public void subscribe(long channelId, long subscriptionId, long weight) throws IOException{
		String method="subscribe";
		HTSMsg msg = new HTSMsg(method);
		msg.put("channelId", channelId);
		msg.put("subscriptionId", subscriptionId);
		msg.put("weight", weight);
		msg.put("seq", sequence.pop(method));
		send(msg);
	}
	
	public void unsubscribe(long subscriptionId) throws IOException{
		String method="unsubscribe";
		HTSMsg msg = new HTSMsg(method);
		msg.put("subscriptionId", subscriptionId);
		msg.put("seq", sequence.pop(method));
		send(msg);
	}
	
	public void subscriptionChangeWeight(long subscriptionId, long weight) throws IOException{
		String method="subscriptionChangeWeight";
		HTSMsg msg = new HTSMsg(method);
		msg.put("subscriptionId", subscriptionId);
		msg.put("weight", weight);
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
				Thread.sleep(100);
				//TODO check wait time
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		is.read(msg, 0, (int) len);
		HTSMsg htsMsg = new HTSMsg(msg);
		System.out.println("Client"+ clientid +" recived "+ htsMsg.get("method") + " " + htsMsg);
		handleHTSMsg(htsMsg);
		
		return htsMsg; 
	}
	
	private void handleHTSMsg(HTSMsg msg){
		String method = (String) msg.get("method");

		if (msg.get("seq") != null) {
			handleReply(msg, (Long) msg.get("seq"));
		}
		else if (method == null){
			System.out.println("no seq, no method. Something is wrong. Forgot to send seq?");		
		} else{
			handleServerToClientMethod(msg, method);
		} 
	}
	
	private void handleServerToClientMethod(HTSMsg msg, String method) {
		if(method.equals("channelAdd")){
			long channelId = chan.add(msg);
			monitor.addChannel(channelId, clientid);
			Long eventId = (Long) msg.get("eventId");
			Long nextEventId = (Long) msg.get("nextEventId");
			try {
				if (eventId!=null)
					getEvent(eventId, "sv");
				if (nextEventId!=null)
					getEvent(nextEventId, "sv");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else if(method.equals("channelUpdate")){
			long channelId = chan.update(msg);
			monitor.updateChannel(channelId, clientid);
		} else if (method.equals("channelDelete")){
			long channelId = chan.remove(msg);
			monitor.deleteChannel(channelId, clientid);
		} else if (method.equals("tagAdd")) {
			tags.add(msg);
			monitor.addTag(msg);
		} else if (method.equals("tagUpdate")) {
			tags.update(msg);
			monitor.updateTag(msg);
		} else if (method.equals("tagDelete")) {
			tags.remove(msg);
			monitor.removeTag(msg);
		} else if(method.equals("eventAdd")){
			events.add(msg);
			monitor.addEvent(msg);
		} else if(method.equals("eventUpdate")){
			events.update(msg);
			monitor.updateEvent(msg);
		} else if(method.equals("eventDeleted")){
			events.remove(msg);
			monitor.removeEvent(msg);
		} else if(method.equals("initialSyncCompleted")){
			//TODO do something maybe.
		} else if(method.equals("subscriptionStart")){
			subscriptions.start(msg);
		} else if(method.equals("subscriptionStop")){
			subscriptions.stop(msg);
		} else if(method.equals("subscriptionStatus")){
			subscriptions.status(msg);
		} else if(method.equals("queueStatus")){
			subscriptions.queueStatus(msg);
		} else if(method.equals("signalStatus")){
			subscriptions.signalStatus(msg);
		} else if(method.equals("muxpkt")){
			subscriptions.muxpkt(msg);
		} else{
			System.out.println("unimplemented ServerToClient-method: " + method);
			//TODO something is wrong. Do something about it.
		}		
	}

	private void handleReply(HTSMsg msg, Long seq) {
		String method = sequence.peek(seq);
		if(method==null){
			
		}else if(method.equals("hello")){
			ReplyHandlers.handleHelloReply(msg,this);
		} else if(method.equals("authenticate")){
			ReplyHandlers.handleAuthenticateReply(msg,this);
		} else if(method.equals("getDiskSpace")){
			ReplyHandlers.handleGetDiskSpaceReply(msg,this);
		} else if(method.equals("getSysTime")){
			ReplyHandlers.handleGetSysTimeReply(msg,this);
		} else if(method.equals("enableAsyncMetadata")){
			ReplyHandlers.handleEnableAsyncMetadataReply(msg,this);
		} else if(method.equals("getEvent")){
			ReplyHandlers.handleGetEventReply(msg,this);
		} else if(method.equals("getEvents")){
			ReplyHandlers.handleGetEventsReply(msg,this);
		} else if(method.equals("epgQuery")){
			ReplyHandlers.handleEpgQueryReply(msg,this);
		} else if(method.equals("getEpgObject")){
			ReplyHandlers.handleGetEpgObjectReply(msg,this);
		} else if(method.equals("getTicket")){
			ReplyHandlers.handleGetTicketReply(msg,this);
		} else if(method.equals("subscribe")){
			ReplyHandlers.handleSubscribeReply(msg,this);
		} else if(method.equals("unsubscribe")){
			ReplyHandlers.handleUnsubscribeReply(msg,this);
		} else if(method.equals("subscriptionChangeWeight")){
			ReplyHandlers.handleSubscriptionChangeWeightReply(msg,this);
		} else{
			System.out.println("unimplemented reply: " + method);
		}
		sequence.giveBack(seq);
	}
	
	public HTSMsg getChannel(long channelId){
		return chan.getChannel(channelId);
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
