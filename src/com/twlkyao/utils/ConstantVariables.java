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
	
	final public static String REGISTER_URL = "cloud/android/register.php"; // The location of the register PHP script
	final public static String LOGIN_URL = "cloud/android/login.php"; // The location of the login PHP script
	final public static String FILE_INFO_UPLOAD_URL = "cloud/android/data_info.php"; // The locaion of the file information PHP script
	final public static String FILE_UPLOAD = "cloud/storage/file_operation.php"; // The location of the file upload PHP script
	
	final public int operation_succeed= 1; // Indicating that the operation is succeeded.
	final public int operation_failed = 0; // Indicating that the operation is failed.
	
	final public int file_upload = 1; // Indicating the type is file upload.
	final public int file_info_upload = 0; // Indicating the type is file info upload.
	
	public String charset = "UTF-8";
}