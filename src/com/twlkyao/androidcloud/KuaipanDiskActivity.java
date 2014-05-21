package com.twlkyao.androidcloud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.twlkyao.kuaipan.DownloadTask;
import com.twlkyao.kuaipan.RequestBase;
import com.twlkyao.kuaipan.UploadTask;

import cn.kuaipan.android.openapi.AuthActivity;
import cn.kuaipan.android.openapi.AuthSession;
import cn.kuaipan.android.openapi.KuaipanAPI;
import cn.kuaipan.android.openapi.session.AccessTokenPair;
import cn.kuaipan.android.openapi.session.AppKeyPair;
import cn.kuaipan.android.sdk.exception.KscException;
import cn.kuaipan.android.sdk.exception.KscRuntimeException;
import cn.kuaipan.android.sdk.model.KuaipanFile;
import cn.kuaipan.android.sdk.oauth.Session.Root;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class KuaipanDiskActivity extends Activity {

	final private String TAG = "KuaipanDiskActivity";
	
	private Button btnKuaipan; // Kuaipan disk button.
	private Button btnOpenstack; // Openstack button.
	private Button btnWeiyun; // Weiyun button.
	private Button btnDropbox; // Dropbox button.
	private Button btn360; // 360 button.
	private Button btnBaidu; // Baidu disk button.
	private Button btn115; // 115 button.
	
//	private View kuaipanView; // The layout view of kuaipan.
	KuaipanAPI kuaipanAPI; // The api of kuaipan disk.
	private AuthSession mAuthSession;
	private boolean mLoggedIn; // Login status indicator.
	private int authType; // The auth type.
	
	private Button btnLogin; // The login button.
	private Button btnOK; // The ok button.
//	private Button btnCancel; // The cancel button.
	private ArrayList<KuaipanFile> kuaipanFileList; // The ArrayList of kuaipan file.
	private TextView userInfo; // The TextView to display the user info.
	private TextView remoteFilePath; // The TextView to display the file path.
	private ListView kuaipanFileListView; // The ListView to hold the KuaipanFile.
	private SimpleAdapter listItemAdapter; // The adapter to adapt the listview and data.
	/**
     * Be sure to configure the consumer key and secret
     */
    private static final String APP_KEY = "" + "xcatrEBRzuAprw4k";
    private static final String APP_SECRET = "" + "iYDnKUOGEhzpHDNr";
    
    final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    final static private String ACCESS_AUTH_TYPE_NAME = "ACCESS_AUTH_TYPE_NAME";
    final static private String ACCESS_UID_NAME = "ACCESS_UID_NAME";
//    private static final int FILE_SELECTED = 1; // Indicator of file selection.
    
    private static final String fREMOTE_FILEPATH = "/我的应用/AndroidCloud/";
    private static String REMOTE_FILEPATH = fREMOTE_FILEPATH; // The path of the cloud.
    private String FILEPATH = ""; // Record the file path from the MainActivity.
    private Intent intent; // The intent from the start Activity.
    private Bundle bundle; // The bundle to store data.
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");
		
		// We create a new AuthSession so that we can use the Kuaipan API.
		mAuthSession = buildSession();
		kuaipanAPI = new KuaipanAPI(this, mAuthSession);

		// Basic Android widgets
		setContentView(R.layout.kuaipan_disk_layout);
		kuaipanFileList = new ArrayList<KuaipanFile>();
		
		intent = getIntent(); // Get the intent.
		bundle = intent.getExtras(); // Get the bundle.
		FILEPATH = bundle.getString("filePath"); // Get the string from the bundle.
		
		findViews();
		setListeners();
		
		setLoggedIn(kuaipanAPI.isAuthorized());
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.v(TAG, "onPause");
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		logOut(); // Clear the session and api.
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		Log.v(TAG, "onRestart");
		REMOTE_FILEPATH = fREMOTE_FILEPATH;
		remoteFilePath.setText(REMOTE_FILEPATH);
		getFileMetadata(REMOTE_FILEPATH);
//		listItemAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			finish(); // Finish the activity.
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * Find the views on the layout.
	 */
	public void findViews() {
		/*btnKuaipan = (Button) findViewById(R.id.btn_kuaipan); // Find the kuaipan disk button.
		btnOpenstack = (Button) findViewById(R.id.btn_openstack);
		btnWeiyun = (Button) findViewById(R.id.btn_weiyun);
		btnDropbox = (Button) findViewById(R.id.btn_dropbox);
		btn360 = (Button) findViewById(R.id.btn_360);
		btnBaidu = (Button) findViewById(R.id.btn_baidu);
		btn115 = (Button) findViewById(R.id.btn_115);*/
		
		userInfo = (TextView) findViewById(R.id.user_info);
		btnLogin = (Button) findViewById(R.id.btn_login);
		remoteFilePath = (TextView) findViewById(R.id.remote_disk_file_path);
		kuaipanFileListView = (ListView) findViewById(R.id.remote_disk_files);
		btnOK = (Button) findViewById(R.id.btn_ok);
		btnLogin.setText(getString(R.string.login));
//		btnCancel = (Button) findViewById(R.id.btn_cancel);
	}
	
	/**
	 * Set click listeners for the views.
	 */
	public void setListeners() {
		btnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onClickLogin();
				/*if(mLoggedIn) {
					getUserInfo();
					getFileMetadata("/");
					Log.v("btnLoginIf", mLoggedIn + "");
				}*/
			}
		});
		
		btnOK.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!FILEPATH.equals("")) {
					
					RequestBase request = new RequestBase();
					request.setApi(kuaipanAPI); // Set api.
					request.setFilePath(FILEPATH); // Set local file path.
					request.setRemotePath(REMOTE_FILEPATH); // Set remote file path.
					remoteFilePath.setText(REMOTE_FILEPATH); // Set the remote file path display.
					
					String result = new UploadTask(KuaipanDiskActivity.this).start(request); // Upload the file to remote cloud.
					if(null == result){ // Refresh the kuaipanFile list after upload successfully.
						getFileMetadata(fREMOTE_FILEPATH);
					}
//					finish(); // Finish the activity.
				} else {
					Toast.makeText(KuaipanDiskActivity.this,
							R.string.file_path_empty, 
							Toast.LENGTH_SHORT).show();
				}
