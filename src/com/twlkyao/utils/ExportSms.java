package com.twlkyao.utils;

import java.io.File;
import java.io.FileOutputStream;
import org.xmlpull.v1.XmlSerializer;

import com.twlkyao.utils.ConstantVariables;
import com.twlkyao.utils.LogUtils;
import com.twlkyao.utils.SmsField;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;
import android.widget.Toast;

public class ExportSms {
	
	private boolean debug = true;
	private String Tag = "ExportSmsXml";
	private LogUtils logUtils = new LogUtils(debug, Tag);
	
	Context context;
	public static final String SMS_URI_ALL = "content://sms/"; // Sms uri.
	private FileOutputStream outStream = null;
	private XmlSerializer serializer; // XmlSerializer.

	public ExportSms(Context context) {
		this.context = context;
	}

	/**
	 * Set the parameters of the xml backup file.
	 */
	public void xmlStart() {

		// Construct the file path to store the sms xml file.
		String path = Environment.getExternalStorageDirectory().getPath() 
				+ ConstantVariables.smsBackupLocation;
		
		logUtils.d(Tag, "path:" + path);

		// Create the directory to store the sms xml file.
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		// Create the sms xml file.
		File file = new File(path, ConstantVariables.smsFile);
		try {
			outStream = new FileOutputStream(file);
			serializer = Xml.newSerializer();
			serializer.setOutput(outStream, "UTF-8"); // Set the encode to UTF-8.
			serializer.startDocument("UTF-8", true); // Write <?xml declaration.
			serializer.startTag(null, "sms"); // Write a start tag.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the xml file.
	 * @return True, if the backup is succeeded.
	 * @throws Exception
	 */
	public boolean createXml() throws Exception {

		this.xmlStart(); // Set the parameters of the xml file.
		Cursor cursor = null;
		try {
			ContentResolver conResolver = context.getContentResolver();
			
			String[] projection = new String[] { SmsField.ADDRESS, SmsField.PERSON, 
					SmsField.DATE, SmsField.PROTOCOL, 
					SmsField.READ, SmsField.STATUS,	
					SmsField.TYPE, SmsField.REPLY_PATH_PRESENT,
					SmsField.BODY,SmsField.LOCKED,SmsField.ERROR_CODE,
					SmsField.SEEN };    // type==1 inbox,type==2 sendbox; 
										// read==0 unread, read==1 read,seen==0 unread, seen==1 read。
			Uri uri = Uri.parse(SMS_URI_ALL);
			cursor = conResolver.query(uri, projection, null, null, "_id asc");
			if (cursor.moveToFirst()) {
				// Check the database, if subject and service_center are null,
				// then no need to get their data.			
				String address;
				String person;
				String date;
				String protocol;
				String read;
				String status;
				String type;
				String reply_path_present;
				String body;
				String locked;
				String error_code;
				String seen;
				do {
					// if address == null, xml file will not create the attribute,
					// in case of parsing correctly, assure all items' 
					// number and sequence of tags are consistent.
					address = cursor.getString(cursor.getColumnIndex(SmsField.ADDRESS));
					if (address == null) {
						address = "";
					}
					person = cursor.getString(cursor.getColumnIndex(SmsField.PERSON));
					if (person == null) {
						person = "";
					}
					date = cursor.getString(cursor.getColumnIndex(SmsField.DATE));
					if (date == null) {
						date = "";
					}
					protocol = cursor.getString(cursor.getColumnIndex(SmsField.PROTOCOL));
					if (protocol == null) { // Convent for xml parsing.
						protocol = "";
					}
					read = cursor.getString(cursor.getColumnIndex(SmsField.READ));
					if (read == null) {
						read = "";
					}
					status = cursor.getString(cursor.getColumnIndex(SmsField.STATUS));
					if (status == null) {
						status = "";
					}
					type = cursor.getString(cursor.getColumnIndex(SmsField.TYPE));
					if (type == null) {
						type = "";
					}
					reply_path_present = cursor.getString(
							cursor.getColumnIndex(SmsField.REPLY_PATH_PRESENT));
					if (reply_path_present == null) { // Convent for xml parsing.
						reply_path_present = "";
					}
					body = cursor.getString(cursor.getColumnIndex(SmsField.BODY));
					if (body == null) {
						body = "";
					}
					locked = cursor.getString(cursor.getColumnIndex(SmsField.LOCKED));
					if (locked == null) {
						locked = "";
					}
					error_code = cursor.getString(cursor.getColumnIndex(SmsField.ERROR_CODE));
					if (error_code == null) {
						error_code = "";
					}
					seen = cursor.getString(cursor.getColumnIndex(SmsField.SEEN));
					if (seen == null) {
						seen = "";
					}
					// Create xml tags.					
					// Start tag.
					serializer.startTag(null, "item");
					// Add attributes.
					serializer.attribute(null, SmsField.ADDRESS, address);
					serializer.attribute(null, SmsField.PERSON, person);
					serializer.attribute(null, SmsField.DATE, date);
					serializer.attribute(null, SmsField.PROTOCOL, protocol);
					serializer.attribute(null, SmsField.READ, read);
					serializer.attribute(null, SmsField.STATUS, status);
					serializer.attribute(null, SmsField.TYPE, type);
					serializer.attribute(null, SmsField.REPLY_PATH_PRESENT, reply_path_present);
					serializer.attribute(null, SmsField.BODY, body);
					serializer.attribute(null, SmsField.LOCKED, locked);
					serializer.attribute(null, SmsField.ERROR_CODE, error_code);
					serializer.attribute(null, SmsField.SEEN, seen);
					// End tag.
					serializer.endTag(null, "item");

				} while (cursor.moveToNext());
			} else {
				return false;
			}
		} catch (SQLiteException ex) {
			ex.printStackTrace();
			
			logUtils.d(Tag, "SQLitException:" + ex.getMessage());
			
		}finally {
			if(cursor != null) {
				cursor.close(); // Close the cursor manually.			
			}
		}
		serializer.endTag(null, "sms");
		serializer.endDocument();
		outStream.flush();
		outStream.close();
		Toast.makeText(context, "Backup Completed!", Toast.LENGTH_SHORT).show();
		return true;
	}
}