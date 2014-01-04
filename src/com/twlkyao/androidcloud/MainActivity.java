/**
 * @Author:		Shiyao Qi
 * @Date:		2013.11.25
 * @Function:	Get the filepath according to the filename
 */
package com.twlkyao.androidcloud;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import com.twlkyao.utils.ConstantVariables;
import com.twlkyao.utils.FileDEncryption;
import com.twlkyao.utils.FileOperation;


import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
//	private TextView textviewFilename; // The filename textview
	private EditText keyword; // The filename edittext
	private Button btnSearch; // The search button
	private String strKeyword; // The filename string
	private String SDcard = Environment.getExternalStorageDirectory().getPath(); // Get the external storage directory
	
	private String upload_file_info_url = ConstantVariables.BASE_URL + ConstantVariables.UPLOAD_FILE_INFO_URL; // Set the file information url
	private String upload_file_url = ConstantVariables.BASE_URL + ConstantVariables.UPLOAD_FILE_URL; // Set the file upload url
	private String check_file_info_url = ConstantVariables.BASE_URL + ConstantVariables.CHECK_FILE_INFO_URL; // Set the check file info url.
//	private float rate; // To indicate the rating.
	private int user_id;	// To store the user id.
	private String Tag = "MainActivity"; // The logcat tag.
	
	private String encryptKey = ConstantVariables.keys[0]; // DEncrypt key.
	private String algorithm = ConstantVariables.algorithms[0]; // DEncrypt algorithm.
	
	private boolean DEncryptFlag; // DEncrypt flag.
	
	private ListView fileListView; // The listview to store the file information
	private ArrayList<File> filelist; // Used to store the filename
	private TextView search_result_label; // The search_result_label
	
	private FileListAdapter fileListAdapter; // The self defined Adapter
	private FileOperation fileOperation = new FileOperation(); // Construct an instance of FileOperation.
