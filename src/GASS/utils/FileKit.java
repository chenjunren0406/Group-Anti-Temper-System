package GASS.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Random;

import FileRecord.FileManage;
import FileRecord.Filerec;
import LocalLog.Log;

/**
 * Some magic functions to deal with file 
 * @author junrenchen
 *
 */
public class FileKit {
	public static void write(byte[] fileContent, File newFileDescriptor)
			throws IOException {
		if (fileContent == null || newFileDescriptor == null)
			System.out.println("file is null");

		BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(newFileDescriptor));
		output.write(fileContent);
		output.close();
	}

	@SuppressWarnings("resource")
	public static byte[] readFileIntoByteArray(String filepath) {
		FileChannel fc = null;
		try {
			fc = new RandomAccessFile(filepath, "r").getChannel();
			MappedByteBuffer byteBuffer = fc.map(MapMode.READ_ONLY, 0, fc.size()).load();
			System.out.println(byteBuffer.isLoaded());
			byte[] result = new byte[(int) fc.size()];
			if (byteBuffer.remaining() > 0) {
				byteBuffer.get(result, 0, byteBuffer.remaining());
			}

			fc.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static FileToSend encryptFile(File file, byte[] key) {
		if (file == null) {
			System.err.println("The file to be encrpyted is null!");
			return null;
		}
		
		try {
			File fileToSend = new File(file.getName() + "_encrypted");
			fileToSend.deleteOnExit();
			fileToSend.createNewFile();
			FileOutputStream fos = new FileOutputStream(fileToSend);
			FileInputStream fis = new FileInputStream(file);
			
			// generate a file encryption key
			byte[] feKey = null;
			if (key == null || key.length != 16) {
				System.err.println("Create a new key for file: " + file.getName());
				feKey = new byte[16];
				Random r = new Random(System.currentTimeMillis());
				r.nextBytes(feKey);
				
				//Add the new file record to file manager
				FileManage fm=new FileManage();
				fm.addFileRecord(new Filerec(file.getName(), TransformKit.bytes2HexString(feKey)));
			}
			else{
				//TODO Do we need to check if the key matches the corresponding file record???
				feKey = key;
			}
			
			boolean clear = false;
			// encrypt and send file
			try {
				CryptoKit.encryptAES(feKey, fis, fos);
				clear = true;
			} catch (Throwable e) {
				e.printStackTrace();
			}
			
			fos.close();
			fis.close();
			  
			return clear ? new FileToSend(fileToSend.getAbsolutePath(), file.getName(), feKey) : null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("resource")
	public static String readFileIntoString(String filePath){
		String result = "";
		String linetxt = "";
		BufferedReader br = null;
	
		try {
			br = new BufferedReader(new FileReader(new File(filePath)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			while((linetxt = br.readLine()) != null){
				result += linetxt;
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		return result;
	}
}
