import java.io.IOException;


public class HTSPProxy {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HTSPClient client = new HTSPClient("84.55.118.184", 9982);
		client.run();
		try {
			client.enableAsyncMetadata();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
