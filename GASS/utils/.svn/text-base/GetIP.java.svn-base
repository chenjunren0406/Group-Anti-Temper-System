package GASS.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *Get Public IP of current machine
 *Only for server get IP of itself!!!!!
 * 
 * Usage: GetIP.GetPublicIP()
 * return String (IPv4) 
 * @author Jeremy
 *
 */
public class GetIP {
	   public static String GetPublicIP() {
		   	  String urlToRead="http://169.254.169.254/latest/meta-data/public-ipv4";
		      URL url;
		      HttpURLConnection conn;
		      BufferedReader rd;
		      String line;
		      String result = "";
		      try {
		         url = new URL(urlToRead);
		         conn = (HttpURLConnection) url.openConnection();
		         conn.setRequestMethod("GET");
		         rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		         while ((line = rd.readLine()) != null) {
		            result += line;
		         }
		         rd.close();
		      } catch (IOException e) {
		         e.printStackTrace();
		      } catch (Exception e) {
		         e.printStackTrace();
		      }
		      return result;
		   }
	   
	   public static String getClientPublicIP() {
		   try {
			   URL whatismyip = new URL("http://checkip.amazonaws.com/");
			   BufferedReader in = new BufferedReader(new InputStreamReader(
					   whatismyip.openStream()));
			   String ip = in.readLine(); //you get the IP as a String
			   in.close();
			   return ip;
		   }
		   catch (Exception e) {
			   return null;
		   }
	   }
	   
	   public static void main(String[] args) {
		   URL whatismyip;
		try {
			whatismyip = new URL("http://checkip.amazonaws.com/");
			BufferedReader in = new BufferedReader(new InputStreamReader(
	                   whatismyip.openStream()));

			String ip = in.readLine(); //you get the IP as a String
			System.out.println(ip);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		   
	   }
}
