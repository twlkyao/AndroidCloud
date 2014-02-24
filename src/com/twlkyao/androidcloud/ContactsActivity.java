/**
 * Author:	Network
 * Editor:	Shiyao Qi
 * Date:	2014.02.23
 * Function:	Get all the contacts and display them for use.
 */
package com.twlkyao.androidcloud;

import com.twlkyao.utils.LogUtils;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.widget.TextView;

public class ContactsActivity extends Activity {

	private TextView contacts; // TextView to display the contacts.
	private ContentResolver contentResolver;
	private String result = ""; // To store the contacts.
	private String TAG = "ContactsActivity";
	private boolean DEBUG = true;
	private LogUtils logUtils = new LogUtils(DEBUG, TAG);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		contacts = (TextView) this.findViewById(R.id.contacts);
		contentResolver = getContentResolver();
		
		String[] columns = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Phone.NUMBER};
		
		Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				columns,
				null, null, null);
		
		while(cursor.moveToNext()) {
			result += "\n" + cursor.getString(0)
					+ ":" + cursor.getString(1) + "\n";
		}
		
		// Release Cursor resource.
		cursor.close();
		contacts.setText(result);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contacts, menu);
		return true;
	}

}
