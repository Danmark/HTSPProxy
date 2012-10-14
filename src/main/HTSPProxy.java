package main;
import java.io.IOException;

import client.HTSPClient;


public class HTSPProxy {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		HTSPClient client = new HTSPClient("84.55.118.184", 9982);
		client.start();
		try {
			client.hello();
			client.enableAsyncMetadata();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
