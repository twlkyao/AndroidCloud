package com.twlkyao.androidcloud;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	/*Register UI*/
	private EditText usernameEditText; // user name
	private EditText emailEditText; // email address
	private EditText passwordEditText; // password
	private EditText confirm_passwordEditText; // confirm password
	
	private String strUsername;	// To store the username string.
	private String strPassword;	// To store the password string.
	private String strEmail;	// To store the email string.
	private String strConfirmPassword;	// To store the confirm password sting
	
	private Button registerButton; // register button
	private Button resetButton; // reset button
	
	String strPattern = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z\\.]*[a-zA-Z]$";
	Pattern pattern;
	
	private String registerUrl= ConstantVariables.BASE_URL + ConstantVariables.REGISTER_URL; //Set the login url
	StringBuilder suggest = null;
	
	// record user's basic information during the session
	private HashMap<String, String> session =new HashMap<String, String>();
	
	// Deal with the time-consuming matters
	private Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(0 == msg.arg1) { // The input format is error
					Toast.makeText(getApplicationContext(),
							suggest, Toast.LENGTH_SHORT).show();
				} else if(1 == msg.arg1) { // The input format is correct
					if(0 == msg.arg2) { // The reigster validate is failed
						Toast.makeText(getApplicationContext(),
								R.string.register_fail, Toast.LENGTH_SHORT).show();
					} else if(1 == msg.arg2) { // The register is succeeded
						Toast.makeText(getApplicationContext(),
								R.string.register_success, Toast.LENGTH_SHORT).show();
						
						Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
						startActivity(intent); // Jump to the MainActivity
						finish();	// Destroy itself 
					}
				}
			}
		};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		findViewsById(); // find the views
		setListener(); // set listeners
	}
	
	private void findViewsById(){
		usernameEditText = (EditText)findViewById(R.id.username);
		emailEditText = (EditText)findViewById(R.id.email);
		passwordEditText = (EditText)findViewById(R.id.password);
		confirm_passwordEditText = (EditText)findViewById(R.id.confirm_password);
		registerButton = (Button)findViewById(R.id.register);
		resetButton= (Button)findViewById(R.id.reset);
	}
	
	private void setListener(){
		
		// register button
		registerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				strUsername = usernameEditText.getText().toString();	// Get the user name
				strEmail = emailEditText.getText().toString();		// Get the E-mail address
				strPassword = passwordEditText.getText().toString();	// Get the password
				strConfirmPassword = confirm_passwordEditText.getText().toString();	// Get the confirm password
				
				// Call the function to validate the register information
				startValidate(strUsername, strEmail, strPassword, strConfirmPassword, registerUrl);
			}
			
		});
		
		/**
		 * reset button
		 */
		resetButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				resetForm(); // reset the form
			}
		});
		
		/**
		 * validate the email address
		 */
		/**
		emailEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
						@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				String strPattern = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z\\.]*[a-zA-Z]$";
				String strEmail = emailEditText.getText().toString();
				Pattern p = Pattern.compile(strPattern);
				Matcher m = p.matcher(strEmail);
				if(!m.matches()){ // email form error
					Toast.makeText(RegisterActivity.this, getString(R.string.email_format_error),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		*/
		
	}
	
	/**
	 * Validate the input parameters, both the format and the effectiveness.
	 * @param username
	 * @param password
	 * @param loginUrl
	 */
	public void startValidate(final String username, final String email,
			final String password, final String confirmPassword, final String registerUrl) {
		final Message msg = Message.obtain(); // Get the Message object
		new Thread(){
			public void run() {
				if(!validateForm(username, email, password, confirmPassword)) { // The input format is error
					msg.arg1 = 0;
				} else { // The input format is correct
					msg.arg1 = 1;
					if(!validateRegister(username, email, password, registerUrl)) { // The register validate is not passed
						msg.arg2 = 0;
					} else{
						msg.arg2 = 1; // The register validate is passed
					}
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	
	/**
	 * Validate the register information and check whether they are effective
	 * @param username User name
	 * @param email Email address
	 * @param password Password
	 * @param confirmPassword Confirm password
	 * @return The input status
	 */
	private boolean validateForm(String username, String email, String password, String confirmPassword){
		
		boolean flag = true;
		
		suggest = new StringBuilder(); // construct a string with the length of 16
		
//		System.out.println("first length:" + suggest.length() + "suggest:" + suggest); // system out the suggest
		
		if(username == null   || username.trim().equals("")){ // username is null or empty
			flag = false;
			suggest.append(getString(R.string.username_empty) + "\n");
		}
		if(email == null || email.trim().equals("")){ // email address is null or empty
			flag = false;
			suggest.append(getString(R.string.email_empty) + "\n");
		} else {
			pattern = Pattern.compile(strPattern);
			Matcher m = pattern.matcher(email);
			if(!m.matches()){ // email form error
				flag = false;
				suggest.append(getString(R.string.email_format_error) + "\n");
			}
		}
		if(password == null || password.trim().equals("")){ // password is null or empty
			flag = false;
			suggest.append(getString(R.string.password_empty) + "\n");
		}
		if(!password.equals(confirmPassword)){ // passwords are not the same
			flag = false;
			suggest.append(getString(R.string.password_not_the_same));
		}
		if(!flag){ // if there are fields thar are not effective
//			Toast.makeText(RegisterActivity.this, suggest.subSequence(0, suggest.length()-1),
//					Toast.LENGTH_SHORT).show();
			
//			Toast.makeText(RegisterActivity.this, suggest, Toast.LENGTH_SHORT).show();
			System.out.println("length:" + suggest.length() + "suggest:" + suggest); // system out the suggest
		}
		
		return flag; // return the validate result
	}
	
	/**
	 * Clear form
	 */
	private void resetForm(){
		usernameEditText.setText("");
		emailEditText.setText("");
		passwordEditText.setText("");
		confirm_passwordEditText.setText("");
//		usernameEditText.requestFocus();
	}
	
	/**
	 * Validate the effectiveness of the parameters.
	 * @param userName The username.
	 * @param email The email address.
	 * @param password The password.
	 * @param validateUrl The validate url.
	 * @return
	 */
	private boolean validateRegister(String userName, String email, String password, String validateUrl) {
		
		// indicate the login state
		boolean loginState = false;

		HttpPost httpRequest =new HttpPost(validateUrl); // create an HttpPost instance with the specifie url
		
		// pass the parameters stored in name-value pair
		List<BasicNameValuePair> params=new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("username",userName)); // add the username name-value
		params.add(new BasicNameValuePair("email", email)); // add the email name-value
		params.add(new BasicNameValuePair("password",password)); // add the password name-value
		
		System.out.println("params to send:" + params.toString()); // system out the parameters
		
		try {
			
			// send HTTP request
			httpRequest.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8)); // encode the entity with utf8
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			
			// execute the HTTP request and get the HTTP response
			HttpResponse httpResponse=new DefaultHttpClient().execute(httpRequest); // execute the http request
			
			// the response status is ok
			if(httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK){	
			
				// get the respond strint for parse
				HttpEntity entity = httpResponse.getEntity(); // obtain the HTTP response entity
				if (entity != null) {
					String info = EntityUtils.toString(entity); // convert the entity to string
					System.out.println("register info:" + info);
					
					// parse the data from the server
					JSONObject jsonObject=null;
					//flag indicate whether login succeeded, get from server
					String flag="";					
					String username="";
					//String uid="";
					String sessionid="";
					try {
						jsonObject = new JSONObject(info);
						flag = jsonObject.getString("flag");							
						username = jsonObject.getString("username"); // the username is null when you first register
						//uid = jsonObject.getString("uid"); // the uid is null when you first register
						sessionid = jsonObject.getString("sessionid"); // the sessionid is null when you first register
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// judge whether pass through the server validation, according to the flag from server 
					if(flag.equals("success")){
						
						// pass parameter to session to record the user related information
						//session.put("uid", uid);
						session.put("username", username);						
						session.put("sessionid", sessionid);
						
						loginState = true;
//						return true;
						
					} else {
						loginState = false;
//						return false;
						
					}
				} else {
					loginState = false;
//					return false;
				}
			}	
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return loginState;
	}
}