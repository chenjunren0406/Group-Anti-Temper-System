package membership;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

public class MemberManage {
	/**
	 * filePath, where you wants to store your xml file
	 */
	private String filepath;
	
	private Document doc;
	
	Element root,member,identity,publickey,updatedate;
	
	public MemberManage(String path,String filename){
		filepath = path +"/"+ filename + ".xml";
		init();
	}
	
	
	/**
	 *  lv 1: root
	 *  lv 2: member
	 *  lv 3: identity, publickey, updatedate
	 */
	public void init(){
		
		root = new Element("member_management");
		/*
		 * write into document
		 */
		doc = new Document(root);
		
		writeIntoXML(doc,filepath);
		
	}
	
	/**
	 * get all members
	 * @return
	 */
	public synchronized ArrayList<Member> getAllMember(){
		
		SAXBuilder builder = new SAXBuilder();
		ArrayList<Member>memberlist = new ArrayList<Member>();
		
		try {
			 doc = builder.build(filepath);
		} catch (JDOMException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		Element getroot = doc.getRootElement();
		
		java.util.List<Element> list = getroot.getChildren();
		
		Iterator<Element> iterator = list.iterator();
		
		while(iterator.hasNext()){
			
			Element cur = iterator.next();
			
			Member curmember = new Member(cur.getChild("identity").getText(), 
							cur.getChild("publickey").getText(), 
							cur.getChild("updatedate").getText());
			
			memberlist.add(curmember);
		}
		
		return memberlist; 
	}
	
	/**
	 * add member to xml
	 * @param newMmeber
	 * @throws JDOMException
	 * @throws IOException
	 */
	public synchronized void addmember(Member newMember) throws JDOMException, IOException{
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new File(filepath));
		root = doc.getRootElement();

		member = new Element("member");
		member.setAttribute("identity",newMember.getMidentity());
		root.addContent(member);
		
		publickey = new Element("publickey");
		publickey.setText(newMember.getMpublickey());
		member.addContent(publickey);
		
		updatedate = new Element("updatedate");
		updatedate.setText(newMember.getMupdatedate());
		member.addContent(updatedate);
		
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
