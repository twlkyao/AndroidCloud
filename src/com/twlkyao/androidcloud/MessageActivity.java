package com.twlkyao.androidcloud;

import com.twlkyao.utils.ExportSms;
import com.twlkyao.utils.ImportSms;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

public class MessageActivity extends Activity {

	private String TAG = "MessageActivity";
	private boolean DEBUG = true;
	// Buttons to operate Sms.
	private Button backupSmsButton;
	private Button recoverSmsButton;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
		findViews();
		setListeners();
	}

	/**
	 * Find the Views on the layout file.
	 */
	public void findViews() {
		backupSmsButton = (Button) this.findViewById(R.id.backupSms);
		recoverSmsButton = (Button) this.findViewById(R.id.recoverSms);
	}
	
	/**
	 * Set listeners for the Buttons.
	 */
	public void setListeners() {
	
	/**
	 * Backup Sms Button Listener.
	 */

	backupSmsButton.setOnClickListener(new View.OnClickListener() {
	
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ExportSms exportSmsXml = new ExportSms(getApplicationContext());
				try {
					exportSmsXml.createXml();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		/**
		 * Recover Sms Button Listener.
		 */
		recoverSmsButton.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ImportSms importSms = new ImportSms(getApplicationContext());
				importSms.InsertSMS();
			}
		});
	}
}