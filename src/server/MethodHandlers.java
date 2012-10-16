package server;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import shared.HTSMsg;

public class MethodHandlers {

	public static String HTSPVERSION = "htspversion"; 
	public static String SERVERNAME = "servername"; 
	public static String SERVERVERSION = "serverversion";
	public static String SERVERCAPABILITY = "servercapability";
	public static String CHALLENGE = "challenge";

	public static void handleHelloMethod(HTSMsg msg, HTSPServer client){
		Collection<String> requiredFields = Arrays.asList(new String[]{"htspversion","servername","serverversion","servercapability","challenge"});
		if (msg.keySet().containsAll(requiredFields)){
			
		} else{
			System.out.println("Faulty request");
		}
	}

	public static void handleAuthenticateMethod(HTSMsg msg, HTSPServer client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{});
		if (msg.keySet().containsAll(requiredFields)){
			//TODO
		} else{
			System.out.println("Faulty request");
		}

	}


	public static void handleGetDiskSpaceMethod(HTSMsg msg, HTSPServer client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{"freediskspace","totaldiskspace"});
		if (msg.keySet().containsAll(requiredFields)){
			//TODO
		} else{
			System.out.println("Faulty request");
		}

	}

	public static void handleGetSysTimeMethod(HTSMsg msg, HTSPServer client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{"time","timezone"});
		if (msg.keySet().containsAll(requiredFields)){
			//TODO
		} else{
			System.out.println("Faulty request");
		}

	}

	public static void handleEnableAsyncMetadataMethod(HTSMsg msg, HTSPServer client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{});
		if (msg.keySet().containsAll(requiredFields)){
			//TODO
		} else{
			System.out.println("Faulty request");
		}

	}

	public static void handleGetEventMethod(HTSMsg msg, HTSPServer client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{"eventId","channelId","start","stop"});
		if (msg.keySet().containsAll(requiredFields)){
			//TODO
		} else{
			System.out.println("Faulty request");
		}

	}

	public static void handleGetEventsMethod(HTSMsg msg, HTSPServer client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{"events"});
		if (msg.keySet().containsAll(requiredFields)){
			//TODO
		} else{
			System.out.println("Faulty request");
		}

	}

	public static void handleEpgQueryMethod(HTSMsg msg, HTSPServer client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{"query"});
		if (msg.keySet().containsAll(requiredFields)){
			//TODO
		} else{
			System.out.println("Faulty request");
		}

	}

	public static void handleGetEpgObjectMethod(HTSMsg msg, HTSPServer client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{});
		if (msg.keySet().containsAll(requiredFields)){
			//TODO
		} else{
			System.out.println("Faulty request");
		}

	}

	public static void handleGetTicketMethod(HTSMsg msg, HTSPServer client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{"path","ticket"});
		if (msg.keySet().containsAll(requiredFields)){
			//TODO
		} else{
			System.out.println("Faulty request");
		}

	}

	public static void handleSubscribeMethod(HTSMsg msg, HTSPServer client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{});
		if (msg.keySet().containsAll(requiredFields)){
			//TODO
		} else{
			System.out.println("Faulty request");
		}

	}

	public static void handleUnsubscribeMethod(HTSMsg msg, HTSPServer client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{});
		if (msg.keySet().containsAll(requiredFields)){
			//TODO
		} else{
			System.out.println("Faulty request");
		}

	}

	public static void handleSubscriptionChangeWeightMethod(HTSMsg msg, HTSPServer client) {
		Collection<String> requiredFields = Arrays.asList(new String[]{});
		if (msg.keySet().containsAll(requiredFields)){
			//TODO
		} else{
			System.out.println("Faulty request");
		}

	}


}
