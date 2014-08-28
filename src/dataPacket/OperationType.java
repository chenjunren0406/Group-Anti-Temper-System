package dataPacket;

public class OperationType {
	public static final int login = 1;
	public static final int exit = 2;
	public static final int loginSuccessful = 3;
	public static final int loginFail = 4;
	
	public static final int fileUpload = 11;
	public static final int fileDownload = 12;
	public static final int fileList = 14;
	public static final int fileDelete = 13;
	public static final int fileOpFail = 10;
	
	public static final int keyRenew = 21;
	public static final int keyRenewFail = 20;
	
	public static final int inviteRequest = 31;
	public static final int inviteApproved = 32;
	public static final int inviteReject = 33;
	public static final int welcomeMsg = 34;
	public static final int inviteFail = 30;
	
	public static final int leaveNotify = 41;
	public static final int leaveACK = 42;
	public static final int leaveFail = 40;
	public static final int broadcastMessage = 50;
	
	public static final int logDownload = 61;
	
	public static final int textMessage = 99;
}
