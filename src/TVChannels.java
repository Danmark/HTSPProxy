import java.util.*;


public class TVChannels {
	private HashMap<Long,HTSMsg> channels;
	
	public static final String CHANNELID = "channelId"; 			//required
	public static final String CHANNELNUMBER = "channelNumber";		//required
	public static final String CHANNELNAME = "channelName";			//required
	public static final String CHANNELICON = "channelIcon";			//optional
	public static final String EVENTID = "eventId";					//optional
	public static final String NEXTEVENTID = "nextEventId";			//optional
	public static final String TAGS = "tags";						//optional
	public static final String SERVICES = "services";				//optional
	
	public static final String[] HTSMsgFields = {CHANNELID,CHANNELNUMBER,CHANNELNAME,CHANNELICON,EVENTID,NEXTEVENTID,TAGS,SERVICES};

	
	public TVChannels(){
		channels = new HashMap<Long,HTSMsg>();
	}
	
	/**
	 * Adds a channel.
	 * @param msg
	 */
	public void add(HTSMsg msg){
		channels.put(((Number)msg.get(CHANNELID)).longValue(), msg);
	}
	
	/**
	 * Updates a channel.
	 * @param reply
	 */
	public void update(HTSMsg msg) {
		HTSMsg chann = channels.get(((Number)msg.get(CHANNELID)).longValue());			
		
		for (String name : msg.keySet()){
			if (Arrays.asList(HTSMsgFields).contains(name)){
				chann.put(name, msg.get(name));
				System.out.println("Updated: " + name);
			}
			else{
				System.out.println("N.B. Unrecognized field: " + name);
			}
		}		
	}
	
	public void remove(HTSMsg msg){
		channels.remove(((Number)msg.get(CHANNELID)).intValue());
	}
	
/*	channelId          u32   required   ID of channel.
	channelNumber      u32   required   Channel number, 0 means unconfigured.
	channelName        str   required   Name of channel.
	channelIcon        str   optional   URL to an icon representative for the channel.
	eventId            u32   optional   ID of the current event on this channel.
	nextEventId        u32   optional   ID of the next event on the channel.
	tags               u32[] optional   Tags this channel is mapped to.
	services           msg[] optional   List of available services (Added in version 5)
*/
	
	public Long[] getChannels(){
		Long[] ret = (Long[]) channels.keySet().toArray();
		return ret;
	}
	
	public Long getChannelNumber(long channelId){
		Long ret = (Long)channels.get(channelId).get(CHANNELNUMBER);
		return ret;
	}
	
	public String getChannelName(long channelId){
		String ret = (String)channels.get(channelId).get(CHANNELNAME);
		return ret;
	}
	
	public String getChannelIcon(long channelId){
		String ret = (String)channels.get(channelId).get(CHANNELICON);
		return ret;			
	}
	
	public Long getEventId(long channelId){
		Long ret = (Long)channels.get(channelId).get(EVENTID);
		return ret;
	}
	
	public Long getNextEventId(long channelId){
		Long ret = (Long)channels.get(channelId).get(NEXTEVENTID);
		return ret;
	}
	
	public Long[] getTags(long channelId){
		Long[] ret = (Long[])channels.get(channelId).get(TAGS);
		return ret;
	}
	
	public HTSMsg[] getServices(long channelId){
		HTSMsg[] ret = (HTSMsg[])channels.get(channelId).get(SERVICES);
		return ret;
	}
}