//	private FileDEncryption fileDEncryption = new FileDEncryption(); // Construct an instance of FileDEncryption.
	
	private ConstantVariables constantVariables = new ConstantVariables(); // Instance an ConstantVariables.
	
	// Deal with the time-consuming matters
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(constantVariables.operation_failed == msg.what) { // The operation is failed.
				if(constantVariables.upload_file_info == msg.arg1) { // It is the file information upload.
					Toast.makeText(getApplicationContext(),
							R.string.file_info_upload_fail, Toast.LENGTH_SHORT).show();
				} else if(constantVariables.upload_file == msg.arg1) { // It is the file upload.
					Toast.makeText(getApplicationContext(),
							R.string.file_upload_fail, Toast.LENGTH_SHORT).show();
				} else if(constantVariables.encrypt_file == msg.arg1) {
					Toast.makeText(getApplicationContext(),
							R.string.encrypt_fail, Toast.LENGTH_SHORT).show();
				} else if(constantVariables.decrypt_file == msg.arg1) {
					Toast.makeText(getApplicationContext(),
							R.string.decrypt_fail, Toast.LENGTH_SHORT).show();
				}
			} else if(constantVariables.operation_succeed == msg.what){ // The operation is succeeded.
				if(constantVariables.upload_file_info == msg.arg1) { // It is the file information upload.
					Toast.makeText(getApplicationContext(),
							R.string.file_info_upload_succeed, Toast.LENGTH_SHORT).show();
				} else if(constantVariables.upload_file == msg.arg1) {
					Toast.makeText(getApplicationContext(),
							R.string.file_upload_succeed, Toast.LENGTH_SHORT).show();
				} else if(constantVariables.encrypt_file == msg.arg1) {
					Toast.makeText(getApplicationContext(),
							R.string.encrypt_succeed, Toast.LENGTH_SHORT).show();
				} else if(constantVariables.decrypt_file == msg.arg1) {
					Toast.makeText(getApplicationContext(),
							R.string.decrypt_succeed, Toast.LENGTH_SHORT).show();
				}
			}
		}
	};	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViews(); // Find the views
		initData(Environment.getExternalStorageDirectory());
		
		Bundle bundle = getIntent().getExtras(); // The bundle object from intent
		user_id = Integer.parseInt(bundle.getString("uid")); // Get the user id to store into the database
		
		setListeners(); // Set the listeners
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Set the menu item selected operation.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()) {
		case R.id.action_settings:
			Toast.makeText(getApplicationContext(),
					R.string.action_settings, Toast.LENGTH_SHORT).show();
			break;
		case R.id.action_help:
			Toast.makeText(getApplicationContext(),
					R.string.help, Toast.LENGTH_SHORT).show();
			break;
			
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Find the views by id
	 */
	public void findViews() {
		keyword = (EditText) this.findViewById(R.id.keyword); // The filename textview
		btnSearch = (Button) this.findViewById(R.id.button_search); // The search button
		search_result_label= (TextView) this.findViewById(R.id.search_result_label); // The search result label
		fileListView = (ListView) this.findViewById(R.id.file_listview); // The listview to store file information
	}
	
	/**
	 * Set the search button listener
	 */
	public void setListeners() {
		
		// Set the btnSearch listener, start to search file.
		btnSearch.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				search_result_label.setVisibility(View.VISIBLE); // Set the search_result_label visible
				
				strKeyword = keyword.getText().toString();
				if(strKeyword != null && !strKeyword.trim().equals("")) { // Judge if the edittext is null or empty
					
					filelist = fileOperation.findFileByName(strKeyword, SDcard);
					
					if(filelist.isEmpty()) { // The search result is null
					
					
					Toast.makeText(getApplicationContext(),
							getString(R.string.result_null),
								Toast.LENGTH_SHORT).show();
					} else { // Add the search result into the listview
						fileListAdapter = new FileListAdapter(getApplicationContext(), filelist,
						SDcard.equals(Environment.getExternalStorageDirectory().toString()));
						fileListView.setAdapter(fileListAdapter);
					} 
				} else { // The input keyword is null
					Toast.makeText(getApplicationContext(),
							getString(R.string.keyword_null),
								Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		/**
		 * Set the fileListView click listener, chose the encrypt_level,
		 * encrypt the file, upload file information, upload file.
		 */
		fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, 
					int position, long id) {
				// TODO Auto-generated method stub
				
//				Log.d(Tag, "onclick");
				
				final File file = (File) fileListAdapter.getItem(position);
//				final String filepath = file.getPath();
				
				if(!file.canRead()) { // If the file can't read, alert
					Log.w(Tag, "Can't read!");
				} else if(file.isDirectory()) { // If the clicked item is a directory, get into it
					initData(file);
				} else { // If the clicked item is a file, get the file information, such as md5 or sha1
					
					Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setIcon(android.R.drawable.ic_dialog_info);
					builder.setTitle(R.string.encrypt_level_title);
					builder.setItems(R.array.encrypt_level, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, final int which) {
							// TODO Auto-generated method stub
							
							// Create the directory to store the encrypted file.
							final File dir1 = new File(Environment.getExternalStorageDirectory().toString()
									+ File.separator + "ABB"); // Create a new directory to store the encrypted file.
							if(!dir1.exists()) { // If the directory is not exist, create it.
								dir1.mkdirs();
							}
							
							final Message msg = Message.obtain();
							Thread thread = new Thread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									
									msg.arg1 = constantVariables.encrypt_file; // Indicate this is the upload file type.
									// First encrypt the file and then upload the info of encrypted file and the file.
									if(startEncrypt(file.getPath(), which, ConstantVariables.keys[which],
											dir1 + File.separator + file.getName(), upload_file_info_url)) { // Encrypt the file successfully.
										
										msg.what = constantVariables.operation_succeed;
									} else {
										msg.what = constantVariables.operation_failed;
									}
									
									handler.sendMessage(msg);
								}
							});
							
							thread.start(); // Start the thread.
						}
					});
					
					AlertDialog alertDialog = builder.create(); // Create the dialog.
					alertDialog.show(); // Show the dialog.
				}
			}
		});
		
		// Set the fileListView long click listener, decrypt the file.
		fileListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, 
					int position, long id) {
				// TODO Auto-generated method stub
				
//				Log.d(Tag, "onLongClick");
				
				final File file = (File) fileListAdapter.getItem(position);
				
				final File dir2 = new File(Environment.getExternalStorageDirectory().toString()
						+ File.separator + "ACC"); // Create a new directory to store the encrypted file.
				if(!dir2.exists()) { // If the directory is not exist, create it.
					dir2.mkdirs();
				}
				
				final Message msg = Message.obtain();
				Thread thread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						msg.arg1 = constantVariables.decrypt_file; // Set the operation flag.
						/**
						 * First get the file information from the server.
						 * If the file is not in the server, stop;
						 * if the file is in the server, then decrypt it.
						 */
						// Call the startDecryption function to decrypt the encrypted file.
						if(startDecrypt(file.getPath(), dir2 + File.separator + file.getName())) {
							msg.what = constantVariables.operation_succeed;
						} else {
							msg.what = constantVariables.operation_failed;
						}
						
						handler.sendMessage(msg);
					}
				});
				
				thread.start();
				
				return true; // Set true in order not to trigger the onItemClickListener.
			}
			
		});
	}
	
	/**
	 * Create the file and upload the file information to remote server,
	 * upload the file to remote server.
	 * @param srcFilePath The file path of the file to be encrypted.
	 * @param encrypt_level The encrypt level.
	 * @param encrypt_key The encrypt key.
	 * @param destFilePath The file path of the encrypted file to store.
	 * @param uploadFileInfoUrl The url of the file information to upload to.
	 * @param uploadFileUrl The url of the file to upload to.
	 * @return True, if encryption is succeeded.
	 */
	public boolean startEncrypt(String srcFilePath,
			int encrypt_level, String encrypt_key,
			String destFilePath, String uploadFileInfoUrl) {
		
		boolean flag = false; // Initial the flag to be false.
		File sd = Environment.getExternalStorageDirectory(); // Get the primary external storage directory.
		boolean can_write = sd.canWrite(); // Indicates whether the current context is allowed to write to this file on SDCard.
		if(!can_write) { // The SDCard is not allowed to write.
			Log.d("startEncrypt", "SDCard can't read" + Environment.getExternalStorageState());
//			Toast.makeText(MainActivity.this, R.string.can_not_read_sdcard, Toast.LENGTH_SHORT).show();
		}
		
		FileDEncryption fileDEncryption = new FileDEncryption();
		
		if(fileDEncryption.Encryption(srcFilePath, ConstantVariables.algorithms[encrypt_level],
				ConstantVariables.keys[encrypt_level], destFilePath)) { // File encryption is succeeded.
			
			String md5 = fileOperation.fileToMD5(destFilePath);	// Get the md5 value of the file
			String sha1 = fileOperation.fileToSHA1(destFilePath);	// Get the sha1 value of the file
			
			Log.d("md5 and sha1", "md5:" + md5 + "\nsha1:" + sha1);			// Log out the md5 and sha1 value of the file
			
			// Call the startUploadFileInfo function to upload file information.
			if(fileOperation.uploadFileInfo(uploadFileInfoUrl,
					String.valueOf(user_id), md5, sha1,
					String.valueOf(encrypt_level), encrypt_key)) {
				
				flag = true;
				
				/**
				 * This shoud call a third party application to do this stuff.
				 */
//				if(uploadFile(uploadFileUrl, destFilePath)) {
//					flag = true;
//				}
				
				// Call the upload app to do the stuff.
			}
		}
		
		return flag;
	}
	
	/**
	 * Decrypt the file if the file is not broken.
	 * @param srcFilepath The file path of the file to be decrypted.
	 * @param algorithm The decrypt algorithm.
	 * @param encKey he encrypt key.
	 * @param destFilepath he file path of the decrypted file to store.
	 * @return True, if the decryption is succeeded.
	 */
	public boolean startDecrypt(final String srcFilePath, final String destFilePath) {
		
		boolean flag = false; // Initial the flag to be false.
		final FileDEncryption fileDEncryption = new FileDEncryption();
				
		// First checksum the file
		
		String md5 = fileOperation.fileToMD5(srcFilePath);
		String sha1 = fileOperation.fileToSHA1(srcFilePath);
		
		HashMap<String, String> resultHashMap;
		
		/**
		 * Get the checkFileInfo result.
		 * The result include <"encrypt_level", encrypt_level>, <"encrypt_key", encrypt_key>
		 */
		resultHashMap = fileOperation.checkFileInfo(check_file_info_url,
			String.valueOf(user_id), md5, sha1);
				
		// Take different measures according to the status of checkFileInfo.
		if(resultHashMap != null) { // The md5 and sha1 values are the same with the server.
			int encrypt_level = Integer.parseInt(resultHashMap.get("encrypt_level"));
			String encrypt_key = resultHashMap.get("encrypt_key");
			if(fileDEncryption.Decryption(srcFilePath,
				ConstantVariables.algorithms[encrypt_level], encrypt_key, destFilePath)) { // The decryption is succeeded.
				flag = true; // Set the DEncryptFlag to false.
			}
		}
		
		return flag;
	}
	
	/**
	 * Create a thread to upload the file.
	 * @param filepath
	 * @param uploadUrl
	 */
	public void startUploadFile(final String uploadFileUrl, final String filepath) {
		
		final Message msg = Message.obtain(); // Get the Message object
		// Create a new thread to do the upload
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				msg.arg1 = constantVariables.upload_file; // Indicate this is the upload file type.
				boolean flag = uploadFile(uploadFileUrl, filepath); // Call the upload file function
				if(flag) {
					msg.what = constantVariables.operation_succeed; // Upload file succeeded.
					
				} else {
					msg.what = constantVariables.operation_failed; // Upload file failed.
				}
				handler.sendMessage(msg);
			}
		});
		
		thread.start(); // Start the thread
			
	}
	
	/**
	 * Upload the specified file to remote server.
	 * @param uploadUrl The server url.
	 * @param filepath The path of the local file.
	 * @return The upload status.
	 */
