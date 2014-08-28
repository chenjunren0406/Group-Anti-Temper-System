package LocalLog;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import GASS.utils.TransformKit;


/**
 * How to use
 * 		XMLHelper xml = new XMLHelper("/Users/junrenchen/Desktop","fk");
 * 		first parameter is filepath
 * 		secode parameter is filename(fk.xml)
 *		xxml.addlog(new Log("aaa", 2, "bbb", "ccc", "April"));
 * @author junrenchen
 *
 */

public class XMLHelper  {
	
	
	/**
	 * filePath, where you wants to store your xml file
	 */
	private String filepath;
	
	private String rootName;
	
	private Document doc;
	
	Element root,logNo,identity,opType,opObject,sign,date;
	
	public XMLHelper(String path, String rootElementName){
		filepath = path;
		rootName = rootElementName;
	}
	
	
	/**
	 *  lv 1: root
	 *  lv 2: log no
	 *  lv 3: identity, opType, opObject, sign,date
	 */
	public void init(){
		
		root = new Element(rootName);

		root.setAttribute("counter", "0");
		
		/*
		 * 开始建文件
		 */
		doc = new Document(root);
		
		writeIntoXML(doc,filepath);
		
	}
	
	/**
	 * get all logs
	 * @return list of logs
	 */
	public ArrayList<Log> getAllLog(){
		SAXBuilder builder = new SAXBuilder();
		ArrayList<Log>loglist = new ArrayList<Log>();
		
		try {
			 doc = builder.build(filepath);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		Element getroot = doc.getRootElement();
		
		java.util.List<Element> list = getroot.getChildren();
		
		Iterator<Element> iterator = list.iterator();
		
		while(iterator.hasNext()){
			
			Element cur = iterator.next();
			
			Log curLog = new Log(UUID.fromString(cur.getAttribute("logId").getValue()),
					cur.getChild("identity").getText(), 
					Integer.parseInt(cur.getChild("opType").getText()),
					cur.getChild("Object").getText(),
					cur.getChild("date").getText());
			
			curLog.setSign(TransformKit.hexString2Bytes(cur.getChild("sign").getText()));
			
			loglist.add(curLog);
		}
		
		return loglist; 
	}
	
	public Log getOneLog(String logId) {
		Log ret = null;
		SAXBuilder builder = new SAXBuilder();
		try {
			 doc = builder.build(filepath);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		Element getroot = doc.getRootElement();
		
		java.util.List<Element> list = getroot.getChildren();
		
		Iterator<Element> iterator = list.iterator();
		
		while(iterator.hasNext()){
			Element cur = iterator.next();
			if (cur.getAttribute("logId").getValue().equals(logId)) {
				ret = new Log(UUID.fromString(logId),
					cur.getChild("identity").getText(), 
					Integer.parseInt(cur.getChild("opType").getText()),
					cur.getChild("Object").getText(),
					cur.getChild("date").getText());
				
				ret.setSign(TransformKit.hexString2Bytes(cur.getChild("sign").getText()));
				break;
			}
		}
		
		return ret;
	}
	
	/**
	 * add log to xml
	 * @param newLog
	 * @throws JDOMException
	 * @throws IOException
	 */
	public synchronized void addlog(Log newLog) throws Exception{
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new File(filepath));
		root = doc.getRootElement();
		int currentNo = Integer.parseInt(root.getAttributeValue("counter")) + 1;
		
		root.setAttribute("counter",String.valueOf(currentNo));
		
		logNo = new Element("logNo");
		logNo.setAttribute("logId",String.valueOf(newLog.getLogId()));
		root.addContent(logNo);
		
		identity = new Element("identity");
		identity.setText(newLog.getidentity());
		logNo.addContent(identity);
		
		opType = new Element("opType");
		opType.setText(String.valueOf(newLog.getOptype()));
		logNo.addContent(opType);
		
		opObject = new Element("Object");
		opObject.setText(newLog.getopObject());
		logNo.addContent(opObject);
		
		sign = new Element("sign");
		sign.setText(TransformKit.bytes2HexString(newLog.getSign()));
		logNo.addContent(sign);
		
		date = new Element("date");
		date.setText(newLog.getdate());
		logNo.addContent(date);
		
		writeIntoXML(doc, filepath);
		
	}
	
	private void writeIntoXML(Document doc,String filepaString){
		org.jdom2.output.Format f = org.jdom2.output.Format.getCompactFormat();
		f.setEncoding("GB2312");
		f.setIndent("  ");
		XMLOutputter xmlOut = new XMLOutputter(f);
		try {
			xmlOut.output(doc, new FileOutputStream(filepath));
		} catch (IOException e) {
				e.printStackTrace();
		}
	}
	
}
