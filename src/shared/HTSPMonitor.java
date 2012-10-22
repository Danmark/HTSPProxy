package shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import server.HTSPServer;

import client.HTSPClient;
import client.ServerInfo;

public class HTSPMonitor {
	List<ServerInfo> servers;
	List<HTSPClient> clients;
	TVChannels chan;
	Tags tags;
	Events events;
	Subscriptions subscriptions;
	HTSPServer server;
	
	public HTSPMonitor(){
		Config conf = new Config();
		servers = conf.getServers();
		clients = new ArrayList<HTSPClient>();
		this.chan = new TVChannels(this);
		this.tags = new Tags(this);
		this.events = new Events(this);
		this.subscriptions = new Subscriptions();
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

	public synchronized List<ServerInfo> getServers() {
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
		// TODO Auto-generated method stub
		return tags.getAll();
	}

	public synchronized Collection<HTSMsg> getAllChannels() {
		// TODO Auto-generated method stub
		return chan.getAll();
	}


}
