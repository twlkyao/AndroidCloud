/**
 * @author:	Network
 * @editor:	Shiyao Qi
 * @date:	2014.02.23
 * @email: qishiyao2008@126.com
 * Function:	Get all the contacts and display them for use.
 */
package com.twlkyao.androidcloud;

import com.twlkyao.utils.LogUtils;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.TextView;
public class ContactsActivity extends Activity {

	private TextView contacts; // TextView to display the contacts.
	private ContentResolver contentResolver;
	private String result = ""; // To store the contacts.
	
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
}
