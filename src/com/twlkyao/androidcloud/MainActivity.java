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

import com.twlkyao.utils.ConstantVariables;
import com.twlkyao.utils.FileOperation;


import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
//	private TextView textviewFilename; // The filename textview
	private EditText keyword; // The filename edittext
	private Button btnSearch; // The search button
	private String strKeyword; // The filename string
	private String SDcard = Environment.getExternalStorageDirectory().getPath(); // Get the external storage directory
	
	private String file_info_url = ConstantVariables.BASE_URL + ConstantVariables.FILE_INFO_UPLOAD_URL; // Set the file information url
	private String file_upload_url = ConstantVariables.BASE_URL + ConstantVariables.FILE_UPLOAD; // Set the file upload url
	private float rating; // To indicate the rating.
	private int user_id;	// To store the user id.
	private String Tag = "MainActivity"; // The logcat tag
	
	private ListView fileListView; // The listview to stotre the file information
	private ArrayList<File> filelist; // Used to store the filename
	private TextView search_result_label; // The search_result_label
	
	private FileListAdapter fileListAdapter; // The self defined Adapter
	private FileOperation fileOperation = new FileOperation(); // Construct an instance of FileOperation
	
	private ConstantVariables constantVariables = new ConstantVariables(); // Instance an ConstantVariables.
	
	// Deal with the time-consuming matters
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(constantVariables.operation_failed == msg.what) { // The operation is failed.
				if(constantVariables.file_info_upload == msg.arg1) { // It is the file information upload.
					Toast.makeText(getApplicationContext(),
							R.string.file_info_upload_fail, Toast.LENGTH_SHORT).show();
				} else if(constantVariables.file_upload == msg.arg1) { // It is the file upload.
					Toast.makeText(getApplicationContext(),
							R.string.file_upload_fail, Toast.LENGTH_SHORT).show();
				}
			} else if(constantVariables.operation_succeed == msg.what){ // The operation is succeeded.
				if(constantVariables.file_info_upload == msg.arg1) { // It is the file information upload.
					Toast.makeText(getApplicationContext(),
							R.string.file_info_upload_succeed, Toast.LENGTH_SHORT).show();
				} else if(constantVariables.file_upload == msg.arg1) {
					Toast.makeText(getApplicationContext(),
							R.string.file_upload_succeed, Toast.LENGTH_SHORT).show();
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
		
		// Set the btnSearch listener
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
		
		// Set the fileListView listener
		fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, 
					int position, long id) {
				// TODO Auto-generated method stub
				
				Log.d(Tag, "onclick");
				
				final File file = (File) fileListAdapter.getItem(position);
				if(!file.canRead()) { // If the file can't read, alert
					Log.w(Tag, "Can't read!");
				} else if(file.isDirectory()) { // If the clicked item is a directory, get into it
					initData(file);
				} else { // If the clicked item is a file, get the file information, such as md5 or sha1
					
					// Set a ProgressDialog
					/*ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
					progressDialog.setTitle(R.string.upload_file_info); // Set the title of the ProgressDialog.
					progressDialog.setMessage(getString(R.string.wait)); // Set the message of the ProgressDialog.
					progressDialog.setIcon(android.R.id.progress); // Set the icon of the ProgressDialog.
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Set the style of the ProgressDialog
					progressDialog.show();*/
					
					final String md5 = fileOperation.fileToMD5(file.getPath());	// Get the md5 value of the file
					final String sha1 = fileOperation.fileToSHA1(file.getPath());	// Get the sha1 value of the file
					
//					progressDialog.dismiss();
					
					Log.d(Tag, "md5:" + md5 + "\nsha1:" + sha1);			// Log out the md5 and sha1 value of the file
					
					final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					
					/**
					 * The setting here is not work
					 */
					final RatingBar ratingBar = new RatingBar(MainActivity.this);
					ratingBar.setRating(0);
					ratingBar.setStepSize(1);
					ratingBar.setNumStars(2);

					builder.setTitle(R.string.alertdialog_title)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setView(ratingBar)
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							rating = ratingBar.getRating();
							// Call the fileOperation to upload the file info
							fileInfo(file_info_url, file.getPath(),
									String.valueOf(user_id), md5, sha1, rating + "", "123456");
						}
					})
					.setNegativeButton("取消", null);
					
					//Create the AlertDialog  
					AlertDialog alertDialog = builder.create();  
					//Show the AlertDialog 
					alertDialog.show();
					
					
				}
			}
		});
		
		fileListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, 
					int position, long id) {
				// TODO Auto-generated method stub
				
				Log.d(Tag, "onlongclick");
				
				File file = (File) fileListAdapter.getItem(position);
				startUploadFile(file.getPath(), file_upload_url);
				
				return true; // Set true in order not to trigger the onItemClickListener.
			}
			
		});
	}
	
	/**
	 * Create an new thread to upload the file information.
	 * @param url The url of the server script.
	 * @param filepath The filepath of the file on Android.
	 * @param uid The user id.
	 * @param md5 The md5 value of the file.
	 * @param sha1 The sha1 value of the file.
	 * @param encrypt_level The encrypt level of the file.
	 * @param encrypt_key The password
	 */
	public void fileInfo(final String url, final String filepath, final String uid,
			final String md5, final String sha1, final String encrypt_level, final String encrypt_key) {
		
		final Message msg = Message.obtain(); // Get the Message object
		new Thread(){
			public void run() {
				boolean flag = fileOperation.uploadFileInfo(url, filepath, uid,
						md5, sha1, encrypt_level, encrypt_key); // Call the uploadFileInfo function to 
																// upload the file information to the Secure Cloud
				msg.arg1 = constantVariables.file_info_upload; // Indicate this is the file information upload type.
				if(flag) { // The upload file information function succeed
					msg.what = constantVariables.operation_succeed;
					
				} else { // The upload file information function failed
					msg.what = constantVariables.operation_failed;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	

	/**
	 * Create a thread to upload the file.
	 * @param filepath
	 * @param uploadUrl
	 */
	public void startUploadFile(final String filepath, final String uploadUrl) {
		
		final Message msg = Message.obtain(); // Get the Message object
		// Create a new thread to do the upload
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				msg.arg1 = constantVariables.file_upload; // Indicate this is the upload file type.
				boolean flag = uploadFile(filepath, uploadUrl, constantVariables.charset); // Call the upload file function
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
	 * @param filepath The path of the local file.
	 * @param uploadUrl The server url.
	 * @param encode The encode type.
	 * @return The upload status.
	 */
	public boolean uploadFile(String filepath, String uploadUrl, String encode) {
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
            BufferedReader br = new BufferedReader(isr, 8 * 1024);  
            String result = br.readLine();
        
            Log.d(Tag, result);
            
//            dos.close(); // Will respond I/O exception if closes.
          } catch (Exception e) {
        	  e.printStackTrace();
        	  status = true;
          }
		return status;
	}

	
	/**
	 * Update the fileListView
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
		fileListAdapter= new FileListAdapter(getApplicationContext(), files, isSDcard); // Update the fileListAdapter
		fileListView.setAdapter(fileListAdapter); // Update the fileListView's adapter
	}


}