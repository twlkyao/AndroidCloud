/**
 * Author:	Network
 * Editor:	Qi Shiyao
 * Date:	2013.11.15
 * Function:	Login and register on the internet
 */
package com.twlkyao.androidcloud;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

import com.twlkyao.utils.ConstantVariables;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;

public class LoginActivity extends Activity {

	private String strUsername; //user name string 
	private String strPassword; //password String
	public static String strResult; //Login result string
	private static String loginUrl = ConstantVariables.BASE_URL + ConstantVariables.LOGIN_URL; // the login url address for android function module
	
//	private ProgressDialog progressDialog; // progressdialog
	
	private StringBuilder suggest = null;
	private String uid = "";	// To store the user id.
	private String Tag = "LoginActivity"; // The logcat tag
	/**
	 * Define UI
	*/
	private EditText username_editText; //UserName EditText
	private EditText password_editText; //Password EditText
	//private CheckBox rememberMe_checkBox; //Remember checkbox
	private Button login_button; //Login button
	private Button register_button; //Register button
	
	// define the hashmap to record the basic information of users during the session
	private HashMap<String, String> session =new HashMap<String, String>();
	
	private boolean isNetError; //Indicate whether the network is available, true for network failure, false for user name or password failure

	// Deal with the time-consuming matters
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			if(0 == msg.arg1) { // The input format is error
				Toast.makeText(getApplicationContext(),
						suggest, Toast.LENGTH_SHORT).show();
				/*
				username_editText.setText(""); // Reset the username
				password_editText.setText(""); // Reset the password
				*/
			} else if(1 == msg.arg1){ // The input format is correct
				if(0 == msg.arg2) { // The login validate is failed
					Toast.makeText(getApplicationContext(),
							R.string.login_fail, Toast.LENGTH_SHORT).show();
					/*
					username_editText.setText(""); // Reset the username
					password_editText.setText(""); // Reset the password
					*/
				} else if(1 == msg.arg2) { // The login validate is passed
					Toast.makeText(getApplicationContext(),
							R.string.login_success, Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					Bundle bundle = new Bundle();	// Construct a bundle to store data.
					bundle.putString("uid", uid);	// Put the uid into bundle.
					intent.putExtras(bundle);		// Put the bundle into intent
					startActivity(intent); // Jump to the MainActivity
					
					finish(); // Destroy itself
				}
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		Log.d(Tag, "onCreate");
		
		setContentView(R.layout.login); //Set content view
		findViewsById(); 	//Find the views 
		
		setListener(); 		//Set listeners
	}

	/**
	 * Find Views
	 */
	private void findViewsById(){
		username_editText = (EditText)findViewById(R.id.username); 		//username
		password_editText = (EditText)findViewById(R.id.password);		//password
		//rememberMe_checkBox = (CheckBox)findViewById(R.id.remember_me);	//remember me
		login_button = (Button)findViewById(R.id.login);				//login
		register_button = (Button)findViewById(R.id.register);		//register
	}
	
	/**
	 * Set Listeners
	*/
	private void setListener(){
		
		/**
		 * Login button
		*/
		login_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// get the user name and password
				strUsername = username_editText.getText().toString(); //Get the username string
				strPassword = password_editText.getText().toString(); //get the password string
				
				startValidate(strUsername, strPassword, loginUrl); // Start a thread to validate
			}
		});
		
		
		/**
		 * Register button
		*/
		register_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class); // Construct an intent to jump
				startActivity(intent); // jump to the new activity
				
				finish(); // destroy the activity
			}
		});
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * Create a new thread to validate 
	 * @param username The username
	 * @param password The password
	 * @param loginUrl The login validate url
	 */
	public void startValidate(final String username, final String password, final String loginUrl) {
		final Message msg = Message.obtain(); // Get the Message object
		
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(!validateForm(username, password)) { // The input format is error
					msg.arg1 = 0; // Indicate the input format is error
				} else { // The input format is correct
					msg.arg1 = 1;
					if(!validateLogin(username, password, loginUrl)){ // The validating is not passed
						msg.arg2 = 0;
					} else {
						msg.arg2 = 1;
					}
				}
				handler.sendMessage(msg);
			}
		});
		
		thread.start();
	}
	
	/**
	 * Validate the register information and check whether they are effective
	 * @param username User name
	 * @param password Password
	 * @return The input status
	 */
	private boolean validateForm(String username, String password){
		
		boolean flag = true;
		
		suggest = new StringBuilder(); // construct a string with the length of 16
		
//		System.out.println("first length:" + suggest.length() + "suggest:" + suggest); // system out the suggest
		
		if(username == null || username.trim().equals("")){ // username is empty
			flag = false;
			suggest.append(getText(R.string.username_empty) + "\n");
		}
		if(password == null || password.trim().equals("")){ // password is empty
			flag = false;
			suggest.append(getText(R.string.password_empty));
		}
		if(!flag){ // if there are fields thar are not effective
//			Toast.makeText(RegisterActivity.this, suggest.subSequence(0, suggest.length()-1),
//					Toast.LENGTH_SHORT).show();
			
			/*********************Add Toast the thread will collapse**************************/
//			Toast.makeText(LoginActivity.this, suggest, Toast.LENGTH_SHORT).show();
			System.out.println("suggest:" + suggest); // system out the suggest
		}
		
		return flag;
	}
	
	/**
	 * Valid the user, the server judge if login success by using the DataOutputStream dos.writeInt(int);
	 * if the sever get int > 0, then succeeded, otherwise failed, after successful login, keep the password
	 * according to the remember checkbox, if the connect time longer than 5s fail too. 
	 * @param username The name of the user
	 * @param password The password of the user
	 * @param validateUrl The url for validating
	 * @return The validate status
	 */
	private boolean validateLogin(String strUsername, String strPassword, String strValidateUrl) {
		
		boolean status = false; // indicate the validate status
		
		HttpPost httpRequest = new HttpPost(strValidateUrl); // construct a new HttpPost instance according to the uri
		
		// use name-value pair to store the parameters to pass
		List<BasicNameValuePair> params=new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("username", strUsername)); // add the username name-value
		params.add(new BasicNameValuePair("password", strPassword)); // add the password name-value
		
