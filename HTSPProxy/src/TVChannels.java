import java.util.*;


public class TVChannels {
	HashMap<Integer,HTSMsg> channels;
	
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
		channels = new HashMap<Integer,HTSMsg>();
	}
	
	/**
	 * Adds a channel.
	 * @param msg
	 */
	public void add(HTSMsg msg){
		channels.put(((Number)msg.map.get(CHANNELID)).intValue(), msg);
		/*Integer[] tags = new Integer[msg.map.size()];
		LinkedList<Integer> tagList = new LinkedList<Integer>();
		for (Object o : (Object[])msg.map.get(TAGS)){
			tagList.add((Integer)o);
		}
		tagList.toArray(tags);
		TVChannel chan = new TVChannel( ((Number)msg.map.get(CHANNELID)).intValue() );
		for (String name: msg.map.keySet())
			TVChannel.add(name,)
			
		TVChannel chan = new TVChannel(((Number)msg.map.get(CHANNELID)).intValue(),
				(String)msg.map.get(CHANNELNAME),
				((Number)msg.map.get(CHANNELNUMBER)).intValue(), 
				(String)msg.map.get(CHANNELICON),
				((Number)msg.map.get(EVENTID)).intValue(), 
				tags
				);
		channels.put( chan.channelId , chan);*/
	}
	
	/**
	 * Updates a channel.
	 * @param reply
	 */
	public void update(HTSMsg msg) {
		HTSMsg chann = channels.get((Number)msg.map.get(CHANNELID));			
		for (String name : msg.map.keySet()){
			if (Arrays.asList(HTSMsgFields).contains(name))
				chann.map.put(name, msg.map.get(name));
			else
				System.out.println("N.B. Unrecognized field: " + name);
		}		
	}
	
	public void remove(HTSMsg msg){
		channels.remove(((Number)msg.map.get(CHANNELID)).intValue());
	}
	
/*	private class TVChannel {
		int channelId;        
		String channelName;      
		int channelNumber;    
		String channelIcon;     
		int eventId;   //optional
		Integer[] tags;    //optional
		
		public TVChannel(int channelId, String channelName, int channelNumber, String channelIcon, int eventId, Integer[] tags){
			this.channelId=channelId;
			this.channelName=channelName;
			this.channelNumber=channelNumber;    
			this.channelIcon=channelIcon; 
			this.eventId=eventId;
			this.tags=tags;
		}
		
		public TVChannel(int intValue) {
			// TODO Auto-generated constructor stub
		}

		public String toString() {
			return channelName + " (" + channelId + ") Tags = " + Arrays.asList(tags);
		}
	}*/
}
