package com.twlkyao.androidcloud;

import java.sql.Date;
import java.text.SimpleDateFormat;
import com.twlkyao.utils.LogUtils;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.view.Menu;
import android.widget.TextView;

public class MessageActivity extends Activity {

	private String TAG = "MessageActivity";
	private boolean DEBUG = true;
	private LogUtils logUtils = new LogUtils(DEBUG, TAG);
	private TextView tv_message;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		tv_message = (TextView) this.findViewById(R.id.messages);
		tv_message.setText(getSmsInPhone("content://sms/")); // Get all the message.
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.message, menu);
		return true;
	}
	
	
	public String getSmsInPhone(String messageType) {  
		
		StringBuilder smsBuilder = new StringBuilder();  
		
		try {
			Uri uri = Uri.parse(messageType);
			String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };
			Cursor cur = getContentResolver().query(uri, projection, null, null, "date desc"); // Get all the sms on device.
			
			if (cur.moveToFirst()) {  
				int index_Address = cur.getColumnIndex("address");
				int index_Person = cur.getColumnIndex("person");
				int index_Body = cur.getColumnIndex("body");
				int index_Date = cur.getColumnIndex("date");
				int index_Type = cur.getColumnIndex("type");
				
				do {  
					String strAddress = cur.getString(index_Address);
					int intPerson = cur.getInt(index_Person);
					String strbody = cur.getString(index_Body);
					long longDate = cur.getLong(index_Date);
					int intType = cur.getInt(index_Type);
					
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					Date d = new Date(longDate);
					String strDate = dateFormat.format(d);
					
					String strType = "";
					if (intType == 1) {
						strType = "接收";
					} else if (intType == 2) {
						strType = "发送";
					} else {  
						strType = "null";
					}  
					
					smsBuilder.append("[ ");  
					smsBuilder.append(strAddress + ", ");
					smsBuilder.append(intPerson + ", ");
					smsBuilder.append(strbody + ", ");
					smsBuilder.append(strDate + ", ");
					smsBuilder.append(strType);
					smsBuilder.append(" ]\n\n");
				} while (cur.moveToNext());
	  
				if (!cur.isClosed()) {
					cur.close();
					cur = null;
				}
			} else {
				smsBuilder.append("no result!");
			} // end if
			
//			smsBuilder.append("getSmsInPhone has executed!");
			
		} catch (SQLiteException ex) {
			logUtils.d("SQLiteException in getSmsInPhone", ex.getMessage());
		}
		
		return smsBuilder.toString();
	}
}
