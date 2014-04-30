package com.twlkyao.androidcloud;


import com.twlkyao.dao.DaoMaster.DevOpenHelper;
import com.twlkyao.utils.LogUtils;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.NumberPicker.OnValueChangeListener;

import com.twlkyao.dao.*;

import com.twlkyao.dao.DaoMaster;

public class SetEncryptLevelActivity extends Activity {
	
	private boolean DEBUG = false;
	private String TAG = "SetEncryptLevel"; // The log tag.
	private LogUtils logUtils = new LogUtils(DEBUG, TAG);
	
	private NumberPicker numberPicker; // The numberpicker.
	private Button btnOK; // The ok button.
	private Button btnCancel; // The cancel button.
	private String md5String; // The md5 value from the MainActivity.
	private String sha1String; // The sha1 value from the MainActivity.
	private String fileParentPath; // The file path of the clicked file's parent.
	
	private DevOpenHelper helper;
	private SQLiteDatabase db; // The database.
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private FileInfoDao fileInfoDao;

 	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_encrypt_level);
		
		initDatabase(); // Initialize the database.
		findViews(); // Find the views.
		setListeners(); // Set listeners.
	}
	
	/**
	 * Init the database.
	 */
	public void initDatabase() {
		helper = new DaoMaster.DevOpenHelper(SetEncryptLevelActivity.this, "encrypt_level_db", null);
        db = helper.getWritableDatabase(); // Create or open a database.
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        fileInfoDao = daoSession.getFileInfoDao();
	}
	/**
	 * Find the views by id.
	 */
	public void findViews() {
		numberPicker = (NumberPicker) this.findViewById(R.id.number_picker);
		numberPicker.setMinValue(1); // Set the minimum value for number picker.
		numberPicker.setMaxValue(5); // Set the maximum value for numer picker.
		numberPicker.setValue(1); // Set default value for number picker.
		btnOK = (Button) this.findViewById(R.id.btn_ok);
		btnCancel = (Button) this.findViewById(R.id.btn_cancel);
	}
	
	/**
	 * Set listeners for the buttons.
	 */
	public void setListeners() {
		
		// Set listeners for OK button.
		btnOK.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// Get the md5 and sha1 value passed from the MainActivity.
				Intent intent = getIntent();
				Bundle bundle = intent.getExtras();
				md5String = bundle.getString("md5");
				sha1String = bundle.getString("sha1");
				fileParentPath = bundle.getString("fileParentPath");
				
				// Get the encrypt level as a string.
				String encrypt_level = Integer.toString(numberPicker.getValue());
				
				// Instantiate a object.
				FileInfo fileInfo = new FileInfo(null, md5String, sha1String, encrypt_level);
				
				fileInfoDao.insertOrReplace(fileInfo); // Inset or replace the entity.
				helper.close(); // Close the database.
				
				logUtils.d(TAG, "Inserted new fileInfo!");
//				setResult(RESULT_OK); // The operation is succeeded.
				
				
				intent.putExtra("fileParentPath", fileParentPath);
				setResult(RESULT_OK, intent); // The operation is succeeded.
				
				finish(); // Finish itself.
			}
		});
		
		// Set listeners for Cancel button.
		btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(RESULT_CANCELED); // The operation is canceled.
				finish(); // Finish itself.
			}
		});
		
		// Set listeners for NumberPicker.
		numberPicker.setOnValueChangedListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				// TODO Auto-generated method stub
				Resources res = getResources();
				String[] encrypt_levels = res.getStringArray(R.array.encrypt_level);
				
				Toast.makeText(getApplicationContext(),
						encrypt_levels[newVal - 1],
						Toast.LENGTH_SHORT).show();
			}
		});
	}
}
