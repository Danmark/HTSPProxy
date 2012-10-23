package main;
import java.io.IOException;

import server.HTSPServer;
import shared.HTSPMonitor;
import client.HTSPClient;
import client.ClientInfo;


public class HTSPProxy {
	public static HTSPMonitor monitor;
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		monitor = new HTSPMonitor();
		
		HTSPServer server = new HTSPServer(9982, monitor);
		monitor.addServer(server);
		server.start();
		
		int clientid = 0;
		for (ClientInfo serverInfo : monitor.getServers()) {
			HTSPClient client = new HTSPClient(serverInfo, monitor);
			client.setClientid(clientid++);
			monitor.addHTSPClient(client);
			client.start();
			client.hello();
			client.enableAsyncMetadata();
		}
		
	}
}
