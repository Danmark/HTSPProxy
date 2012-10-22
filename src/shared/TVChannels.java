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
	
	public synchronized void add(long channelId, int serverId) {
		channels.add(new TVChannel(channelId, serverId));
	}

	public synchronized void update(long channelId, int serverId) {
		//TODO send to clients with AsyncMetadata enabled
	}

	public synchronized void remove(long channelId, int serverId) {
		int i=0;
		for(TVChannel chann:channels){
			if(chann.getChannelId()==channelId && chann.getServerId()==serverId){
				channels.remove(i++);
			}
		}
				
	}
	
	public synchronized HTSMsg get(long channelId, int serverId){
		HTSMsg ret = monitor.getClient(serverId).getChannel(channelId);
		//TODO update ret.channelId and maybe eventId and nextEventId, and tags...
		return ret;
	}
	
	public Collection<HTSMsg> getAll(){
		Collection<HTSMsg> ret = new ArrayList<HTSMsg>();
		for(TVChannel channel:channels){
			ret.add(get(channel.getChannelId(),channel.getServerId()));
		} 
		return ret;
	}
	
	private class TVChannel{

		protected long channelId;
		protected int serverId;

		public TVChannel(long channelId, int serverId) {
			this.channelId = channelId;
			this.serverId = serverId;
		}

		public long getChannelId() {
			return channelId;
		}
		
		public int getServerId() {
			return serverId;
		}
	}

}
