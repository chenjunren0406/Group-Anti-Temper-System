package GASS.client.controller;

import java.io.File;
import java.util.ArrayList;

import GASS.client.model.*;
import GASS.client.view.*;


public class ClientController {
	
	/**
	 * The MainModel
	 */
	private ClientModel model;
	
	/**
	 * The MainView
	 */
	private ClientMainUI view;
	
	/**
	 * The Constructor
	 */
	public ClientController(){
		/**
		 * Initialize the MainModel instance
		 */
		model=new ClientModel(new ClientM2Vadapter() {
			public void addFileOnList(String s) {
				view.addFileOnList(s);
			}
			
			public void addFilesOnList(ArrayList<String> list) {
				view.addFilesOnList(list);
			}

			public void deleteFileOnList(String s) {
				view.deleteFileOnList(s);
			}
			
			public void appendInfo(String string) {
				view.appendInfo(string);
			}
			
			public void refreshFileList(ArrayList<String> list) {
				view.refreshList(list);
			}

			@Override
			public void receiveLogFromServer() {
				view.mergeLogFromServer();
			}
		});
		
		/**
		 * Initialize the MainView instance
		 */
		view=new ClientMainUI(new ClientV2Madapter(){
			public void uploadLocalFile(String filepath) {
				//model.upload(file);
			}
            public void uploadLocalFile(File file){
            	model.upload(file);
            }
			public void downloadSeverFile(String filename) {
				model.download(filename);
			}

			public void deleteSeverFile(String filename) {
				model.deleteSeverFile(filename);
			}

			public void refreshSeverFile() {
				model.refreshSeverFile();
			}

			@Override
			public void sendJoinRequest(String code, String ip) {
				model.sendRequest(code, ip);
			}

			@Override
			public void download_log() {
				model.download_log();
			}
			
			@Override
			public void leave() {
				model.leaveGroup();
			}
			
			@Override
			public void logout() {
				model.logout();
			}

		});
	}
	/**
	 * Start the controller
	 */
	public void start(){
		model.start();
		view.start();
	}
	/**
	 * new Main controller
	 * @param args
	 */
	public static void main(String[] args) {
		(new ClientController()).start();
	}
}
