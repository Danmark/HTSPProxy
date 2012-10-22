package shared;

import java.util.ArrayList;
import java.util.Collection;


public class Tags {
	/**
	 * required
	 */
	public final static String TAGID = "tagId";

	/**
	 * required
	 */
	public final static String TAGNAME = "tagName";

	/**
	 * optional
	 */
	public final static String TAGICON = "tagIcon";

	/**
	 * optional
	 */
	public final static String TAGTITLEICON = "tagTitledIcon";

	/**
	 * optional
	 */
	public final static String MEMBERS = "members";

	public static final String[] HTSMsgFields = {TAGID,TAGNAME,TAGICON,TAGTITLEICON,MEMBERS};

	private ArrayList<Tag> tags;
	private HTSPMonitor monitor;

	public Tags(HTSPMonitor monitor){
		this.monitor=monitor;
		tags = new ArrayList<Tag>();
	}

	public synchronized void add(long tagId, int clientId) {
		tags.add(new Tag(tagId, clientId));
	}

	public synchronized void update(long taglId, int clientId) {
		//TODO send to clients with AsyncMetadata enabled
	}

	public synchronized void remove(long tagId, int clientId) {
		int i=0;
		for(Tag tag:tags){
			if(tag.getTagId()==tagId && tag.getClientId()==clientId){
				tags.remove(i++);
			}
		}

	}

	public synchronized HTSMsg get(long tagId, int clientId){
		HTSMsg ret = monitor.getClient(clientId).getTag(tagId);
		//TODO update ret.channelId and maybe eventId and nextEventId, and tags...
		return ret;
	}

	public Collection<HTSMsg> getAll(){
		Collection<HTSMsg> ret = new ArrayList<HTSMsg>();
		for(Tag tag:tags){
			ret.add(get(tag.getTagId(),tag.getClientId()));
		} 
		return ret;
	}

	private class Tag{

		protected long tagId;
		protected int clientId;

		public Tag(long tagId, int clientId) {
			this.tagId = tagId;
			this.clientId = clientId;
		}

		public long getTagId() {
			return tagId;
		}

		public int getClientId() {
			return clientId;
		}
	}

}
