package shared;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import server.HTSPServer;
import server.HTSPServer.HTSPServerConnection;

import client.HTSPClient;
import client.ClientInfo;

public class HTSPMonitor {
	List<ClientInfo> servers;
	List<HTSPClient> clients;
	List<HTSPServerConnection> serverConnections;
	TVChannels chan;
	Tags tags;
	Events events;
	Subscriptions subscriptions;
	
	private HTSPServer server;
	
	public HTSPMonitor(){
		Config conf = new Config();
		servers = conf.getServers();
		clients = new ArrayList<HTSPClient>();
		serverConnections = new ArrayList<HTSPServerConnection>();
		this.chan = new TVChannels(this);
		this.tags = new Tags(this);
		this.events = new Events(this);
		this.subscriptions = new Subscriptions(this);
	}

	/**
	 * @param e
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public synchronized boolean addHTSPClient(HTSPClient client) {
		return clients.add(client);
	}

	/**
	 * @param index
	 * @return
	 * @see java.util.List#get(int)
	 */
	public synchronized HTSPClient getHTSPClient(int index) {
		return clients.get(index);
	}

	public synchronized List<ClientInfo> getServers() {
		return servers;
	}
	
	public synchronized void addChannel(long channelId, int clientId){
		chan.add(channelId,clientId);
	}

	public synchronized void updateChannel(long channelId, int clientId) {
		chan.update(channelId,clientId);
	}
	
	public synchronized void deleteChannel(long channelId, int clientId) {
		chan.remove(channelId,clientId);
	}
	
	public synchronized void addTag(long channelId, int clientId) {
		tags.add(channelId,clientId);		
	}
	
	public synchronized void updateTag(long channelId, int clientId) {
		tags.update(channelId,clientId);
	}
	
	public synchronized void removeTag(long channelId, int clientId) {
		tags.remove(channelId,clientId);
	}
	
	public synchronized void addEvent(long eventId, int clientId) {
		events.add(eventId,clientId);
	}
	
	public synchronized void updateEvent(long eventId, int clientId) {
		events.update(eventId,clientId);
	}
	
	public synchronized void removeEvent(long eventId, int clientId) {
		events.remove(eventId,clientId);
	}
	
	public synchronized HTSPClient getClient(int id){
		return clients.get(id);
	}

	public synchronized void addServer(HTSPServer server) {
		this.server = server;
	}

	public synchronized HTSMsg getEvent(long eventId, int clientId) {
		return events.get(eventId, clientId);
	}

	public synchronized Collection<HTSMsg> getAllTags() {
		return tags.getAll();
	}

	public synchronized Collection<HTSMsg> getAllChannels() {
		return chan.getAll();
	}

	public HTSPServer getServer() {
		return server;
	}

	public void subscribe(HTSMsg msg, HTSPServerConnection conn) throws IOException {
		int clientId=0;
		//TODO handle multiple clients
		getHTSPClient(clientId).subscribe((Long)msg.get("channelId"), (Long)msg.get("subscriptionId"));
		subscriptions.add((Long)msg.get("channelId"),clientId,(Long)msg.get("subscriptionId"),conn.getServerConnectionId());
	}
	
	public void startSubscription(HTSMsg msg, int clientId) {
		// TODO Auto-generated method stub
		try {
			subscriptions.start(msg, clientId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stopSubscription(HTSMsg msg, int clientId) {
		// TODO Auto-generated method stub
		try {
			subscriptions.stop(msg, clientId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void subscriptionMuxpkt(HTSMsg msg, int clientId) {
		// TODO Auto-generated method stub
		try {
			subscriptions.muxpkt(msg, clientId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void subscriptionQueueStatus(HTSMsg msg, int clientId) {
		// TODO Auto-generated method stub
		try {
			subscriptions.queueStatus(msg, clientId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void subscriptionSignalStatus(HTSMsg msg, int clientId) {
		// TODO Auto-generated method stub
		try {
			subscriptions.signalStatus(msg, clientId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void subscriptionStatus(HTSMsg msg, int clientId) {
		// TODO Auto-generated method stub
		try {
			subscriptions.status(msg, clientId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public HTSPServerConnection getServerConnection(int serverConnectionId) {
		return serverConnections.get(serverConnectionId);
	}

	public void addServerConnection(HTSPServerConnection serverConnection) {
		serverConnections.add(serverConnection);
		
	}


}
