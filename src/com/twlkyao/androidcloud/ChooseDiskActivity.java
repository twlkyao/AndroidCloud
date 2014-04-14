package com.twlkyao.androidcloud;

import cn.kuaipan.android.openapi.AuthSession;
import cn.kuaipan.android.openapi.KuaipanAPI;
import cn.kuaipan.android.openapi.session.AccessTokenPair;
import cn.kuaipan.android.openapi.session.AppKeyPair;
import cn.kuaipan.android.sdk.oauth.Session.Root;
import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ChooseDiskActivity extends Activity {

	private Button btnKuaipan; // Kuaipan disk button.
	private Button btnBaidu; // Baidu disk button.

	KuaipanAPI kuaipanAPI; // The api of kuipan disk.
	private AuthSession mAuthSession;
	private int authType;
	
	/**
     * Be sure to configure the consumer key and secret
     */
    private static final String APP_KEY = "" + "xcBjcIHOvb0RhxTg";
    private static final String APP_SECRET = "" + "VA5w25Qw0LOKmNhT";
    
    final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    final static private String ACCESS_AUTH_TYPE_NAME = "ACCESS_AUTH_TYPE_NAME";
    final static private String ACCESS_UID_NAME = "ACCESS_UID_NAME";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		 // We create a new AuthSession so that we can use the Kuaipan API.
        mAuthSession = buildSession();
        kuaipanAPI = new KuaipanAPI(this, mAuthSession);
        
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_disk);
		// Show the Up button in the action bar.
		
		findViews();
		setListeners();
	}

	/**
	 * Find the views on the layout.
	 */
	public void findViews() {
		btnKuaipan = (Button) findViewById(R.id.btn_kuaipan); // Find the kuaipan disk button.
	}
	
	/**
	 * Set click listeners for the views.
	 */
	public void setListeners() {
		btnKuaipan.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				kuaipanAPI.startAuthForResult();
			}
		});
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
}
