package shared;

import java.util.ArrayList;
import java.util.Collection;




public class TVChannels {
	/**
	 * required
	 */
	public static final String CHANNELID = "channelId";
	
	/**
	 * required
	 */
	public static final String CHANNELNUMBER = "channelNumber";
	
	/**
	 * required
	 */
	public static final String CHANNELNAME = "channelName";
	
	/**
	 * optional
	 */
	public static final String CHANNELICON = "channelIcon";
	
	/**
	 * optional
	 */
	public static final String EVENTID = "eventId";
	
	/**
	 * optional
	 */
	public static final String NEXTEVENTID = "nextEventId";
	
	/**
	 * optional
	 */
	public static final String TAGS = "tags";
	
	/**
	 * optional
	 */
	public static final String SERVICES = "services";
	
	public static final String[] HTSMsgFields = {CHANNELID,CHANNELNUMBER,CHANNELNAME,CHANNELICON,EVENTID,NEXTEVENTID,TAGS,SERVICES};

	private ArrayList<TVChannel> channels;
	private HTSPMonitor monitor;
	
	public TVChannels(HTSPMonitor monitor){
		this.monitor=monitor;
		channels = new ArrayList<TVChannel>();
	}
	
	public synchronized void add(long channelId, int clientId) {
		channels.add(new TVChannel(channelId, clientId));
	}

	public synchronized void update(long channelId, int clientId) {
		//TODO send to clients with AsyncMetadata enabled
	}

	public synchronized void remove(long channelId, int clientId) {
		int i=0;
		for(TVChannel chann:channels){
			if(chann.getChannelId()==channelId && chann.getClientId()==clientId){
				channels.remove(i++);
			}
		}
				
	}
	
	public synchronized HTSMsg get(long channelId, int clientId){
		HTSMsg ret = monitor.getClient(clientId).getChannel(channelId);
		//TODO update ret.channelId and maybe eventId and nextEventId, and tags...
		return ret;
	}
	
	public Collection<HTSMsg> getAll(){
		Collection<HTSMsg> ret = new ArrayList<HTSMsg>();
		for(TVChannel channel:channels){
			ret.add(get(channel.getChannelId(),channel.getClientId()));
		} 
		return ret;
	}
	
	private class TVChannel{

		protected long channelId;
		protected int clientId;

		public TVChannel(long channelId, int serverId) {
			this.channelId = channelId;
			this.clientId = serverId;
		}

		public long getChannelId() {
			return channelId;
		}
		
		public int getClientId() {
			return clientId;
		}
	}

}
