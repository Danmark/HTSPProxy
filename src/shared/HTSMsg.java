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
	private List<Object> list;
	public static final int HMF_NONE = 0;
	public static final int HMF_MAP  = 1;
	public static final int HMF_S64  = 2;
	public static final int HMF_STR  = 3;
	public static final int HMF_BIN  = 4;
	public static final int HMF_LIST = 5;
	public static final int HMF_DBL = 6;
	private byte[] htsMsg;
	private boolean isSerialized;

	public HTSMsg(){
		this.map = new HashMap<String, Object>();
		this.list = new LinkedList<Object>();
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
		deserialize();
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
		byte[] msgByteArray;
		if (isSerialized){
			length =  htsMsg.length;
			msgByteArray = htsMsg;
		}
		else {
			ByteArrayOutputStream msg = new ByteArrayOutputStream();
			Set<Entry<String,Object>> set = map.entrySet();
			for (Entry<String, Object> entry : set){
				createHTSMsgField(entry.getKey(), entry.getValue(), msg);
			}
			for (Object o : list){
				createHTSMsgField("", o, msg);
			}
			length = msg.toByteArray().length;
			msgByteArray = msg.toByteArray();
		}
			byte[] ret = new byte[length+4];
			for (int i = 0; i < 4; i++) {
				int offset = (3 - i) * 8;
				ret[i] = (byte) ((length >>> offset) & 0xFF);
			}
			int i=0;
			for (byte b:msgByteArray){
				ret[i+4]=b;
				i++;
			}
			htsMsg=ret;
			isSerialized=true;
		return ret;
	}
	
	private void createHTSMsgField(String name, Object value, ByteArrayOutputStream msg) throws IOException{
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
			for(Object o : (List<Object>) value){
				dataMsg.put("",o);
			}
			data = dataMsg.serialize();
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

	public void deserialize(){
		int i = 0;

		while(i<htsMsg.length){
			short nameLength=0;
			long dataLength=0;
			String name="";
			Object data="";
			int type = (int)htsMsg[(int)i];
			i++;
			nameLength = (short) getS64(Arrays.copyOfRange(htsMsg, i, i+1),1) ;
			i++;
			dataLength = getS64(Arrays.copyOfRange(htsMsg, i, i+4),4);
			i+=4;
			try {
				name = new String(Arrays.copyOfRange(htsMsg, i, i+nameLength), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				name = "";
				e.printStackTrace();
			}
			i+=nameLength;
			if(dataLength + i > htsMsg.length || i +dataLength<0){
				System.out.println("crazy message!! \n" + DatatypeConverter.printHexBinary(htsMsg));
				return;
			}
			byte[] dataBytes = Arrays.copyOfRange(htsMsg, i, (int)(i + dataLength));
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
			
			if(name.equals("")){
				list.add(data);
			}
			else{
				map.put(name, data);				
			}
			if (data.equals(null)) {
				System.out.println("datan Šr null. " + name);				
			}
			if (name.equals("error")){
				System.out.println(data);
			}
			
			i+=dataLength;			
		}
	}

	private List<Object> getList(byte[] dataBytes, long dataLength) {
		HTSMsg msg = new HTSMsg(dataBytes);
		return msg.list;
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
	
	public Object remove(String name){
		return map.remove(name);
	}
	
	public Object put(String key, Object value){
		isSerialized=false;
		Object ret = value;
		if(key.equals("")){
			list.add(value);
		} else {			
			ret = map.put(key, value);
		}
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
