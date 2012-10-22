package shared;

import java.util.ArrayList;
import java.util.Collection;

public class Events {
	/**
	 * required
	 */
	public final static String EVENTID = "eventId";   //        u32   required   Event ID
	/**
	 * required
	 */
	public final static String CHANNELID = "channelId"; //          u32   required   The channel this event is related to.
	/**
	 * required
	 */
	public final static String START = "start";//              u64   required   Start time of event, UNIX time.
	/**
	 * required
	 */
	public final static String STOP = "stop"; //              u64   required   Ending time of event, UNIX time.
	/**
	 * optional
	 */
	public final static String TITLE = "title";//              str   optional   Title of event.
	/**
	 * optional
	 */
	public final static String SUMMARY = "summary";//            str   optional   Short description of the event (Added in version 6).
	/**
	 * optional
	 */
	public final static String DESCRIPTION = "description";//        str   optional   Long description of the event.
	/**
	 * optional
	 */
	public final static String SERIERLINKID = "serieslinkId";//       u32   optional   Series Link ID (Added in version 6).
	/**
	 * optional
	 */
	public final static String EPISODEID = "episodeId";//          u32   optional   Episode ID (Added in version 6).
	/**
	 * optional
	 */
	public final static String SEASONID = "seasonId";//           u32   optional   Season ID (Added in version 6).
	/**
	 * optional
	 */
	public final static String BRANDID = "brandId";//            u32   optional   Brand ID (Added in version 6).
	/**
	 * optional
	 */
	public final static String CONTENTTYPE = "contentType";//        u32   optional   DVB content code (Added in version 4, Modified in version 6*).
	/**
	 * optional
	 */
	public final static String AGERATING = "ageRating";//          u32   optional   Minimum age rating (Added in version 6).
	/**
	 * optional
	 */
	public final static String STARRATING = "starRating";//         u32   optional   Star rating (1-5) (Added in version 6).
	/**
	 * optional
	 */
	public final static String FIRSTAIRED = "firstAired";//         s64   optional   Original broadcast time, UNIX time (Added in version 6).
	/**
	 * optional
	 */
	public final static String SEASONNUMBER = "seasonNumber";//       u32   optional   Season number (Added in version 6).
	/**
	 * optional
	 */
	public final static String SEASONCOUNT = "seasonCount";//        u32   optional   Show season count (Added in version 6).
	/**
	 * optional
	 */
	public final static String EPISODENUMBER = "episodeNumber";//      u32   optional   Episode number (Added in version 6).
	/**
	 * optional
	 */
	public final static String EPISODECOUNT = "episodeCount";//       u32   optional   Season episode count (Added in version 6).
	/**
	 * optional
	 */
	public final static String PARTNUMBER = "partNumber";//         u32   optional   Multi-part episode part number (Added in version 6).
	/**
	 * optional
	 */
	public final static String PARTCOUNT = "partCount";//          u32   optional   Multi-part episode part count (Added in version 6).
	/**
	 * optional
	 */
	public final static String EPISODEONSCREEN = "episodeOnscreen";//    str   optional   Textual representation of episode number (Added in version 6).
	/**
	 * optional
	 */
	public final static String IMAGE = "image";//              str   optional   URL to a still capture from the episode (Added in version 6).
	/**
	 * optional
	 */
	public final static String DVRID = "dvrId";//              u32   optional   ID of a recording (Added in version 5).
	/**
	 * optional
	 */
	public final static String NEXTEVENTID = "nextEventId";//        u32   optional   ID of next event on the same channel.

	public static final String[] HTSMsgFields = {EVENTID,CHANNELID,START,STOP,TITLE,SUMMARY,DESCRIPTION,SERIERLINKID,EPISODEID,SEASONID,BRANDID,CONTENTTYPE,AGERATING,STARRATING,FIRSTAIRED,SEASONNUMBER,SEASONCOUNT,EPISODENUMBER,EPISODECOUNT,PARTNUMBER,PARTCOUNT,EPISODEONSCREEN,IMAGE,DVRID,NEXTEVENTID};
	
	private ArrayList<Event> events;
	private HTSPMonitor monitor;

	public Events(HTSPMonitor monitor){
		this.monitor=monitor;
		events = new ArrayList<Event>();
	}

	public synchronized void add(long eventId, int clientId) {
		events.add(new Event(eventId, clientId));
	}

	public synchronized void update(long eventlId, int clientId) {
		//TODO send to clients with AsyncMetadata enabled
	}

	public synchronized void remove(long eventId, int clientId) {
		int i=0;
		for(Event event:events){
			if(event.getEventId()==eventId && event.getClientId()==clientId){
				events.remove(i++);
			}
		}

	}

	public synchronized HTSMsg get(long eventId, int clientId){
		HTSMsg ret = monitor.getClient(clientId).getEvent(eventId);
		//TODO update ret.channelId and maybe eventId and nextEventId, and tags...
		return ret;
	}

	public Collection<HTSMsg> getAll(){
		Collection<HTSMsg> ret = new ArrayList<HTSMsg>();
		for(Event event:events){
			ret.add(get(event.getEventId(),event.getClientId()));
		} 
		return ret;
	}

	private class Event{

		protected long eventId;
		protected int clientId;

		public Event(long eventId, int clientId) {
			this.eventId = eventId;
			this.clientId = clientId;
		}

		public long getEventId() {
			return eventId;
		}

		public int getClientId() {
			return clientId;
		}
	}

}

