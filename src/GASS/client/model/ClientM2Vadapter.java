package GASS.client.model;

import java.util.ArrayList;

import dataType.AFile;

public interface ClientM2Vadapter {
	
	/**
	 * Add one new File on server file list on view 
	 * @param f
	 */
	public void addFileOnList(String s);
	
	/**
	 * Add multiple files on server file list on view
	 * @param list
	 */
	public void addFilesOnList(ArrayList<String> list);
	
	/**
	 * Delete a particular file on server file list
	 * @param f
	 */
	public void deleteFileOnList(String s);
	
	
	/**
	 * Append interactive information on view 
	 * @param string
	 */
	public void appendInfo(String string);

	/**
	 * refresh the file list using list received from server
	 * @param list the receiver list of file names
	 */
	public void refreshFileList(ArrayList<String> list);
	
	public void receiveLogFromServer();
}
