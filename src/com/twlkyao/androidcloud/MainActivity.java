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
import java.util.HashMap;
import java.util.List;

import com.twlkyao.dao.DaoMaster;
import com.twlkyao.dao.DaoSession;
import com.twlkyao.dao.FileInfo;
import com.twlkyao.dao.FileInfoDao;
import com.twlkyao.dao.DaoMaster.DevOpenHelper;
import com.twlkyao.dao.FileInfoDao.Properties;
import com.twlkyao.utils.ConstantVariables;
import com.twlkyao.utils.FileDEncryption;
import com.twlkyao.utils.FileOperation;
import com.twlkyao.utils.LogUtils;
import com.twlkyao.utils.StringSplice;

import de.greenrobot.dao.query.QueryBuilder;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private String TAG = "MainActivity";
	private boolean DEBUG = true;
	private LogUtils logUtils = new LogUtils(DEBUG, TAG);
	
	
//	private TextView textviewFilename; // The filename textview
	private EditText keyword; // The filename edittext
	private Button btnSearch; // The search button
	private String strKeyword; // The filename string
	private String SDcard = Environment.getExternalStorageDirectory().getPath(); // Get the external storage directory
	
	private ConstantVariables constantVariables = new ConstantVariables(); // Instance an ConstantVariables.
	private String upload_file_info_url = ConstantVariables.BASE_URL + ConstantVariables.UPLOAD_FILE_INFO_URL; // Set the file information url
//	private String upload_file_url = ConstantVariables.BASE_URL + ConstantVariables.UPLOAD_FILE_URL; // Set the file upload url
	private String check_file_info_url = ConstantVariables.BASE_URL + ConstantVariables.CHECK_FILE_INFO_URL; // Set the check file info url.
//	private float rate; // To indicate the rating.
	private int user_id;	// To store the user id.
	
