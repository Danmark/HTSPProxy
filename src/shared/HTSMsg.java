package shared;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

/**
 * A HTSMsg
 */
public class HTSMsg {
	private Map<String,Object> map;
	public static final int HMF_NONE = 0;
	public static final int HMF_MAP  = 1;
	public static final int HMF_S64  = 2;
	public static final int HMF_STR  = 3;
	public static final int HMF_BIN  = 4;
	public static final int HMF_LIST = 5;
	public static final int HMF_DBL = 6;
	private Integer noName;
	private byte[] htsMsg;
	private boolean isSerialized;

	public HTSMsg(){
		this.map = new HashMap<String, Object>();
		noName = new Integer(0);
		isSerialized=false;
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
	
	public HTSMsg(Map<String, Object> map) {
		this();
		this.map.putAll(map);
	}
	public HTSMsg(byte[] msg){
		this();
		htsMsg=msg;
		isSerialized=true;
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
		if (isSerialized){
			return htsMsg;
		}
		
		int length = 0; //TODO set a proper length
		ByteArrayOutputStream msg = new ByteArrayOutputStream();
		Set<Entry<String,Object>> set = map.entrySet();
		for (Entry<String, Object> entry : set){
			String name = entry.getKey();
			Object value = entry.getValue();
			int type = getType(value);
			final byte[] data;
			switch (type) {
			case HMF_MAP:
				// TODO: get the data from the map
				data = new byte[0];
				break;

			case HMF_S64:
				long longData=((Number)value).longValue();
				byte[] tmpData = new byte[]
						{ 
						(byte)(longData >> 56), 
						(byte)(longData >> 48 & 0xff),
						(byte)(longData >> 40 & 0xff),
						(byte)(longData >> 32 & 0xff),
						(byte)(longData >> 24 & 0xff),
						(byte)(longData >> 16 & 0xff), 
						(byte)(longData >> 8 & 0xff), 
						(byte)(longData & 0xff) 
						};
				int i=0;
				while(tmpData[i]==0 && i < 7){
					i++;
				}
				data = Arrays.copyOfRange(tmpData, i, 8);
				break;

			case HMF_STR:
				data=((String)value).getBytes();
				break;

			case HMF_BIN:
				data=(byte[])value;
				break;

			case HMF_LIST:
				HTSMsg dataMsg = new HTSMsg();
				for(Object o : (List) value){
					dataMsg.put("",o);
				}
				data = dataMsg.serialize();
				//TODO make sure this works. Probably needs to handle the names.
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
		isSerialized=true;
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
			nameLength = (short) getS64(Arrays.copyOfRange(msg, i, i+1),1) ;
			i++;
			dataLength = getS64(Arrays.copyOfRange(msg, i, i+4),4);
			i+=4;
			try {
				name = new String(Arrays.copyOfRange(msg, i, i+nameLength), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				name = "";
				e.printStackTrace();
			}
			i+=nameLength;
			if(dataLength + i > msg.length || i +dataLength<0){
				System.out.println("crazy message!! \n" + DatatypeConverter.printHexBinary(msg));
				return;
			}
			byte[] dataBytes = Arrays.copyOfRange(msg, i, (int)(i + dataLength));
			switch (type) {
			case HMF_NONE:
				data = getBytes(dataBytes,dataLength);
				break;
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
				System.out.print("Unsupported data type: " + type);
				return;
			}
			
			if(map.containsKey(name)){
				name = noName.toString();
				noName++;
			}
			if (data.equals(null)) {
				System.out.println("datan �r null. " + name);				
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

	public static long getS64(byte[] dataBytes, long dataLength) {
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
		isSerialized=false;
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
