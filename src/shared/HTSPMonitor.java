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
		this.tags = new Tags();
		this.events = new Events();
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
	
	public synchronized void addTag(HTSMsg tag) {
		tags.add(tag);		
	}
	
	public synchronized void updateTag(HTSMsg msg) {
		tags.update(msg);
	}
	
	public synchronized void removeTag(HTSMsg msg) {
		tags.remove(msg);
	}
	
	public synchronized void addEvent(HTSMsg msg) {
		events.add(msg);
	}
	
	public synchronized void updateEvent(HTSMsg msg) {
		events.update(msg);
	}
	
	public synchronized void removeEvent(HTSMsg msg) {
		events.remove(msg);
	}
	
	public synchronized HTSPClient getClient(int id){
		return clients.get(id);
	}

	public synchronized void addServer(HTSPServer server) {
		this.server = server;
	}

	public synchronized HTSMsg getEvent(Long eventId) {
		return events.get(eventId);
	}

	public synchronized Collection<HTSMsg> getAllTags() {
		// TODO Auto-generated method stub
		return tags.clone().getAll();
	}

	public synchronized Collection<HTSMsg> getAllChannels() {
		// TODO Auto-generated method stub
		return chan.getAll();
	}


}
