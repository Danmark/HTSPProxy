package main;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.HTSPClient;


public class HTSPProxy {
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		List<ServerInfo> servers = new ArrayList<ServerInfo>();
		ServerInfo voldemort = new ServerInfo("84.55.118.184", 3982, "Voldemort");
		voldemort.setUsername("test");
		voldemort.setPassword("test");
		voldemort.setEnableAsyncMetadata(true);
		servers.add(voldemort);
		
		for (ServerInfo serverInfo : servers) {
			HTSPClient client = new HTSPClient(serverInfo);
			client.start();
			client.hello();
			client.enableAsyncMetadata();
		}
	}
}
