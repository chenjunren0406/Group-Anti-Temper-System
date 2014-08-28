package pushPacket;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.text.StyledEditorKit.ForegroundAction;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import GASS.utils.TransformKit;
import dataType.P2PMessage;
import dataType.P2PMessage.MessageType;

public class pushMessageLog {
	
	private String logPath;
	
	public pushMessageLog(){
		this.logPath = "./pushlog.xml";
		createFile();
	}
	
	private void createFile(){
		File tmp = new File(logPath);
		if (!tmp.exists()) {
			Element root = new Element("pushMessageList");
			Document doc = new Document(root);
			writeIntoXML(doc, logPath);
		}
	}
	
	
	/**
	 * add pushmessage into log
	 */
	public void addPushMessage(String from, String to, byte[] content, MessageType type,String title){
		SAXBuilder builder = new SAXBuilder();
		Document _doc = null;
		
		try {
			 _doc = builder.build(logPath);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		Element _root = _doc.getRootElement();
		
		Element pushMessageInfo = new Element("pushMessageInfo");
		
		/*
		 * generate one log 
		 */
		pushMessageInfo.setAttribute("from", from);
		pushMessageInfo.setAttribute("to", to);
		pushMessageInfo.setAttribute("type",type.toString());
		pushMessageInfo.setAttribute("title", title);
		pushMessageInfo.setText(TransformKit.bytes2HexString(content));
		
		/*
		 * add to log file
		 */
		_root.addContent(pushMessageInfo);
		
		writeIntoXML(_doc, logPath);
	}
	
	/**
	 * input client's id, then return a list, which full of pushPacket need push to him
	 * @param id
	 * @return
	 */
	public ArrayList<PushPacket<P2PMessage>> getPushPacket(String id){
		
		ArrayList<PushPacket<P2PMessage>> pushlist = new ArrayList<PushPacket<P2PMessage>>();
		
		SAXBuilder builder = new SAXBuilder();
		Document _doc = null;
		
		try {
			 _doc = builder.build(logPath);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		Element _root = _doc.getRootElement();
		
		
		Iterator<Element> iterator = _root.getChildren().iterator();
		
		HashSet<Element> toRemove = new HashSet<Element>();
		
		
		while(iterator.hasNext()){
			
			Element tmp = iterator.next();
			
			/*
			 * then add it to push list 
			 */
			
			System.out.println("log checking....." + tmp.getAttributeValue("to"));
			
			if(tmp.getAttributeValue("to").equals(id)){
				
				
				P2PMessage _p2p = new P2PMessage(tmp.getAttributeValue("from"), 
						tmp.getAttributeValue("to"),
						MessageType.valueOf(tmp.getAttributeValue("type")), 
						TransformKit.hexString2Bytes(tmp.getText()),
						tmp.getAttributeValue("title"));
				
				PushPacket<P2PMessage> tmpPacket = new PushPacket<P2PMessage>(P2PMessage.class, _p2p);
				
				pushlist.add(tmpPacket);
				
				/*
				 * delete this log 
				 */
				
				toRemove.add(tmp);
			}
		}
		
		for (Element e : toRemove)
			_root.removeContent(e);
		
		toRemove.clear();
		
		writeIntoXML(_doc, logPath);
		
		return pushlist;
	}
	
	/*
	 * write into file
	 */
	private void writeIntoXML(Document doc,String filepaString){
		org.jdom2.output.Format f = org.jdom2.output.Format.getCompactFormat();
		f.setEncoding("GB2312");
		f.setIndent("  ");
		XMLOutputter xmlOut = new XMLOutputter(f);
		try {
			xmlOut.output(doc, new FileOutputStream(logPath));
		} catch (IOException e) {
				e.printStackTrace();
		}
	}
}
