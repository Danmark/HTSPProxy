package shared;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import main.ServerInfo;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;

public class Config {
	private Document doc;
	
	public Config() throws ParserConfigurationException, SAXException, IOException{
		File file = new File("config");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();
	}
	
	public List<ServerInfo> getServers(){
		List<ServerInfo> list = new ArrayList<ServerInfo>();
		NodeList nodeList = doc.getElementsByTagName("server");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) n;
				ServerInfo serverInfo = new ServerInfo(e.getElementsByTagName("name").item(0).getChildNodes().item(0).getNodeValue());
				//TODO fetch ip and port and so on, check if they exist etc.
				
				list.add(serverInfo);
			}			
		}
		return null;
	}
}
