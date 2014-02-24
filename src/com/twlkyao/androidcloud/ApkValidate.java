/**
 * Author:	Shiyao Qi
 * Date:	2014.01.11
 * Function:	Validate the downloaded apks.
 */
package com.twlkyao.androidcloud;

import java.io.File;

import com.twlkyao.utils.ApkOperation;
import com.twlkyao.utils.ConstantVariables;
import com.twlkyao.utils.LogUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ApkValidate extends Activity {

	private LinearLayout layout;
	private Button btn_cancel;
	private Button btn_ok;
	private TextView tv_message;
	
	private ApkOperation apkOperation = new ApkOperation();
	private String filePath;
	private String tag = "ApkValidation";
	private boolean debug = false;
	private LogUtils logUtils = new LogUtils(debug, tag);
	private String apkCheckUrl = ConstantVariables.BASE_URL + ConstantVariables.CHECK_APK_INFO;
//	private String apkCheckUrl = "http://10.0.2.2/cloud/check_apk_info"; // Change this to your service address.
	// Deal with the time-consuming matters
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what) {
			case 1: // Validation passed.
				tv_message.setText(getString(R.string.app_passed));
				
				// Need a better way to show the result.
//				try {
//					Thread.sleep(10000);
//					finish();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				btn_ok.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						logUtils.d(tag, "btn_ok");
						finish();
					}
				});
				
				btn_cancel.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						logUtils.d(tag, "btn_cancel");
					
						finish();
					}
				});
				
				break;
			case 0: // Validation failed.
				tv_message.setText(getString(R.string.app_failed));
				btn_cancel.setText(getString(R.string.btn_delete));
				btn_ok.setText(getString(R.string.btn_reserve));
				btn_ok.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						logUtils.d(tag, "No operation");
						finish();
					}
				});
				
				btn_cancel.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						File file = new File(filePath);
						if(file.exists()) {
							file.delete();
						
							logUtils.d(tag, "File deleted");
						}
						finish();
					}
				});
				
				
				/*Builder builder = new AlertDialog.Builder(ApkValidate.this);
				builder.setTitle(R.string.app_validate_title);
				builder.setIcon(android.R.drawable.ic_dialog_alert);
				builder.setMessage(R.string.app_failed);
				builder.setPositiveButton(R.string.btn_delete, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						File file = new File(filePath);
						if(file.exists()) {
							file.delete();
						
							logUtils.d(tag, "File deleted");
						}
						finish();
					}
				});
				builder.setNegativeButton(R.string.btn_ok, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						logUtils.d(tag, "No operation");
						finish();
					}
				});
				builder.create();
				builder.show();
				break;*/
			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_apk_validate); // Set contentView.
		
		Bundle bundle = getIntent().getExtras();
		logUtils.d(tag, filePath);
		
		filePath = bundle.getString("filepath");
		
		final Message msg = Message.obtain();
		
		final Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				boolean flag = apkOperation.ApkCheckInfo(apkCheckUrl, filePath);
				if(flag) {
					msg.what = 1; // The apk passed validation.
				} else {
					msg.what = 0; // The apk failed validation.
				}
				handler.sendMessage(msg);
			}
		});
		
		thread.start(); // Start the thread.
		
		layout=(LinearLayout)findViewById(R.id.apk_validate_layout); // Find the layout.
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_ok = (Button) findViewById(R.id.btn_ok);
		tv_message = (TextView) findViewById(R.id.tx_message);
		
		// Optional
		layout.setOnClickListener(new View.OnClickListener() { // Set layout click listener.

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(),
						R.string.click_error,
						Toast.LENGTH_SHORT).show();
			}
		});
				
		btn_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				logUtils.d(tag, "Canceled");
				thread.stop();
				finish();
			}
		});
				
		btn_ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				logUtils.d(tag, "No operation");
			}
		});
	}

	// Finish this activity when hit other place.
	public boolean onTouchEvent(MotionEvent event){
//		finish();
		return true;
	}
	
}