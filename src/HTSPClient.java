import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.*;


public class HTSPClient extends Thread {
	Socket socket;
	BufferedOutputStream os;
	BufferedInputStream is;
	TVChannels chan;
	
	
	public HTSPClient(String addr, int port){
		this.chan = new TVChannels();
		try {
			socket = new Socket(addr, port);
			os = new BufferedOutputStream(socket.getOutputStream());
			is = new BufferedInputStream(socket.getInputStream());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void hello() throws IOException{
		
		String method="hello";
		Map<String, Object> map=new HashMap<String,Object>();
		map.put("htspversion", new Long(6));
		map.put("clientname", "HTSPProxy");
		map.put("clientversion", "alpha");
		HTSMsg hello = new HTSMsg(method, map);
		send(hello);
	}
	
	public void enableAsyncMetadata() throws IOException{
		String method="enableAsyncMetadata";
		HTSMsg enableAsyncMetadata = new HTSMsg(method);
		send(enableAsyncMetadata);
	}
	
	private void send(HTSMsg msg) throws IOException{
		byte[] bytes = msg.serialize();
		System.out.println("Sending " + msg.get("method") + " " + msg);
		os.write(bytes);
		os.flush();
	}
	
	public HTSMsg rcv() throws IOException{
		byte[] lenBytes = new byte[4];
		is.read(lenBytes, 0, 4);
		long len = ByteBuffer.wrap(lenBytes,0,4).getInt();
		byte[] msg = new byte[(int)len];
		while (is.read(msg,0,(int)len)!=len){
			try {
				wait(100);
				//TODO check wait time
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		HTSMsg htsMsg = new HTSMsg(msg);
		handleReply(htsMsg);
		System.out.println("Recived " + htsMsg);
		
		return htsMsg; 
	}
	
	public void handleReply(HTSMsg reply){
		String method = (String) reply.get("method");
		if (method == null){
		} else if(method.equals("channelAdd")){
			//System.out.println("adding Channel "+ reply.map.get("channelName") + " id = " + reply.map.get("channelId"));
			chan.add(reply);
			
		} else if(method.equals("channelUpdate")){
			//System.out.println("updating Channel where id= " + reply.map.get("channelId"));
			chan.update(reply);
			
		} else if (method.equals("channelDelete")){
			chan.remove(reply);
		} else{
			//TODO something is wrong. Do something about it.
		}
	}
	
	public void run(){
		try {
			hello();
			while(true){
				rcv();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
}
