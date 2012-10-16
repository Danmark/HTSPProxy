package shared;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import client.ServerInfo;
import client.ServerInfo.Builder;

public class Config {
	private Document doc;
	
	public Config() {
		File file = new File("config");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(file);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		doc.getDocumentElement().normalize();
	}
	
	public List<ServerInfo> getServers(){
		List<ServerInfo> list = new ArrayList<ServerInfo>();
		NodeList nodeList = doc.getElementsByTagName("server");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);
			
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) n;
				Builder serverInfoBuilder = new ServerInfo.Builder(e.getElementsByTagName("name").item(0).getChildNodes().item(0).getNodeValue());
				serverInfoBuilder.ip(e.getElementsByTagName("ip").item(0).getChildNodes().item(0).getNodeValue());
				serverInfoBuilder.port(Integer.parseInt(e.getElementsByTagName("port").item(0).getChildNodes().item(0).getNodeValue()));
				serverInfoBuilder.username(e.getElementsByTagName("username").item(0).getChildNodes().item(0).getNodeValue());
				serverInfoBuilder.password(e.getElementsByTagName("password").item(0).getChildNodes().item(0).getNodeValue());
				//TODO check if they exist etc.
				
				ServerInfo serverInfo = serverInfoBuilder.build();
				list.add(serverInfo);
			}			
		}
		return list;
	}
}
