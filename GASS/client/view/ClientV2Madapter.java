package GASS.client.view;

import java.io.File;

public interface ClientV2Madapter {
	/**
	 * when uploading, get the local file that needed to be uploaded
	 * @filepath the path of local file
	 */
	public void uploadLocalFile(String filepath);
	public void uploadLocalFile(File file);
	
	/**
	 * when downloading, get the file needed to be download
	 * @filename the name of file
	 */
	public void downloadSeverFile(String filename);
	/**
	 * when deleting, get the file needed to be deleted
	 * @filename the name of file
	 */
	public void deleteSeverFile(String filename);
	/**
	 * refresh server files
	 */
	public void refreshSeverFile();
	
	/**
	 * send a join request to the administrator in the group with access code
	 * @param code access code to the server
	 * @param ip IP address of the server
	 */
	public void sendJoinRequest(String code, String ip);
	
	/**
	 * down load server's log
	 */
	public void download_log();
	
	/**
	 * leave group
	 */
	public void leave();
	
	/**
	 * logout from server
	 */
	public void logout();
}
