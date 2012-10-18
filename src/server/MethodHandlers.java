package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import server.HTSPServer.HTSPServerConnection;
import shared.HTSMsg;
import shared.HTSPMonitor;

public class MethodHandlers {

	static Long htspversion = new Long(6);
	static String servername = "HTSPProxy";
	static String serverversion = "alpha";
	static List<String> servercapability = new ArrayList<String>();
	
	private static byte[] createChallence(){
		//TODO
		return new byte[32];
	}

	public static void handleHelloMethod(HTSMsg msg, HTSPServerConnection conn) throws IOException{
		Collection<String> requiredFields = Arrays.asList(new String[]{"htspversion","clientname","clientversion"});
		if(((Number) msg.get("htspversion")).longValue()==1){
			requiredFields = Arrays.asList(new String[]{"htspversion","clientname"});
		}
		if (msg.keySet().containsAll(requiredFields)){
			HTSMsg reply = new HTSMsg();
			handleExtraFields(msg,reply);
			reply.put("htspversion", htspversion);
			reply.put("servername", servername);
			reply.put("serverversion", serverversion);
			reply.put("servercapability", servercapability);
			reply.put("challenge", createChallence());
			conn.send(reply);
		} else{
			System.out.println("Faulty request");
		}
	}

	public static void handleAuthenticateMethod(HTSMsg msg, HTSPServerConnection conn) throws IOException {
		Collection<String> requiredFields = Arrays.asList(new String[]{});
		if (msg.keySet().containsAll(requiredFields)){
			HTSMsg reply = new HTSMsg();
			handleExtraFields(msg,reply);
			//TODO
			conn.send(reply);
		} else{
			System.out.println("Faulty request");
		}

	}

	public static void handleGetDiskSpaceMethod(HTSMsg msg, HTSPServerConnection conn) throws IOException {
		Collection<String> requiredFields = Arrays.asList(new String[]{"freediskspace","totaldiskspace"});
		if (msg.keySet().containsAll(requiredFields)){
			HTSMsg reply = new HTSMsg();
			handleExtraFields(msg,reply);
			//TODO
			conn.send(reply);
		} else{
			System.out.println("Faulty request");
		}

	}

	public static void handleGetSysTimeMethod(HTSMsg msg, HTSPServerConnection conn) throws IOException {
		Collection<String> requiredFields = Arrays.asList(new String[]{"time","timezone"});
		if (msg.keySet().containsAll(requiredFields)){
			HTSMsg reply = new HTSMsg();
			handleExtraFields(msg,reply);
			//TODO
			conn.send(reply);
		} else{
			System.out.println("Faulty request");
		}

	}

	public static void handleEnableAsyncMetadataMethod(HTSMsg msg, HTSPServerConnection conn, HTSPMonitor monitor) throws IOException {
		Collection<String> requiredFields = Arrays.asList(new String[]{});
		if (msg.keySet().containsAll(requiredFields)){
			HTSMsg reply = new HTSMsg();
			handleExtraFields(msg,reply);
			conn.send(reply);
			for(HTSMsg tag:monitor.getAllTags()){
				Object l = tag.remove("members");
				conn.send(tag);
				tag.put("members",l);
			}
			for (HTSMsg channel:monitor.getAllChannels()){
				conn.send(channel);
			}
			for(HTSMsg tag:monitor.getAllTags()){
				tag.put("method", "tagUpdate");
				conn.send(tag);
				tag.put("method", "tagAdd");
			}
			//TODO (send dvr)
			conn.send(new HTSMsg("initialSyncCompleted"));
			//TODO start the asyncMetadataThread. (and implement dito)
		} else{
			System.out.println("Faulty request");
		}

	}

	public static void handleGetEventMethod(HTSMsg msg, HTSPServerConnection conn, HTSPMonitor monitor) throws IOException {
		Collection<String> requiredFields = Arrays.asList(new String[]{"eventId"});
		if (msg.keySet().containsAll(requiredFields)){
			HTSMsg reply = monitor.getEvent((Long) msg.get("eventId"));
			monitor.getClient(0).getEvent((Long) msg.get("eventId"), "sv");
			while((reply = monitor.getEvent((Long) msg.get("eventId")))==null){
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			handleExtraFields(msg,reply);
			//TODO
			conn.send(reply);
		} else{
			System.out.println("Faulty request");
		}

	}

	public static void handleGetEventsMethod(HTSMsg msg, HTSPServerConnection conn) throws IOException {
		Collection<String> requiredFields = Arrays.asList(new String[]{"events"});
		if (msg.keySet().containsAll(requiredFields)){
			HTSMsg reply = new HTSMsg();
			handleExtraFields(msg,reply);
			//TODO
			conn.send(reply);
		} else{
			System.out.println("Faulty request");
		}

	}

	public static void handleEpgQueryMethod(HTSMsg msg, HTSPServerConnection conn) throws IOException {
		Collection<String> requiredFields = Arrays.asList(new String[]{"query"});
		if (msg.keySet().containsAll(requiredFields)){
			HTSMsg reply = new HTSMsg();
			handleExtraFields(msg,reply);
			//TODO
			conn.send(reply);
		} else{
			System.out.println("Faulty request");
		}

	}

	public static void handleGetEpgObjectMethod(HTSMsg msg, HTSPServerConnection conn) throws IOException {
		Collection<String> requiredFields = Arrays.asList(new String[]{});
		if (msg.keySet().containsAll(requiredFields)){
			HTSMsg reply = new HTSMsg();
			handleExtraFields(msg,reply);
			//TODO
			conn.send(reply);
		} else{
			System.out.println("Faulty request");
		}

	}

	public static void handleGetTicketMethod(HTSMsg msg, HTSPServerConnection conn) throws IOException {
		Collection<String> requiredFields = Arrays.asList(new String[]{"path","ticket"});
		if (msg.keySet().containsAll(requiredFields)){
			HTSMsg reply = new HTSMsg();
			handleExtraFields(msg,reply);
			//TODO
			conn.send(reply);
		} else{
			System.out.println("Faulty request");
		}

	}

	public static void handleSubscribeMethod(HTSMsg msg, HTSPServerConnection conn) throws IOException {
		Collection<String> requiredFields = Arrays.asList(new String[]{});
		if (msg.keySet().containsAll(requiredFields)){
			HTSMsg reply = new HTSMsg();
			handleExtraFields(msg,reply);
			//TODO
			conn.send(reply);
		} else{
			System.out.println("Faulty request");
		}

	}

	public static void handleUnsubscribeMethod(HTSMsg msg, HTSPServerConnection conn) throws IOException {
		Collection<String> requiredFields = Arrays.asList(new String[]{});
		if (msg.keySet().containsAll(requiredFields)){
			HTSMsg reply = new HTSMsg();
			handleExtraFields(msg,reply);
			//TODO
			conn.send(reply);
		} else{
			System.out.println("Faulty request");
		}

	}

	public static void handleSubscriptionChangeWeightMethod(HTSMsg msg, HTSPServerConnection conn) throws IOException {
		Collection<String> requiredFields = Arrays.asList(new String[]{});
		if (msg.keySet().containsAll(requiredFields)){
			HTSMsg reply = new HTSMsg();
			handleExtraFields(msg,reply);
			//TODO
			conn.send(reply);
		} else{
			System.out.println("Faulty request");
		}
		
	}

	private static void handleExtraFields(HTSMsg msg, HTSMsg reply) {
		Long seq;
		if((seq = (Long) msg.get("seq")) != null)
			reply.put("seq", seq);
		//TODO implement authorization
	}
}
