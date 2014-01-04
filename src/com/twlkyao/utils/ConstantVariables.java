package com.twlkyao.utils;

import android.R.integer;

public class ConstantVariables {
	
	/**
	 * AVD base address
	 */
	final public static String BASE_URL= "http://10.0.2.2/"; //Set the login url; 

	/**
	 * Real world base address
	 */
//	final public static String BASE_URL= "http://219.245.80.97/"; //Set the login url
	
	final public static String REGISTER_URL = "cloud/android/register.php"; // The location of the register PHP script
	final public static String LOGIN_URL = "cloud/android/login.php"; // The location of the login PHP script
	final public static String UPLOAD_FILE_INFO_URL = "cloud/android/data_info.php"; // The locaion of the file information PHP script
	final public static String UPLOAD_FILE_URL = "cloud/storage/file_operation.php"; // The location of the file upload PHP script
	final public static String CHECK_FILE_INFO_URL = "cloud/android/check_data_info.php"; // The locaion of the file information PHP script


	final public int operation_succeed= 1; // Indicating that the operation is succeeded.
	final public int operation_failed = 0; // Indicating that the operation is failed.
	
	final public int upload_file = 1; // Indicating the type is file upload.
	final public int upload_file_info = 0; // Indicating the type is file info upload.
	
	public int encrypt_file = 3; // Indicating the type is encrypt file.
	public int decrypt_file = 2; // Indicating the type is decrypt file.
	
	final public static String charset = "UTF-8";
	
	/**
	 * DES THe length of DES algorithm is 64bits, 56bits are alid.
	 * DESede The length of DESede algorithm is 192bits.
	 * Blowfish The length of Blowfish algorithm is 8bits to 448bits.
	 */
	
	final public static String[] algorithms = {
			"DESede", "DES", "Blowfish"
	};
	
	public static String[] keys = {
		"12345678abcdefgh12345678",
		"12345678",
		"12345678abcdefgh1234567812345678abcdefgh1234567812345678abcdefgh12345678"

	};
}