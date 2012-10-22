package client;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Holds information about a server.
 */
public class ServerInfo {
	final static int STANDARD_PORT = 9982;
	final static String STANDARD_IP = "localhost";

	
	private String ip;
	private int port;
	private String name;
	private String username;
	private String password;
	private boolean enableAsyncMetadata = false;
	
	// Properties from hello-response.
	private Long htspversion;
	private String servername;
	private String serverversion;
	private List<String> servercapability;
	private byte[] challenge;
	
	private byte[] digest;
	
	public static class Builder{
		private String name;
		private String ip = STANDARD_IP;
		private int port = STANDARD_PORT;
		private String username = "";
		private String password = "";
		
		public Builder(String name){
			this.name=name;
		}
		
		public Builder ip(String ip){
			this.ip=ip;
			return this;
		}
		
		public Builder port(int port){
			this.port=port;
			return this;
		}
		public Builder username(String username){
			this.username=username;
			return this;
		}
		public Builder password(String password){
			this.password=password;
			return this;
		}
		public ServerInfo build(){
			return new ServerInfo(this);
		}
		
	}
		
	private ServerInfo(Builder builder) {
		this.name = builder.name;
		this.ip = builder.ip;
		this.port = builder.port;
		this.username = builder.username;
		this.password = builder.password;
	}

	public String getIP() {
		return this.ip;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public String getName() {
		return this.name;
	}
	
	/**
	 * Checks if this server needs a authentication.
	 * (Checks if a username is set)
	 * @return
	 */
	public boolean needAuth() {
		return username != null;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean enableAsyncMetadata() {
		return this.enableAsyncMetadata;
	}
	
	public void setEnableAsyncMetadata(boolean enableAsyncMetadata) {
		this.enableAsyncMetadata = enableAsyncMetadata;
	}
	
	public void setHtspversion(Long htspversion) {
		this.htspversion = htspversion;
	}
	
	public Long getHtspversion() {
		return this.htspversion;
	}
	
	public void setServername(String servername) {
		this.servername = servername;
	}
	
	public String getServername() {
		return this.servername;
	}
	
	public void setServerversion(String serverversion) {
		this.serverversion = serverversion;
	}
	
	public String getServerversion() {
		return this.serverversion;
	}
	
	public void setServercapability(List<String> servercapability) {
		this.servercapability = servercapability;
	}
	
	public List<String> getServercapabaility() {
		return this.servercapability;
	}
	
	public void setChallenge(byte[] challenge) {
		this.challenge = challenge;
		if (needAuth() && (getDigest() == null || getDigest().length == 0)) {
			digest();
		}
	}
	
	public byte[] getChallenge() {
		return this.challenge;
	}
	
	public void setDigest(byte[] digest) {
		this.digest = digest;
	}
	
	public byte[] getDigest() {
		return this.digest;
	}
	
	public void digest() {
		if (password == null || challenge == null || challenge.length == 0) {
			throw new RuntimeException("Password or challenge was null (or zero size)");
		}
		try {
			MessageDigest md = MessageDigest.getInstance("sha1");
			md.reset();
			ByteBuffer bb = ByteBuffer.allocate(password.getBytes().length + challenge.length);
			bb.put(password.getBytes());
			bb.put(challenge);
			md.update(bb.array());
			setDigest(md.digest());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Could not generate digest", e);
		}
	}

	public Object getServerId() {
		// TODO Auto-generated method stub
		return null;
	}
}