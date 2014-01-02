/**
 * @Author:		Shiyao Qi
 * @Date:		2013.11.25
 * @Function:	Operations on file
 * @Email:		qishiyao2008@126.com
 */

package com.twlkyao.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class FileOperation {
	
	private String Tag = "FileOperation"; // The logcat tag
	
	// Define the hashmap to record the basic information of files during the session
	private HashMap<String, String> session =new HashMap<String, String>();
	
	
	/**
	 * Find the specified files according to the filepath
	 * @param keyword The keyword
	 * @param filepath The filepath to start
	 * @return A list that contains the files that meet the conditions
	 */
	public ArrayList<File> findFileByName(String keyword, String filepath) {
		File files = new File(filepath); // Create a new file
		ArrayList<File> list = new ArrayList<File>(); // Used to store the search result
		for(File file : files.listFiles()) { // Recursively search the file
			
//			Log.d("ListFiles",file.getAbsolutePath()); // Log the files under the specified filepath
			
			if(file.isDirectory()) { // The variable file is a directory
				
//				Log.d("ListFile","Directory:"+file.getAbsolutePath()); // If the variable is a directory log it
				
				if(file.getName().contains(keyword)) { // If the filepath contains the keyword, add it to the list
					list.add(file);
				}
				if(file.canRead()) {  // Without this the program will collapse
					list.addAll(findFileByName(keyword, file.getAbsolutePath())); // Recursive into the filepath
				}
				
			} else { // The variable file is a file
				
//				Log.d("ListFile","File:"+file.getAbsolutePath());
				if(file.getName().contains(keyword)) { // If the file's name contains the keyword, add it to the list
					list.add(file);
				}
			}
		}
		return list;
	}
	
	/**
	 * Get the md5 value of the filepath specified file
	 * @param filePath The filepath of the file
	 * @return The md5 value
	 */
	public String fileToMD5(String filePath) {
	    InputStream inputStream = null;
	    try {
	        inputStream = new FileInputStream(filePath); // Create an FileInputStream instance according to the filepath
	        byte[] buffer = new byte[1024]; // The buffer to read the file
	        MessageDigest digest = MessageDigest.getInstance("MD5"); // Get a MD5 instance
	        int numRead = 0; // Record how many bytes have been read
	        while (numRead != -1) {
	            numRead = inputStream.read(buffer);
	            if (numRead > 0)
	                digest.update(buffer, 0, numRead); // Update the digest
	        }
	        byte [] md5Bytes = digest.digest(); // Complete the hash computing
	        return convertHashToString(md5Bytes); // Call the function to convert to hex digits
	    } catch (Exception e) {
	        return null;
	    } finally {
	        if (inputStream != null) {
	            try {
	                inputStream.close(); // Close the InputStream
	            } catch (Exception e) { }
	        }
	    }
	}
	
	/**
	 * Get the sha1 value of the filepath specified file
	 * @param filePath The filepath of the file
	 * @return The sha1 value
	 */
	public String fileToSHA1(String filePath) {
	    InputStream inputStream = null;
	    try {
	        inputStream = new FileInputStream(filePath); // Create an FileInputStream instance according to the filepath
	        byte[] buffer = new byte[1024]; // The buffer to read the file
	        MessageDigest digest = MessageDigest.getInstance("SHA-1"); // Get a SHA-1 instance
	        int numRead = 0; // Record how many bytes have been read
	        while (numRead != -1) {
	            numRead = inputStream.read(buffer);
	            if (numRead > 0)
	                digest.update(buffer, 0, numRead); // Update the digest
	        }
	        byte [] sha1Bytes = digest.digest(); // Complete the hash computing
	        return convertHashToString(sha1Bytes); // Call the function to convert to hex digits
	    } catch (Exception e) {
	        return null;
	    } finally {
	        if (inputStream != null) {
	            try {
	                inputStream.close(); // Close the InputStream
	            } catch (Exception e) { }
	        }
	    }
	}

	/**
	 * Convert the hash bytes to hex digits string
	 * @param hashBytes
	 * @return The converted hex digits string
	 */
	private static String convertHashToString(byte[] hashBytes) {
		String returnVal = "";
		for (int i = 0; i < hashBytes.length; i++) {
			returnVal += Integer.toString((hashBytes[i] & 0x0ff) + 0x100, 16).substring(1);
		}
		return returnVal.toLowerCase();
	}
	
	/**
	 * Upload the file information to specified url.
	 * @param strUploadFileInfoUrl The url of the file information server.
	 * @param filepath The filepath of the local file.
	 * @param file_md5 The md5 value of the file.
	 * @param file_sha1 The sha1 value of the file.
	 * @param encrypt_level The encrypt level of the file.
	 * @param encrypt_key The encrypt key of the file.
	 */
	public boolean uploadFileInfo(String strUploadFileInfoUrl, String filepath, String user_id,
			String file_md5, String file_sha1, String encrypt_level, String encrypt_key){
		
		boolean status = false;	// The flag to indicate the return state
		
		HttpPost httpRequest =new HttpPost(strUploadFileInfoUrl); // Construct a new HttpPost instance according to the uri
		
		// use name-value pair to store the parameters to pass
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("user_id", user_id)); 			// Add the user_id name-value
		params.add(new BasicNameValuePair("file_md5", file_md5)); 			// Add the file_md5 name-value
		params.add(new BasicNameValuePair("file_sha1", file_sha1)); 		// Add the file_sha1 name-value
		params.add(new BasicNameValuePair("encrypt_level", encrypt_level)); 	// Add the encrypt_level name_value
		params.add(new BasicNameValuePair("encrypt_key", encrypt_key)); 	// Add the encrypt_key name-value
		
		Log.d(Tag, "params to send:" + params.toString());	// Log out the parameters
		
		try{
			
			// Encode the entity with utf8, and send the entity to the request
			httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8)); 
			
		} catch (UnsupportedEncodingException e){
			e.printStackTrace();
		}
		try{
			// Execute an HTTP request and ge the result
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest); // execute the http request
			
			// Response status is ok
			if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){	
			
				// Get the response string and parse it
				HttpEntity entity = httpResponse.getEntity(); // Obtain the HTTP response entity
				if (entity != null) { // The entity obtained is not null
					String info = EntityUtils.toString(entity); // Convert the entity to string
					
					Log.d(Tag, info); // Log out the returned info
					
					JSONObject jsonObject=null;
					// Flag to indicate whether login succeeded, others to store the data from server
					
					String flag = ""; 	// The flag to indicate the upload status.
					String file_id = ""; // The file id.
					String uid = "";		// The user id.
					String fmd5 = "";	// The md5 value of the file.
					String fsha1 = "";	// The sha1 value of the file.
					String e_level = "";	// The encrypt level of the file.
//					String e_key = "";	// The encrypt key of the file.
					String sessionid = "";	// The session id.
					try {
						jsonObject = new JSONObject(info); // Construct an JsonObject instance from the name-value Json string
						flag = jsonObject.getString("flag"); // Get the value mapped by name:flag							
						file_id = jsonObject.getString("file_id"); // Get the value mapped by name:file_id
						uid = jsonObject.getString("user_id");	// Get the value mapped by name:user_id.
						fmd5 = jsonObject.getString("file_md5");	// Get the value mapped by name:file_md5.
						fsha1 = jsonObject.getString("file_sha1");	// Get the value mapped by name:file_sha1.
						e_level = jsonObject.getString("encrypt_level");	// Get the value mapped by name:encrypt_level.
					//	e_key = jsonObject.getString("encrypt_key");	// Get the value mapped by name:encrypt_key
						sessionid = jsonObject.getString("sessionid"); // Get the value mapped by name:sessionid
					
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// Judge whether the validation if succeeded according to the flag
					if(flag.equals("insert")
							|| flag.equals("update")) { // If the operation type is insert or update, set status as true
						
						// Set values to record the file-related information 
						session.put("uid", uid);
						session.put("file_id", file_id);
						session.put("file_md5", fmd5);
						session.put("fsha1", fsha1);
						session.put("encryption_flag", e_level);
					//	session.put("encrypt_key", e_key);
						session.put("sessionid", sessionid);
						status = true;
					} else { // If the operation type is unknown or some other errors, set status as false
						status = false;
					}
				} else { // Entity is null
					status = false;
				}
			} // Status code equal ok 
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}
}
