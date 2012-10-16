package main;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import shared.Config;
import client.HTSPClient;


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
		
		List<HTSPClient> clients = new ArrayList<HTSPClient>();
		int clientid = 0;
		for (ServerInfo serverInfo : servers) {
			HTSPClient client = new HTSPClient(serverInfo);
			client.setClientid(clientid++);
			clients.add(client);
			client.start();
			client.hello();
			client.enableAsyncMetadata();
		}
		Thread.sleep(1000);
		clients.get(0).subscribe(41, 1, 0);
		
		/**
		 * HTSPServer server = new HTSPServer(clients);
		 * server.start();
		 */
	}
}