//				finish(); // Finish the Activity.
			}
		});
		
		/*btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(RESULT_CANCELED, intent); // The operation is succeeded.
				startActivity(intent); // Jump to the new activity.
				finish();
//				logOut(); // Call the logOut() function to finish the activity.
			}
		});*/
	}

	/**
	 * The event on click login button.
	 */
	private void onClickLogin() {
		if (!mLoggedIn) {
			// Start the authentication
			kuaipanAPI.startAuthForResult();
		} else {
			logOut();
		}
	}
	
	/**
	 * Logout the account and update the UIs.
	 */
	private void logOut() {
		
		// Remove credentials from the session
		mAuthSession.unAuthorize();
		kuaipanAPI.unAuthorize();
		btnLogin.setText(R.string.login);
		// Clear our stored keys
		clearKeys();
		
		// Change UI state to display logged out version
		setLoggedIn(false);
//		finish(); // Stop the current activity.
	}
	
	/**
     * Information of this account
     */
    void getUserInfo() {
        if (!kuaipanAPI.isAuthorized()) {
            showToast(getString(R.string.unauthorized));
            return;
        }

        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (!TextUtils.isEmpty(result)) {
                	Log.v(TAG, "result:" + result);
                	// Get the string split by ", because the json string is not validate.
                	String[] resultString = result.split("\"");
                	userInfo.setText(resultString[1]); // Get the user name from the json string.
					
                } else {
                    showToast(getString(R.string.get_user_info_failed));
                    return;
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                try {
                    return kuaipanAPI.getAccountInfo().toString();

                } catch (KscRuntimeException e) {
                    e.printStackTrace();
                    Log.v(TAG,
                            "KscRuntimeException--Failed!!!" + e.getErrorCode());
                } catch (KscException e) {
                    e.printStackTrace();
                    Log.v(TAG, "KscException--Failed!!!" + e.getErrorCode());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.v(TAG, "InterruptedException--Failed!!!");
                }
                return "";
            }
        }.execute();
    }
	
	private AuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AuthSession session;

        String[] stored = getKeys();
        if (stored != null) {
            AccessTokenPair accessToken = new AccessTokenPair(stored[0],
                    stored[1]);
            if (TextUtils.isEmpty(stored[2])) {

            }
            authType = !TextUtils.isEmpty(stored[2]) ? Integer
                    .valueOf(stored[2]) : 0;
            session = new AuthSession(appKeyPair, accessToken, Root.KUAIPAN);
            this.authType = Integer.valueOf(stored[3]);
        } else {
            session = new AuthSession(appKeyPair, Root.KUAIPAN);
        }
        return session;
    }
	
	/**
     * Shows keeping the access keys returned from Trusted Authenticator in a
     * local store, rather than storing user name & password, and
     * re-authenticating each time (which is not to be done, ever).
     */
    private void storeKeys(String key, String secret, String uid,
            String authType) {
        // Save the access key for later
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.putString(ACCESS_KEY_NAME, key);
        edit.putString(ACCESS_SECRET_NAME, secret);
        edit.putString(ACCESS_UID_NAME, uid);
        edit.putString(ACCESS_AUTH_TYPE_NAME, authType);
        edit.commit();
    }
	
    /**
     * Clear all the stored keys.
     */
    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit(); // Create an editor for the SharedPreferences.
        edit.clear(); // Mark to clean the editor.
        edit.commit(); // Commit the changes.
    }
    
	/**
     * Shows keeping the access keys returned from Trusted Authenticator in a
     * local store, rather than storing user name & password, and
     * re-authenticating each time (which is not to be done, ever).
     * 
     * @return Array of [access_key, access_secret, uid, auth_type], or null if
     *         none stored
     */
    private String[] getKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        String uid = prefs.getString(ACCESS_UID_NAME, null);
        String authType = prefs.getString(ACCESS_AUTH_TYPE_NAME, null);
        if (key != null && secret != null) {
            String[] ret = new String[4];
            ret[0] = key;
            ret[1] = secret;
            ret[2] = uid;
            ret[3] = authType;
            return ret;
        } else {
            return null;
        }
    }

    /**
     * Convenience function to change UI state based on being logged in
     */
    private void setLoggedIn(boolean loggedIn) {
        mLoggedIn = loggedIn;
        Log.v("setLoggedIn", mLoggedIn + "");
        if (!loggedIn) {
        	btnLogin.setText(getString(R.string.login));
            clearDisplay();
        } else {
        	btnLogin.setText(getString(R.string.logout));
            /*getUserInfo();
            getFileMetadata("/我的应用/AndroidCloud/");*/
        }
        btnOK.setEnabled(loggedIn);
//        btnCancel.setEnabled(loggedIn);
    }
    
    /**
     * Clear the user info display.
     */
    private void clearDisplay() {
       
        userInfo.setText(""); // Set the user info to empty.
        kuaipanFileList.clear(); // Clear the KuaipanFile list.
//        listItemAdapter.notifyDataSetChanged(); // Clear the data in listview.
    }
    
    /**
     * Retrieval detail info files of this account in current file path
     * 
     * @param path the file path in Kuaipan server
     */
    void getFileMetadata(String path) {
        if (!kuaipanAPI.isAuthorized()) {
            showToast(getString(R.string.unauthorized));
            return;
        }

        new AsyncTask<String, Void, String>() {
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                if (TextUtils.isEmpty(result)) {
                    showToast(getString(R.string.get_user_info_failed));
                    return;
                }

                ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
                for (int i = 0; i < kuaipanFileList.size(); i++) {
                	
                	Log.v("keys",kuaipanFileList.size() + "");
                	
                    HashMap<String, Object> map = new HashMap<String, Object>();
//                    map.put("FileItem", kuaipanFileList.get(i).path;
                    map.put("FileItem", kuaipanFileToString(kuaipanFileList.get(i))); // Get the file path.
                    listItem.add(map);
                }

                listItemAdapter = new SimpleAdapter(
                        KuaipanDiskActivity.this, listItem, R.layout.kuaipan_file_list_item,
                        new String[] {
                            "FileItem"
                        }, new int[] {
                            R.id.filepath
                        });

                kuaipanFileListView.setAdapter(listItemAdapter);
                kuaipanFileListView.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                            int arg2, long arg3) {

                        KuaipanFile file = kuaipanFileList.get(arg2);
                        displayAlert(getString(R.string.fileinfo), file); // Display the file info.
                        
                        /*if (file != null && file.isFile()) { // The item clicked is a file.
                        	displayAlert(getString(R.string.fileinfo), file);
                        } else { // The item clicked is a directory.
                        	String[] fileInfo = file.toString().split("\""); // Get the file path of the current file.
                        	REMOTE_FILEPATH = fileInfo[1];
                        	Log.v(TAG, "新的路径：" + REMOTE_FILEPATH);
                        	remoteFilePath.setText(REMOTE_FILEPATH);
                        	getFileMetadata(REMOTE_FILEPATH);
                        }*/
                    }
                });
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    String filepath = params[0];
                    Log.v("keys", (params[0] == null ? "null" : params[0]));
                    KuaipanFile file = kuaipanAPI.metadata(filepath);
                    kuaipanFileList.clear();

                    getAllFiles(kuaipanFileList, file);
                    Log.v(TAG, "metadata:" + file.toString());
                    return file.toString();
                } catch (KscRuntimeException e) {
                    e.printStackTrace();
                    Log.v(TAG,
                            "KscRuntimeException--Failed!!!" + e.getErrorCode());
                } catch (KscException e) {
                    e.printStackTrace();
                    Log.v(TAG, "KscException--Failed!!!" + e.getErrorCode());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.v(TAG, "InterruptedException--Failed!!!");
                }
                return "";
            }
        }.execute(new String(path));
    }
    
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    
    protected void displayAlert(String title, final KuaipanFile file) {

        AlertDialog.Builder confirm = new AlertDialog.Builder(this);
        confirm.setTitle(title); // Set title.
        confirm.setMessage(file.toString()); // Set message.
       
        if(file.isFile()) { // The item clicked is a KuaipanFile, add the download button.
        	// Set positive button.
            confirm.setPositiveButton(getString(R.string.download),
            		new DialogInterface.OnClickListener() {
            			public void onClick(DialogInterface dialog, int which) {
            				dialog.dismiss();
            				String remotePath = file.path;
            				RequestBase request = new RequestBase();
            				request.setApi(kuaipanAPI);
            				request.setRemotePath(remotePath);
            				new DownloadTask(KuaipanDiskActivity.this).start(request);
            				Log.v(TAG, "getDataFile:" + remotePath);
            			}
            });
        }
        
        // Set negative button.
        confirm.setNegativeButton(getString(R.string.cancel),
        new DialogInterface.OnClickListener() {
        	
        	public void onClick(DialogInterface dialog, int which) {
        		dialog.dismiss();
        	}
        });
        confirm.show();
//        confirm.show().show();
    }
    
    /**
     * Add all the KuaipanFiles under the file.
     * @param list The list to store the files.
     * @param file The file under which to get.
     */
    public void getAllFiles(ArrayList<KuaipanFile> list, KuaipanFile file) {
//    	public void getAllFiles(ArrayList<String> list, KuaipanFile file) {
    	
//        list.add(file); // Add the file itself.
        
        if (file.isDirectory()) {
            List<KuaipanFile> childrens = file.getChildren();
            if (childrens != null) {
                int size = childrens.size();
                for (int i = 0; i < size; i++) {
                    KuaipanFile children = childrens.get(i);
                    /*if (children.isDirectory()) {
                        getAllFiles(list, childrens.get(i));
                    } else {
                        list.add(children);
                    }*/
                    list.add(children); // Only add the direct child files.
                }
            }
        }
    }
    
    /**
     * Get the file name from the kuaipanFile.
     * @param kuaipanFile The kuaipanFile.
     * @return The file name string.
     */
    public String kuaipanFileToString(KuaipanFile kuaipanFile) {
    	String[] parentPath = kuaipanFile.toString().split("\"");
    	String[] directory = parentPath[1].split("/");
    	return directory[directory.length - 1];
         /*File:{path:"/我的应用/AndroidCloud/weiyun2.1.702/com",
         	file_id:4554787047604274, type:"folder",
         	name:"com", sha1:"", size:0, rev:1, 
         	create_time:"Fri May 02 20:44:15 GMT+0800 2014",
         	modify_time:"Fri May 02 20:44:15 GMT+0800 2014", 
         	children:{[*/
    }
    
    /**
     * Get the parent file path from the kuaipanFile.
     * @param kuaipanFile The kuaipanFile.
     * @return The file parent's name string.
     */
    public String kuaipanFileParentFile(KuaipanFile kuaipanFile) {
    	String[] parentPath = kuaipanFile.toString().split("\"");
    	String[] directory = parentPath[1].split("/");
    	return directory[directory.length - 2];
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent intent) {

        Log.v(TAG, "requestCode:" + requestCode + " resultCode:" + resultCode);

        if (requestCode == AuthActivity.AUTH_REQUEST_CODE) {

            Log.v(TAG, "resultCode: " + getAuthResultName(resultCode));
            Log.v(TAG, "ResultData:  " + intent);

            if (intent == null) {
                return;
            }

            if (resultCode == AuthActivity.SUCCESSED && intent != null) {
                Bundle values = intent.getExtras();
                final String accessToken = values
                        .getString(AuthActivity.EXTRA_ACCESS_TOKEN);
                final String accessSecret = values
                        .getString(AuthActivity.EXTRA_ACCESS_SECRET);
                final String uid = values.getString(AuthActivity.EXTRA_UID);
                authType = values.getInt(AuthActivity.EXTRA_AUTH_TYPE);

                Log.v(TAG, "Authorized by " + getAuthName(authType) + " server");

                final String error_code = values
                        .getString(AuthActivity.EXTRA_ERROR_CODE);
                Log.v(TAG, "!!!accessToken=" + accessToken + "\n"
                        + "accessSecret=" + accessSecret + "\n" + "uid=" + uid
                        + "\n" + "error_code=" + error_code);

                /**
                 * set validate access token pair, then isAuthorized will return
                 * true
                 */
                kuaipanAPI.setAccessToken(accessToken, accessSecret);

                getUserInfo();
				getFileMetadata(fREMOTE_FILEPATH);
                
                setLoggedIn(kuaipanAPI.isAuthorized());

                // store all concern values into preference
                storeKeys(accessToken, accessSecret, uid,
                        String.valueOf(authType));
            } else { // Auth failed.
            	finish(); // Finish the Activity.
            }
        } else { // Other kind of operation.
          
        }
    }
    
    /**
     * Get the name of auth type.
     * @param authType
     * @return
     */
    private String getAuthName(int authType) {
        if (authType == AuthActivity.WEIBO_AUTH) {
            return "[weibo]";
        } else if (authType == AuthActivity.QQ_AUTH) {
            return "[qq]";
        } else if (authType == AuthActivity.XIAOMI_AUTH) {
            return "[xiaomi]";
        } else if (authType == AuthActivity.KUAIPAN_AUTH) {
            return "[kuaipan]";
        }
        return "[UnKnown]";
    }
    
    private String getAuthResultName(int resultCode) {
        if (authType == AuthActivity.CANCELED) {
            return "[CANCELED]";
        } else if (authType == AuthActivity.FAILED) {
            return "[FAILED]";
        } else if (authType == AuthActivity.SUCCESSED) {
            return "[SUCCESSED]";
        }
        return "[UnKnown]";
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
}
