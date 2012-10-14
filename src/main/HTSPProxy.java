package main;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import shared.Config;

import client.HTSPClient;


public class HTSPProxy {
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		try {
			Config conf = new Config();
			//TODO List<ServerInfo> servers = conf.getServers();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODO 
		
		List<ServerInfo> servers = new ArrayList<ServerInfo>();
		
		
		ServerInfo voldemort = new ServerInfo("84.55.118.184", "Voldemort");
		voldemort.setUsername("test");
		voldemort.setPassword("test");
		voldemort.setEnableAsyncMetadata(true);
		servers.add(voldemort);
		
		List<HTSPClient> clients = new ArrayList<HTSPClient>();
		for (ServerInfo serverInfo : servers) {
			HTSPClient client = new HTSPClient(serverInfo);
			clients.add(client);
			client.start();
			client.hello();
			client.enableAsyncMetadata();
		}
		
		
		/**
		 * HTSPServer server = new HTSPServer(clients);
		 * server.start();
		 */
	}
}
