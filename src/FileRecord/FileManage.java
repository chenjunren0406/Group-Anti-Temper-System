package FileRecord;

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

public class FileManage {
	/**
	 * filePath, where you wants to store your xml file
	 */
	private static String filepath="FileRecord.xml";
	
	private Document doc;
	
	Element root,file,name,FEKey;
	
	public FileManage(){
		//init();
	}
	
	
	/**
	 *  lv 1: root
	 *  lv 2: file
	 *  lv 3: name, FEKey
	 */
	public void init(){
		
		root = new Element("file_mangemnt");
		
		doc = new Document(root);
		
		writeIntoXML(doc,filepath);
		
	}
	
	/**
	 * get all members
	 * @return
	 */
	public synchronized ArrayList<Filerec> getAllFile(){
		
		SAXBuilder builder = new SAXBuilder();
		ArrayList<Filerec>filelist = new ArrayList<Filerec>();
		
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
			
			Filerec curfile = new Filerec(cur.getAttributeValue("name"), 
							cur.getChild("FEKey").getText());
			
			filelist.add(curfile);
		}
		
		return filelist; 
	}
	
	public synchronized Filerec findFEKey(String filename){
		ArrayList<Filerec> rec=getAllFile();
		for (Filerec r:rec){
			if (r.getfilename().equals(filename)) return(r);
		}
		return null;
	}
	
	/**
	 * add file to xml
	 * @param newfile
	 * @throws JDOMException
	 * @throws IOException
	 */
	public synchronized void addFileRecord(Filerec newfile) throws JDOMException, IOException{
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new File(filepath));
		root = doc.getRootElement();

		file = new Element("file");
		file.setAttribute("name",newfile.getfilename());
		root.addContent(file);
		
		FEKey = new Element("FEKey");
		FEKey.setText(newfile.getFEkey());
		file.addContent(FEKey);
		
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
