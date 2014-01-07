package com.twlkyao.utils;

public class ConstantVariables {
	
	/**
	 * AVD base address
	 */
	final public static String BASE_URL= "http://10.0.2.2/"; //Set the login url; 

	/**
	 * Real world base address
	 */
//	final public static String BASE_URL= "http://219.245.80.97/"; //Set the login url
	
	// The string of website.
	final public static String REGISTER_URL = "cloud/android/register.php"; // The location of the register PHP script
	final public static String LOGIN_URL = "cloud/android/login.php"; // The location of the login PHP script
	final public static String UPLOAD_FILE_INFO_URL = "cloud/android/data_info.php"; // The locaion of the file information PHP script
	final public static String UPLOAD_FILE_URL = "cloud/storage/file_operation.php"; // The location of the file upload PHP script
	final public static String CHECK_FILE_INFO_URL = "cloud/android/check_data_info.php"; // The locaion of the file information PHP script
	final public static String RETRIEVE_ENCRYPT_KEY = "cloud/android/keys_generator.php"; // The location of the keys generator PHP script.
	
	// The SharedPreferences name and key of base key.
	public String PREF_NAME = "base_key"; // The SharedPreferences name. of the base key.
	public String PREF_KEY = "base_key_key"; // The SharedPreferences key of the base key.
	
	// The SharedPreferences name and key of encrypt level
	public String ENCRYPT_LEVEL_PREF_NAME = "encrypt_level"; // The SharedPreferences name of the encrypt level.
	public String ENCRPT_LEVEL_PREF_KEY = "encrypt_level_key"; // The SharedPreferences key of the encrypt level.
	
	final public int operation_succeed= 1; // Indicating that the operation is succeeded.
	final public int operation_failed = 0; // Indicating that the operation is failed.
	
	final public int upload_file = 1; // Indicating the type is file upload.
	final public int upload_file_info = 0; // Indicating the type is file info upload.
	
	final public int encrypt_file = 3; // Indicating the type is encrypt file.
	final public int decrypt_file = 2; // Indicating the type is decrypt file.
	
	public static String charset = "UTF-8";
	
	/**
	 * DES THe length of DES algorithm is 64bits, 56bits are valid.
	 * DESede The length of DESede algorithm is 192bits.
	 * Blowfish The length of Blowfish algorithm is 8bits to 448bits.
	 */
	
	public String[] algorithms = {
			"DESede", "AES"
	};
	
	public String[] keys = {
		"12345678abcd",
		"12345678"

	};
	
	public String[] packageNames = {
		"com.owncloud.android",
		"com.qq.qcloud"
//		"com.baidu.netdisk",
	};
	
	public String[] classNames= {
		"com.owncloud.android.ui.activity.FileDisplayActivity",
		"com.qq.qcloud.LaunchActivity"
//		"com.baidu.netdisk.ui.Navigate",
	};
  }