//	private String encryptKey = ConstantVariables.keys[0]; // DEncrypt key.
//	private String algorithm = ConstantVariables.algorithms[0]; // DEncrypt algorithm.
	
	private ListView fileListView; // The listview to store the file information
	private ArrayList<File> filelist; // Used to store the filename
	private TextView search_result_label; // The search_result_label
	
	private FileListAdapter fileListAdapter; // The self defined Adapter
	private FileOperation fileOperation = new FileOperation(); // Construct an instance of FileOperation.
	
	private String md5SetString; // To record the md5 of the click file when set encrypt level.
	private String sha1SetString; // To recored the sha1 of the click file when set encrypt level.
	
	private final int set_level = 1;
	private final int upload = 2;
	
	public static String FILEPATH = "";
	
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
				} else if(constantVariables.encrypt_file == msg.arg1) { // It is the file encryption operation.
					Toast.makeText(getApplicationContext(),
							R.string.encrypt_succeed, Toast.LENGTH_SHORT).show();
					
					// Call the cloud Apps to upload file.
					Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setIcon(android.R.drawable.ic_dialog_info);
					builder.setTitle(R.string.apps_choice);
					builder.setItems(R.array.apps_choice, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							if(which < 5) {
								ComponentName componentName = new ComponentName(
								constantVariables.packageNames[which],
								constantVariables.classNames[which]);
								Intent intent = new Intent();
								intent.setComponent(componentName);
								startActivity(intent);
							}
							
							if(5 == which) { // Third party sdk.
								// Change onClick to jump to kuaipan.
								Intent intent = new Intent(MainActivity.this, KuaipanDiskActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString("filePath", FILEPATH);
								intent.putExtras(bundle); // Put the bundle into the intent.
								startActivityForResult(intent, upload); // Start the KuaipanActivity for result.
							}
						}
						
					});
					
					AlertDialog alertDialog = builder.create(); // Create the dialog.
					alertDialog.show(); // Show the dialog.
					
					
					/*// Call the third party net disks via api.
					Intent intent = new Intent(MainActivity.this, ChooseDiskActivity.class);
					startActivity(intent);*/
					
				} else if(constantVariables.decrypt_file == msg.arg1) { // Decrypt the files.
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

		Intent intent = new Intent(MainActivity.this, ObserverService.class);
		startService(intent);
		
		findViews(); // Find the views.
		initData(Environment.getExternalStorageDirectory());
		
		Bundle bundle = getIntent().getExtras(); // The bundle object from intent.
		user_id = Integer.parseInt(bundle.getString("uid")); // Get the user id to store into the database
		
		setListeners(); // Set the listeners
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		logUtils.d(TAG, "onPause");
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		logUtils.d(TAG, "onRestart");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		initData(Environment.getExternalStorageDirectory()); // Refresh the listview.
		logUtils.d(TAG, "onResume");
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		logUtils.d(TAG, "onStart");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		logUtils.d(TAG, "onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		logUtils.d(TAG, "onDestroy");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Make the Activity run in background rather than exit.
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * Set the menu item selected operation.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()) { // Act according to the menu item clicked.
		
		// The switch-case need to be reorganized.
			case R.id.action_set_base_keys: // The action_set_base_keys item.
				
				// Judge whether the base_key is already exist.
				SharedPreferences sp = getSharedPreferences(constantVariables.PREF_NAME, MODE_PRIVATE); // Get the SharedPreferences.
				String base_key = sp.getString(constantVariables.PREF_KEY, ""); // Get the string value.
				
				logUtils.d(TAG, "Base key:" + base_key);
				
				// Only show the set base key dialog when there is no base key.
				if(base_key.equals("")) { 
					final Dialog set_base_key_dialog = new Dialog(MainActivity.this, R.style.AppThemeDialog);
					set_base_key_dialog.setContentView(R.layout.set_base_key_dialog);
					set_base_key_dialog.setCancelable(true);

					// Find the submit button.
					Button btn_submit = (Button) set_base_key_dialog.findViewById(R.id.btn_submit);
					
					// Find the edit text.
					final EditText editText = (EditText) set_base_key_dialog.findViewById(R.id.edittext_base_key);
					final EditText editTextConfirm = (EditText) set_base_key_dialog.findViewById(R.id.edittext_base_key_confirm);
					
					btn_submit.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// Store the base key in to SharedPreferences.
							String base_key = editText.getText().toString();
							String base_key_confirm = editTextConfirm.getText().toString();
							
							if(!base_key.equals("") && !base_key_confirm.equals("") && base_key.equals(base_key_confirm)) {
								// Get the SharedPreferences object, the SharedPreferences can only accessed by the calling application.
								SharedPreferences sp = getSharedPreferences(constantVariables.PREF_NAME,
										Context.MODE_PRIVATE);
								
								// Create a new Editor for SharedPreferences.
								SharedPreferences.Editor editor = sp.edit(); 
								
								// Set data.
								editor.putString(constantVariables.PREF_KEY, base_key);
								
								// Call the commit method to save changes.
								editor.commit();
								Toast.makeText(MainActivity.this,
										R.string.base_key_success, Toast.LENGTH_SHORT).show();
								set_base_key_dialog.dismiss();
							} else {
								editText.setText("");
								editTextConfirm.setText("");
								Toast.makeText(MainActivity.this,
										R.string.base_key_conflict, Toast.LENGTH_SHORT).show();
							}
						}
					});
					
					Button btn_cancel= (Button) set_base_key_dialog.findViewById(R.id.btn_cancel);
					btn_cancel.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							set_base_key_dialog.dismiss();
						}
					});
					//now that the dialog is set up, it's time to show it    
					set_base_key_dialog.show(); 
				} else { // Show the alert dialog when the base key is already exist.
					Dialog alertDialog = new AlertDialog.Builder(MainActivity.this)
					.setTitle(getString(R.string.base_key_alert_title))
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton(R.string.btn_ok, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.dismiss();
						}
					})
					.create();
					alertDialog.show(); 
				}
				
				break;
			case R.id.action_show_contacts: // The show contacts item.
				Intent intent_contacts = new Intent(getApplicationContext(), ContactsActivity.class);
				startActivity(intent_contacts);				
				break;
			case R.id.action_show_messages: // The show message item.
				Intent intent_messages = new Intent(getApplicationContext(), MessageActivity.class);
				startActivity(intent_messages);
				break;
			case R.id.action_help: // The help item.
				Toast.makeText(getApplicationContext(),
						R.string.help, Toast.LENGTH_LONG).show();
				break;
			case R.id.exit: // The exit item.
				android.os.Process.killProcess(android.os.Process.myPid()); // Get the pid of the process.
				System.exit(0); // Cause the VM to stop running and the program exit with the code, 0 represents for the normal exition.
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
				
				final File file = (File) fileListAdapter.getItem(position);
