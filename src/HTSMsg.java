import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.nio.ByteBuffer;


/**
 * A HTSMsg
 * @author origon
 */
public class HTSMsg {
	private Map<String,Object> map;
	public static final int HMF_MAP  = 1;
	public static final int HMF_S64  = 2;
	public static final int HMF_STR  = 3;
	public static final int HMF_BIN  = 4;
	public static final int HMF_LIST = 5;
	private Integer noName;

	public HTSMsg(){
		this.map = new HashMap<String, Object>();
		noName = new Integer(0);
	}
	
	public HTSMsg(String method, Map<String,Object> map){
		this();
		this.map.putAll(map);
		this.map.put("method", method);
	}
	
	public HTSMsg(String method){
		this();
		this.map.put("method", method);
	}
	public HTSMsg(byte[] msg){
		this();
		deserialize(msg);
	}
	
	private int getType(Object entry){
		int ret = 0;
		
		if(entry instanceof Map){
			ret = HMF_MAP;
		}else if (entry instanceof Long){
			ret = HMF_S64;
		}else if (entry instanceof String){
			ret = HMF_STR;
		}else if (entry instanceof byte[]){
			ret = HMF_BIN;
		}else if (entry instanceof List){
			ret = HMF_LIST;
		}else {
			throw new IllegalArgumentException("The datatype '" + entry.getClass() + "' is not implemented");
		}
		
		return ret;
	}

	public byte[] serialize() throws IOException{
		int length = 0; //TODO set a proper length
		ByteArrayOutputStream msg = new ByteArrayOutputStream();
		for (Entry<String, Object> entry : map.entrySet()){
			String name = entry.getKey();
			Object value = entry.getValue();
			int type = getType(value);
			byte[] data = new byte[0];
			switch (type) {
			case HMF_MAP:
				// TODO: get the data from the map
				break;

			case HMF_S64:
				int intData=((Number)value).intValue();
				data = new byte[]
						{ 
						(byte)(intData >> 24), 
						(byte)(intData >> 16 & 0xff), 
						(byte)(intData >> 8 & 0xff), 
						(byte)(intData & 0xff) 
						};
				break;

			case HMF_STR:
				data=((String)value).getBytes();
				break;

			case HMF_BIN:
				data=(byte[])value;
				break;

			case HMF_LIST:
				System.out.println("Got a list, dot know what to do.");
				//TODO get the data from the list
				break;

			default:
				throw new RuntimeException("Unsupported type: " + type);
			}

			//TYPE
			msg.write(type & 0xff);
			//NAMELENGTH
			msg.write((name.getBytes("UTF-8").length));
			//DATALENGTH
			for(int i=3;i>=0;i--)
				msg.write((byte)((data.length >>> i*8) & 0xff));
			//NAME
			msg.write(name.getBytes("UTF-8"));
			//DATA
			msg.write(data);
		}
		length = msg.toByteArray().length;
		byte[] ret = new byte[length+4];
		for (int i = 0; i < 4; i++) {
			int offset = (3 - i) * 8;
			ret[i] = (byte) ((length >>> offset) & 0xFF);
		}
		int i=0;
		for (byte b:msg.toByteArray()){
			ret[i+4]=b;
			i++;
		}
		return ret;
	}

	public void deserialize(byte[] msg){
		int i = 0;

		while(i<msg.length){
			short nameLength=0;
			long dataLength=0;
			String name="";
			Object data="";
			int type = (int)msg[(int)i];
			i++;
			nameLength = msg[(int)i];
			i++;
			ByteBuffer buff = ByteBuffer.wrap(Arrays.copyOfRange(msg, i, i+4));
			dataLength = buff.getInt();
			i+=4;
			try {
				name = new String(Arrays.copyOfRange(msg, i, i+nameLength), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				name = "";
				e.printStackTrace();
			}
			i+=nameLength;
			byte[] dataBytes = Arrays.copyOfRange(msg, i, (int)(i + dataLength));
			switch (type) {
			case HMF_MAP:
				data = getMap(dataBytes,dataLength);
				break;
				
			case HMF_S64:
				data = getS64(dataBytes,dataLength);
				break;
				
			case HMF_STR:
				data = getString(dataBytes,dataLength);
				break;
				
			case HMF_BIN:
				data = getBytes(dataBytes,dataLength);
				break;
				
			case HMF_LIST:
				data = getList(dataBytes,dataLength);
				break;
			default:
				throw new RuntimeException("Unsupported type: " + type);
			}
			
			if(map.containsKey(name)){
				name = noName.toString();
				noName++;
			}
			if (data.equals(null)) {
				System.out.println("datan Šr null. " + name);				
			}
			if (name.equals("error")){
				System.out.println(data);
			}
			
			map.put(name, data);
			i+=dataLength;			
		}
	}

	private List<Object> getList(byte[] dataBytes, long dataLength) {
		HTSMsg msg = new HTSMsg(dataBytes);
		List<Object> data= new ArrayList<Object>(msg.map.values());
		return data;
	}

	private Object getBytes(byte[] dataBytes, long dataLength) {
		return dataBytes.clone();
	}

	private Object getString(byte[] dataBytes, long dataLength) {
		try {
			return new String(ByteBuffer.wrap(dataBytes, 0, (int)dataLength).array(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}		
	}

	private long getS64(byte[] dataBytes, long dataLength) {
		byte[] s64 = new byte[8];
		for (int k = 8-(int)dataLength ; k<8 ;k++)
			s64[k] = dataBytes[k-8+(int)dataLength];
		ByteBuffer buff = ByteBuffer.wrap(s64);
		long data = buff.getLong();
		return data;
	}

	private Map<String,Object> getMap(byte[] dataBytes, long dataLength) {
		Map<String,Object> data = (new HTSMsg(dataBytes)).map;
		return data;
	}
	
	public Object get(String name){
		return map.get(name);
	}
	
	public Object put(String key, Object value){
		Object ret = map.put(key, value);
		return ret;
	}
	
	public Set<String> keySet(){
		Set<String> ret = map.keySet();
		return ret;
	}
	
	public String toString(){
		return map.toString();
	}
}
