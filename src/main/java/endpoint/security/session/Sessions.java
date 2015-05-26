package endpoint.security.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import endpoint.security.Users;
import endpoint.security.response.AppResponse;
import enpoint.log.Logger;

/**
 * 
 * @author danielaahumada
 * Manage sessions.xml file
 */

public class Sessions {
	
	private static Properties prop;
	private static String filePath;
	
	static public void init() throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		prop = new Properties();
		filePath = (new File(".")).getCanonicalFile().getCanonicalFile().getCanonicalPath()
				+ "/sessions.xml";
		System.out.println(filePath);
		prop.loadFromXML(new FileInputStream(filePath));
	}
	
	public static String getConfig(String parameter) {
		String s = prop.getProperty(parameter);
		if(s == null)
			return null;
		else{
			long timeS = System.currentTimeMillis();
			long timeInit = Long.parseLong(prop.getProperty(parameter + ".time"));
			if(timeS - timeInit > 1500000000){	
				return null;
			}
			return s;
		}
	}
	
	public static AppResponse setConfig(String value, HttpServletRequest request) {
		long timeS = System.currentTimeMillis();
		String session_id = MD5(timeS + value); 
		if(session_id==null)
			return new AppResponse(2,"Log In Failed","");
		prop.setProperty(session_id, value);
		prop.setProperty(session_id + ".time", Long.toString(timeS));
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		System.out.println("session_ip " + ipAddress);
		   if (ipAddress == null) {  
			   ipAddress = request.getRemoteAddr();  
		   }
		   prop.setProperty(session_id + ".ip", ipAddress);
		try {
			prop.storeToXML(new FileOutputStream(filePath), "");
		} catch (FileNotFoundException e) {
			return new AppResponse(2,"Log In Failed","");
		} catch (IOException e) {
			return new AppResponse(2,"Log In Failed","");
		}
		//System.out.println("session existe:" + prop.containsValue(value));
		return new AppResponse(1,value,session_id);
	}
	
	public static String MD5(String md5) {
	   try {
	        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
	        byte[] array = md.digest(md5.getBytes());
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < array.length; ++i) {
	          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
	       }
	        return sb.toString();
	    } catch (java.security.NoSuchAlgorithmException e) {
	    	return null;
	    }
	}

	public static String decrypt(String encryptedData, String initialVectorString, String secretKey) {
	    String decryptedData = null;
	    try {
	        SecretKeySpec skeySpec = new SecretKeySpec(MD5(secretKey).getBytes(), "AES");
	        IvParameterSpec initialVector = new IvParameterSpec(initialVectorString.getBytes());
	        Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
	        cipher.init(Cipher.DECRYPT_MODE, skeySpec, initialVector);
	        byte[] encryptedByteArray = (new org.apache.commons.codec.binary.Base64()).decode(encryptedData.getBytes());
	        byte[] decryptedByteArray = cipher.doFinal(encryptedByteArray);
	        decryptedData = new String(decryptedByteArray, "UTF8");
	    } catch (Exception e) {
	        return null;
	    }
	    return decryptedData;
	}

	public static String getRole(String session) {
		String user = getConfig(session);
		return Users.getConfig(user + ".role");
	}

	public static AppResponse checkSession(String session, HttpServletRequest request) {
		System.out.println("session:" + session);
		/*request hecho por tests*/
		if(prop.getProperty(session + ".ip")=="127.0.0.1"){
			if(getConfig(session)!=null){
				return new AppResponse(1,"",session);
			}
		}
		/* */
		String session_d = decrypt(session,"\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0","ABCDEF123456789");
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		session_d = session;
		System.out.println("session_d:" + session_d);
	   if (ipAddress == null) {  
		   ipAddress = request.getRemoteAddr();  
	   }
		System.out.println("ip_session=" + prop.getProperty(session_d + ".ip") + " ip=" + ipAddress);
		if(getConfig(session_d)!=null && prop.getProperty(session_d + ".ip").compareTo(ipAddress)==0){
//		if(getConfig(session_d)!=null){
			long timeS = System.currentTimeMillis();
			/*if(timeS - Long.parseLong(prop.getProperty(session_d + ".time")) > 15000000){
				prop.remove(session_d);
				prop.remove(session_d + ".time");
				prop.remove(session_d + ".ip");
				try {
					prop.storeToXML(new FileOutputStream(filePath), "");
				} catch (FileNotFoundException e) {
					return new AppResponse(3,"Invalid Session","");
				} catch (IOException e) {
					return new AppResponse(3,"Invalid Session","");
				}
				return new AppResponse(3,"Session ended","");
			}*/
			return new AppResponse(1,"",session_d);
		}
		else
			return new AppResponse(3,"Invalid Session","");
	}

	public static String getGraphs(String session) {
		String user = getConfig(session);
		return Users.getConfig(user + ".graphs");
	}

	public static String getUser(String session) {
		return getConfig(session);
	}

	public static void deleteSession(String session) {
		String session_d = decrypt(session,"\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0","ABCDEF123456789");
		session_d=session;
		prop.remove(session_d);
		prop.remove(session_d + ".time");
		prop.remove(session_d + ".ip");
		try {
			prop.storeToXML(new FileOutputStream(filePath), "");
		} catch (FileNotFoundException e) {
			Logger.write("Invalid Session - Couldn't delete session" );
		} catch (IOException e) {
			Logger.write("Invalid Session - Couldn't delete session" );
		}
	}
}
