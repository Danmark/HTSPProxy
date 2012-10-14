import java.io.IOException;


public class HTSPProxy {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TVChannels channels= new TVChannels();
		HTSPClient client = new HTSPClient("84.55.118.184", 9982, channels);
		try {
			System.out.println("Sending hello.");
			client.hello();
			System.out.println("Recieveing response:");
			client.rcv();
			System.out.println("Response recieved\nSending enableAsyncMetadata");
			client.enableAsyncMetadata();
			while(true){
				//System.out.println("Recieveing response:");
				client.rcv();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