//	public boolean uploadFile( String uploadUrl, String filepath, String uid, String encode) {
	public boolean uploadFile( String uploadUrl, String filepath) {
		boolean status = true;
		
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "******";
		try
		{
			URL url = new URL(uploadUrl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

			// Set the size of the transfer stream, in case that the application
			// collapses due to small memory, this method is used when we don't
			// know the size of the content, we use HTTP request without cache
			httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K  
			
			// Set the input and output
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			
			// Set the HTTP method
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			// Get outputstream according to the url connection
			DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
			
			// Write the HTTP POST header
			dos.writeBytes(twoHyphens + boundary + end);
			
			// Convert the encode type
			String filename = filepath.substring(filepath.lastIndexOf("/") + 1);
			
//			filename = filename.getBytes(encode).toString();
			
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""
					+ filename + "\"" + end);
            dos.writeBytes(end);
        
            FileInputStream fis = new FileInputStream(filepath);
            
            int bufferSize = 8 * 1024; // The size of the buffer, 8KB.
            byte[] buffer = new byte[bufferSize];
            int length = 0;

            while ((length = fis.read(buffer)) != -1) {
            	
            	// Write data to DataOutputStream
            	dos.write(buffer, 0, length);
            }
            
            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            
            fis.close(); // Close the FileInputStream.
            dos.flush(); // Flush the data to DataOutputStream.
        
            // Get the content of the response
            InputStream is = httpURLConnection.getInputStream();
            
            InputStreamReader isr = new InputStreamReader(is, "utf-8");  
            BufferedReader br = new BufferedReader(isr, 8 * 1024); // Set to 8KB to get better performance.
            String result = br.readLine();
        
            Log.d(Tag + "UploadFile", result);
            
//          dos.close(); // Will respond I/O exception if closes.
            fis.close(); // Close the FileInputStream.
            
          } catch (Exception e) {
        	  e.printStackTrace();
        	  status = true;
          }
		return status;
	}

	
	/**
	 * Update the fileListView and sort the files according to their name.
	 * @param folder The new folder path to display
	 */
	private void initData(File folder) {
		boolean isSDcard = folder.equals(Environment.getExternalStorageDirectory()); // Judge is the folder is the SDcard
		ArrayList<File> files = new ArrayList<File>();   
		if (!isSDcard) { // If the current folder is not the SDcard
			files.add(Environment.getExternalStorageDirectory()); // Back to the SDcard
			files.add(folder.getParentFile()); // Back to parent
		}
		File[] filterFiles = folder.listFiles(); // Get the file list under the specified folder
		if (null != filterFiles && filterFiles.length > 0) {
			for (File file : filterFiles) { // Add the files to the ArrayList
				files.add(file);
			}
		}
		Collections.sort(files); // Sort the files.
		
		fileListAdapter= new FileListAdapter(getApplicationContext(), files, isSDcard); // Update the fileListAdapter
		fileListView.setAdapter(fileListAdapter); // Update the fileListView's adapter
	}


}