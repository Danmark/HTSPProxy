package shared;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import server.HTSPServer.HTSPServerConnection;

public class Subscriptions {
	
	List<Subscription> subscriptions;
	HTSPMonitor monitor;
	int i=0;
	
	public Subscriptions(HTSPMonitor monitor){
		this.monitor=monitor;
		subscriptions = new ArrayList<Subscription>();
	}
	
	public synchronized void add(long channelId, int clientId, long serverSubscriptionId, int serverConnectionId){
		subscriptions.add(new Subscription(channelId,clientId,serverSubscriptionId,serverConnectionId,monitor));
	}
	
	public synchronized void start(HTSMsg msg, int clientId) throws IOException{
		Subscription s = getSubscription((Long) msg.get("subscriptionId"), clientId);
		HTSPServerConnection c = monitor.getServerConnection(s.serverConnectionId);
		msg.put("subscriptionId", s.serverSubscriptionId);
		c.send(msg);
	}
	
	public synchronized void stop(HTSMsg msg, int clientId) throws IOException {
		Subscription s = getSubscription((Long) msg.get("subscriptionId"), clientId);
		HTSPServerConnection c = monitor.getServerConnection(s.serverConnectionId);
		msg.put("subscriptionId", s.serverSubscriptionId);
		c.send(msg);		
	}

	public synchronized void status(HTSMsg msg, int clientId) throws IOException {
		Subscription s = getSubscription((Long) msg.get("subscriptionId"), clientId);
		HTSPServerConnection c = monitor.getServerConnection(s.serverConnectionId);
		msg.put("subscriptionId", s.serverSubscriptionId);
		c.send(msg);		
	}

	public synchronized void queueStatus(HTSMsg msg, int clientId) throws IOException {
		Subscription s = getSubscription((Long) msg.get("subscriptionId"), clientId);
		HTSPServerConnection c = monitor.getServerConnection(s.serverConnectionId);
		msg.put("subscriptionId", s.serverSubscriptionId);
		c.send(msg);		
	}

	public synchronized void signalStatus(HTSMsg msg, int clientId) throws IOException {
		Subscription s = getSubscription((Long) msg.get("subscriptionId"), clientId);
		HTSPServerConnection c = monitor.getServerConnection(s.serverConnectionId);
		msg.put("subscriptionId", s.serverSubscriptionId);
		c.send(msg);		
	}

	public synchronized void muxpkt(HTSMsg msg, int clientId) throws IOException {
		Subscription s = getSubscription((Long) msg.get("subscriptionId"), clientId);
		HTSPServerConnection c = monitor.getServerConnection(s.serverConnectionId);
		msg.put("subscriptionId", s.serverSubscriptionId);
		c.send(msg);		
	}
	
	
	public int generateSubscriptionId(int clientId){
		//TODO make this work in a good way...
		return i++;
	}
	
	private Subscription getSubscription(long subscriptionId, int clientId){
		//TODO this method is not very efficient...
		for(Subscription s : subscriptions){
			if (s.clientId==clientId && s.clientSubscriptionId==subscriptionId){
				return s;
			}
		}
		return null;
	}
	
	
	private class Subscription{
		
		int serverConnectionId;
		int clientId;
		long serverSubscriptionId;
		long clientSubscriptionId;
		HTSPMonitor monitor;
	
		public Subscription(long channelId, int clientId, long serverSubscriptionId, int serverConnectionId, HTSPMonitor monitor) {
			this.monitor=monitor;
			this.serverConnectionId = serverConnectionId;
			this.clientId=clientId;
			this.serverSubscriptionId=serverSubscriptionId;
			this.clientSubscriptionId=monitor.subscriptions.generateSubscriptionId(clientId);
			try {
				monitor.getClient(clientId).subscribe(channelId, clientSubscriptionId);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