//				final String filepath = file.getPath();
				
				if(!file.canRead()) { // If the file can't read, alert
					logUtils.w(TAG, "Can't read!");
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
									+ File.separator + getString(R.string.encrypt_directory)); // Create a new directory to store the encrypted file.
							if(!dir1.exists()) { // If the directory is not exist, create it.
								dir1.mkdirs();
							}
							
							final Message msg = Message.obtain();
							
							Thread thread = new Thread(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									
									msg.arg1 = constantVariables.encrypt_file; // Indicate this is the encrypt file type.
									
									if(0 == which) { // There is no need to encrypt the files.
										
										/*// Move the src file to dest file path
										InputStream is = new FileInputStream(file.getPath());
										
										File destFile = new File(dir1 + File.separator + file.getName()); // Create a new File instance.
										if(!destFile.exists()) { // Only create a new file, if the file does not exists.
											try {
												destFile.createNewFile(); // Create a new, empty file.
											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}*/
										
										FILEPATH = file.getPath(); // Record the file path of the clicked item.
										msg.what = constantVariables.operation_succeed;
									} else if(which < 5){ // Choose algorithms and keys to encrypt the files.
										// Get the remote generated encrypt key.
										String remote_encrypt_key = fileOperation.retrieveEncryptKey(
												ConstantVariables.BASE_URL + ConstantVariables.RETRIEVE_ENCRYPT_KEY,
												which);
									
										logUtils.d(TAG, "Encrypt Key:" + remote_encrypt_key);
									
										// Get the base key from SharedPreferences.
										SharedPreferences sp = getSharedPreferences(constantVariables.PREF_NAME, MODE_PRIVATE); // Get the SharedPreferences.
										String base_key = sp.getString(constantVariables.PREF_KEY, ""); // Get the string value.
									
										/**
										 * Currently the base_key is not used.
										 */
										if(!remote_encrypt_key.equals("")) { // The retrieved encrypt key is not null.
										
											// First encrypt the file and then upload the info of encrypted file and the file.
											if(startEncrypt(file.getPath(), which, remote_encrypt_key, base_key,
												dir1 + File.separator + file.getName(), upload_file_info_url)) { // Encrypt the file successfully.
												FILEPATH = dir1 + File.separator + file.getName(); // Record the file path of the encrypted file.
												msg.what = constantVariables.operation_succeed;
											} else { // The encryption is failed.
												msg.what = constantVariables.operation_failed;
											}
										} else { // The remote key is not passed correctly.
											msg.what = constantVariables.operation_failed;
										}
									} else if(5 == which) {
										FileOperation fileOperation = new FileOperation();
						            	String md5String = fileOperation.fileToMD5(file.getPath());
						            	String sha1String = fileOperation.fileToSHA1(file.getPath());
						            	
						            	DevOpenHelper helper = new DaoMaster.DevOpenHelper(MainActivity.this,
						            			"encrypt_level_db", null);
						            	SQLiteDatabase db = helper.getWritableDatabase();
						            	DaoMaster daoMaster = new DaoMaster(db);
						            	DaoSession daoSession = daoMaster.newSession();
						                FileInfoDao fileInfoDao = daoSession.getFileInfoDao();
						            	
						                QueryBuilder<FileInfo> qb = fileInfoDao.queryBuilder();
						                qb.where(qb.and(Properties.Sha1.eq(sha1String),
						                		Properties.Md5.eq(md5String)));
						                List<FileInfo> fileInfoList = qb.list();
						                int size = fileInfoList.size();
						                if(0 == size) { // No default encrypt level.
						                	Looper.prepare();
						                	Toast.makeText(MainActivity.this,
						                			R.string.no_default_level,
						                			Toast.LENGTH_SHORT).show();
						                	Looper.loop();
						                } else {
						                	String encryptLevelString = fileInfoList.get(0).getLevel(); // Get the encrypt level string.
						                	int level = Integer.valueOf(encryptLevelString); // Convert into int.
						                	
											if(1 == level) { // There is no need to encrypt the files.
												FILEPATH = file.getPath(); // Record the file path of the clicked item.
												msg.what = constantVariables.operation_succeed; 
											} else {
						                		// Get the remote generated encrypt key.
												String remote_encrypt_key = fileOperation.retrieveEncryptKey(
														ConstantVariables.BASE_URL + ConstantVariables.RETRIEVE_ENCRYPT_KEY,
														level - 1);
											
												logUtils.d(TAG, "Encrypt Key:" + remote_encrypt_key);
											
												// Get the base key from SharedPreferences.
												SharedPreferences sp = getSharedPreferences(constantVariables.PREF_NAME, MODE_PRIVATE); // Get the SharedPreferences.
												String base_key = sp.getString(constantVariables.PREF_KEY, ""); // Get the string value.
											
												/**
												 * Currently the base_key is not used.
												 */
												if(!remote_encrypt_key.equals("")) { // The retrieved encrypt key is not null.
												
													// First encrypt the file and then upload the info of encrypted file and the file.
													if(startEncrypt(file.getPath(), level - 1, remote_encrypt_key, base_key,
														dir1 + File.separator + file.getName(), upload_file_info_url)) { // Encrypt the file successfully.
														FILEPATH = dir1 + File.separator + file.getName(); // Record the file path of the encrypted file.
														msg.what = constantVariables.operation_succeed;
													} else { // The encryption is failed.
														msg.what = constantVariables.operation_failed;
													}
												} else { // The remote key is not passed correctly.
													msg.what = constantVariables.operation_failed;
												}
						                	}
 						                	
						                }
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
		
		/*// Set the fileListView long click listener, decrypt the file.
		fileListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, 
					int position, long id) {
				// TODO Auto-generated method stub
				
				
				final File file = (File) fileListAdapter.getItem(position);
				
				final File dir2 = new File(Environment.getExternalStorageDirectory().toString()
						+ File.separator + getString(R.string.decrypt_directory)); // Create a new directory to store the encrypted file.
				if(!dir2.exists()) { // If the directory is not exist, create it.
					dir2.mkdirs();
				}
				
				final Message msg = Message.obtain();
				Thread thread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						msg.arg1 = constantVariables.decrypt_file; // Set the operation flag.
						*//**
						 * First get the file information from the server.
						 * If the file is not in the server, stop;
						 * if the file is in the server, then decrypt it.
						 *//*
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
			
		});*/
		
		fileListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				// TODO Auto-generated method stub
				MenuInflater inflater = getMenuInflater();  
				inflater.inflate(R.menu.listview_longclick_menu, menu);
				AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
				int position = info.position;
				logUtils.d(TAG + "Clicked", position + "");
			}
		});
	}

	/**
	 * The implementation of the context item selected.
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final File file = (File) fileListAdapter.getItem((int)info.id);
		
		switch(item.getItemId()){ 
			case R.id.set_level:
				
				md5SetString = fileOperation.fileToMD5(file.getPath());
				sha1SetString = fileOperation.fileToSHA1(file.getPath());
				
				Intent intent = new Intent(MainActivity.this, SetEncryptLevelActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("md5", md5SetString);
				bundle.putString("sha1", sha1SetString);
				bundle.putString("fileParentPath", file.getParentFile().getPath()); // Add the file's parent path to the bundle, in order to refersh the listview.
				
				logUtils.d(TAG, "md5:" + md5SetString + "sha1:" + sha1SetString);
				
				intent.putExtras(bundle);
				startActivityForResult(intent, set_level);
				break;
			case R.id.decrypt:
				final File dir2 = new File(Environment.getExternalStorageDirectory().toString()
						+ File.separator + getString(R.string.decrypt_directory)); // Create a new directory to store the encrypted file.
				if(!dir2.exists()) { // If the directory is not exist, create it.
					dir2.mkdirs();
				}
				
				final Message msg = Message.obtain();
				Thread thread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						
						msg.arg1 = constantVariables.decrypt_file; // Set the operation flag.
						// First get the file information from the server.
						// If the file is not in the server, stop;
						// if the file is in the server, then decrypt it.
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
				break;
		} 
		return super.onContextItemSelected(item);
	}
	
	/**
	 * Get the data from SharedPreferences.
	 * @param sharedPreferencesName The name of the SharedPreferences.
	 * @param sharedPreferencesKey The key of the SharedPreferences.
	 * @return The retrieved data.
	 */
	public String getBaseKey(String sharedPreferencesName, String sharedPreferencesKey) {
		String base_key = "";
		
		// Get the SharedPreferences object.
		SharedPreferences sp = getSharedPreferences(sharedPreferencesName,
				Context.MODE_PRIVATE);
		
		// Get the data.
		base_key = sp.getString(sharedPreferencesKey, "");
		return base_key;
	}

	/**
	 * Create the file and upload the file information to remote server,
	 * upload the file to remote server.
	 * @param srcFilePath The file path of the file to be encrypted.
	 * @param encrypt_level The encrypt level.
	 * @param remote_key The remote encrypt key.
	 * @param base_key The base encrypt key.
	 * @param destFilePath The file path of the encrypted file to store.
	 * @param uploadFileInfoUrl The url of the file information to upload to.
	 * @param uploadFileUrl The url of the file to upload to.
	 * @return True, if encryption is succeeded.
	 */
	public boolean startEncrypt(String srcFilePath,
			int encrypt_level, String remote_key,
			String base_key, String destFilePath, String uploadFileInfoUrl) {
		
		boolean flag = false; // Initial the flag to be false.
		File sd = Environment.getExternalStorageDirectory(); // Get the primary external storage directory.
		boolean can_write = sd.canWrite(); // Indicates whether the current context is allowed to write to this file on SDCard.
		if(!can_write) { // The SDCard is not allowed to write.
			logUtils.d("startEncrypt", "SDCard can't write!" + Environment.getExternalStorageState());
		}
		
		FileDEncryption fileDEncryption = new FileDEncryption();
		
		String encrypt_key = StringSplice.stringSplice(remote_key,
				base_key); // Splice the remote_encrypt_key and base_key.
		
		if(fileDEncryption.Encryption(srcFilePath, constantVariables.algorithms[encrypt_level - 1], // Minus 1 for the reason that the level 0 is not encrypted.
				encrypt_key, destFilePath)) { // File encryption is succeeded.
			
			String md5 = fileOperation.fileToMD5(destFilePath);	// Get the md5 value of the file
			String sha1 = fileOperation.fileToSHA1(destFilePath);	// Get the sha1 value of the file
			
			logUtils.d(TAG, "Algorithm:" + constantVariables.algorithms[encrypt_level - 1]); // Log out the algorithms.
			logUtils.d("md5 and sha1", "md5:" + md5 + "\nsha1:" + sha1);			// Log out the md5 and sha1 value of the file
			
			// Call the startUploadFileInfo function to upload file information.
			if(fileOperation.uploadFileInfo(uploadFileInfoUrl,
					String.valueOf(user_id), md5, sha1,
					String.valueOf(encrypt_level), remote_key)) {
				
				flag = true;
				
				/**
				 * This shoud call a third party application to do this stuff.
				 */
				
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
			
			logUtils.d(TAG, "Decrypt Key:" + encrypt_key);
			
			// Get the base key from SharedPreferences.
			SharedPreferences sp = getSharedPreferences(constantVariables.PREF_NAME, MODE_PRIVATE); // Get the SharedPreferences.
			String base_key = sp.getString(constantVariables.PREF_KEY, ""); // Get the string value.
			
			String decrypt_key = StringSplice.stringSplice(encrypt_key, base_key);
			
			if(fileDEncryption.Decryption(srcFilePath,
				constantVariables.algorithms[encrypt_level - 1], decrypt_key, destFilePath)) { // The decryption is succeeded.
				flag = true; // Set the flag to false.
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
        
            logUtils.d(TAG + "UploadFile", result);
            
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
		if (null != filterFiles && filterFiles.length > 0) { // The foder is a directory and contains at least on file.
			for (File file : filterFiles) { // Add the files to the ArrayList
				files.add(file);
			}
		}
		
		fileListAdapter= new FileListAdapter(getApplicationContext(), files, isSDcard); // Update the fileListAdapter
		fileListView.setAdapter(fileListAdapter); // Update the fileListView's adapter
	}

	
	/**
	 * On Activity result dealments.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
			case set_level:  // Set encrypt level.
				
				if(RESULT_OK == resultCode) { // Set encrypt level succeeded.
					String fileParentPath = data.getStringExtra("fileParentPath");
					
					Toast.makeText(MainActivity.this,
							getString(R.string.set_level_ok),
							Toast.LENGTH_SHORT).show();
					initData(new File(fileParentPath)); // Refresh the ListView.
				} else if(resultCode == RESULT_CANCELED) { // Set encrypt level canceled.
					Toast.makeText(MainActivity.this,
							getString(R.string.set_level_cancel),
							Toast.LENGTH_SHORT).show();
				}
			break;
			/*case upload: // Upload files to cloud.
				if(RESULT_OK == resultCode) {
					Toast.makeText(MainActivity.this,
							R.string.upload_success,
							Toast.LENGTH_SHORT).show();
				} else if(RESULT_CANCELED == resultCode) {
					Toast.makeText(MainActivity.this,
							R.string.upload_canceled,
							Toast.LENGTH_SHORT).show();
				}
				break;*/
			default:
				break;
		}
	}
}