//		System.out.println("params to send:" + params.toString()); // system out the parameters
		
		try{
			
			// encode the entity with utf8, and send the entity to the request
			httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8)); 
			
		} catch (UnsupportedEncodingException e){
			e.printStackTrace();
		}
		try{
			// execute an HTTP request and ge the result
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest); // execute the http request
			
			// response status is ok
			if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){	
			
				// get the response string and parse it
				HttpEntity entity = httpResponse.getEntity(); // obtain the HTTP response entity
				if (entity != null) { 
					String info = EntityUtils.toString(entity); // convert the entity to string
					
//					System.out.println(Tag + info); // system out the entity
					Log.d(Tag, info); // Log out the info
					JSONObject jsonObject;
					//flag indicate whether login succeeded, others to store the data from server
					String flag="";					
					String username="";
					
					String sessionid="";
					try {
						jsonObject = new JSONObject(info); // construct an JsonObject instance from the name-value Json string
						flag = jsonObject.getString("flag"); // get the value mapped by name:flag							
						username = jsonObject.getString("username"); // get the value mapped by name:name
						uid = jsonObject.getString("uid"); // get the value mapped by name:userid
						
						Log.d(Tag, "uid:" + uid);
						
						sessionid = jsonObject.getString("sessionid"); // get the value mapped by name:sessionid				
					
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// judge whether the validation if succeeded according to the flag
					if(flag.equals("success")) { // if login succeeded, set the values accordingly
						
						// set values to record the user-related information 
						//session.put("uid", uid);
						session.put("username", username);						
						session.put("sessionid", sessionid);
						status = true;
					} else { // Login failed
						status = false;
					}
				} else { // Entity is null
					status = false;
				}
			} // status code equal ok 
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return status;
	}
	
	/**
	 * The method to deal with the forget password.
	 * @param v
	 */
	public void forget_password(View v) { // Forget password.
    	Uri uri = Uri.parse("http://3g.qq.com"); 
    	Intent intent = new Intent(Intent.ACTION_VIEW, uri); 
    	startActivity(intent);
      }
}