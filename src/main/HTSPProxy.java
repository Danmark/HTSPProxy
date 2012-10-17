package main;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import server.HTSPServer;
import shared.Config;
import client.HTSPClient;
import client.ServerInfo;


public class HTSPProxy {
	public static List<ServerInfo> servers;
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		Config conf = new Config();
		servers = conf.getServers();
		
		HTSPServer server = new HTSPServer(9982);
		server.start();
		
		List<HTSPClient> clients = new ArrayList<HTSPClient>();
		int clientid = 0;
		for (ServerInfo serverInfo : servers) {
			HTSPClient client = new HTSPClient(serverInfo, server);
			client.setClientid(clientid++);
			clients.add(client);
			client.start();
			client.hello();
			client.enableAsyncMetadata();
		}
		
	}
}
