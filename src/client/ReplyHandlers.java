package client;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import shared.HTSMsg;
import shared.HTSPMonitor;

public class ReplyHandlers {
	
	public static String HTSPVERSION = "htspversion"; 
	public static String SERVERNAME = "servername"; 
	public static String SERVERVERSION = "serverversion";
	public static String SERVERCAPABILITY = "servercapability";
	public static String CHALLENGE = "challenge";

	public static void handleHelloReply(HTSMsg msg, HTSPClient client){
		Collection<String> requiredFields = Arrays.asList(new String[]{"htspversion","servername","serverversion","servercapability","challenge"});
		if (msg.keySet().containsAll(requiredFields)){
			for(String s : msg.keySet()){
				if(s.equals(HTSPVERSION)){
					client.serverInfo.setHtspversion((Long) msg.get(s));
				}
				else if (s.equals(SERVERNAME)){
					client.serverInfo.setServername((String) msg.get(s));
				}
				else if (s.equals(SERVERVERSION)){
					client.serverInfo.setServerversion((String) msg.get(s));
				}
				else if (s.equals(SERVERCAPABILITY)){
					client.serverInfo.setServercapability((List<String>) msg.get(s));
				}
				else if (s.equals(CHALLENGE)){
					client.serverInfo.setChallenge((byte[]) msg.get(s));
					try {
						client.authenticate();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else if (msg.get("error") != null){
			//TODO
		} else {
			System.out.println("Faulty reply");
		}
	}
	
	public static void handleAuthenticateReply(HTSMsg msg, HTSPClient client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{});
		if (msg.keySet().containsAll(requiredFields)){
			//TODO
		} else if (msg.get("error") != null){
			//TODO
		} else{
			System.out.println("Faulty reply");
		}
		
	}

	
	public static void handleGetDiskSpaceReply(HTSMsg msg, HTSPClient client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{"freediskspace","totaldiskspace"});
		if (msg.keySet().containsAll(requiredFields)){
			//TODO
		} else if (msg.get("error") != null){
			//TODO
		} else{
			System.out.println("Faulty reply");
		}
		
	}
	
	public static void handleGetSysTimeReply(HTSMsg msg, HTSPClient client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{"time","timezone"});
		if (msg.keySet().containsAll(requiredFields)){
			//TODO
		} else if (msg.get("error") != null){
			//TODO
		} else{
			System.out.println("Faulty reply");
		}
		
	}
	
	public static void handleEnableAsyncMetadataReply(HTSMsg msg, HTSPClient client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{});
		if (msg.keySet().containsAll(requiredFields)){
			//TODO
		} else if (msg.get("error") != null){
			//TODO
		} else{
			System.out.println("Faulty reply");
		}
		
	}
	
	public static void handleGetEventReply(HTSMsg msg, HTSPClient client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{"eventId","channelId","start","stop"});
		if (msg.keySet().containsAll(requiredFields)){
			client.events.add(msg);
			client.monitor.addEvent(msg);
		} else if (msg.get("error") != null){
			//TODO
		} else{
			System.out.println("Faulty reply");
		}
		
	}
	
	public static void handleGetEventsReply(HTSMsg msg, HTSPClient client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{"events"});
		if (msg.keySet().containsAll(requiredFields)){
			//TODO
		} else if (msg.get("error") != null){
			//TODO
		} else{
			System.out.println("Faulty reply");
		}
		
	}
	
	public static void handleEpgQueryReply(HTSMsg msg, HTSPClient client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{"query"});
		if (msg.keySet().containsAll(requiredFields)){
			//TODO
		} else if (msg.get("error") != null){
			//TODO
		} else{
			System.out.println("Faulty reply");
		}
		
	}
	
	public static void handleGetEpgObjectReply(HTSMsg msg, HTSPClient client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{});
		if (msg.keySet().containsAll(requiredFields)){
			//TODO
		} else if (msg.get("error") != null){
			//TODO
		} else{
			System.out.println("Faulty reply");
		}
		
	}
	
	public static void handleGetTicketReply(HTSMsg msg, HTSPClient client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{"path","ticket"});
		if (msg.keySet().containsAll(requiredFields)){
			//TODO
		} else if (msg.get("error") != null){
			//TODO
		} else{
			System.out.println("Faulty reply");
		}
		
	}
	
	public static void handleSubscribeReply(HTSMsg msg, HTSPClient client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{});
		if (msg.keySet().containsAll(requiredFields)){
			//TODO
		} else if (msg.get("error") != null){
			//TODO
		} else{
			System.out.println("Faulty reply");
		}
		
	}
	
	public static void handleUnsubscribeReply(HTSMsg msg, HTSPClient client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{});
		if (msg.keySet().containsAll(requiredFields)){
			//TODO
		} else if (msg.get("error") != null){
			//TODO
		} else{
			System.out.println("Faulty reply");
		}
		
	}
	
	public static void handleSubscriptionChangeWeightReply(HTSMsg msg, HTSPClient client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{});
		if (msg.keySet().containsAll(requiredFields)){
			//TODO
		} else if (msg.get("error") != null){
			//TODO
		} else{
			System.out.println("Faulty reply");
		}
		
	}
}
