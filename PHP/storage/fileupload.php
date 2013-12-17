<?php  

	header("Content-Type: text/html; charset=utf-8") ;

    session_start(); // start the seesion
    
    /*mobile version */
    //$username = htmlspecialchars($_POST["username"]);
    $file_md5 = $_POST["file_id"];		// file md5 value
	$user_id = $_POST["user_id"];            // The file owner id
	$file_name = $_POST["file_name"];	// The name of the file
	$file_path = $_POST["file_path"]; 	// The path of the local file	

    /*web version*/
    //$username = htmlspecialchars(_get("username"));
    //$password=_get("password");

    // include the database connect php script
    include_once('conn_fileupload.php');

/**
 * 以下内容未作修正
 * /
    // check whether the username and the password are correct
    $mysql_select = "SELECT file_id, user_id , file_md5, file_sha1, encrypt_level, encrypt_key 
		FROM $tableName WHERE user_id = '$user_id' AND file_md5 = '$file_md5' AND file_sha1 = '$file_sha1'"; // define the select sql query string
    $select_result= mysql_query($mysql_select)
        or die("Could not execute the SELECT operation! ".mysql_error()."<br>"); // get the SELECT result
    $result = mysql_fetch_array($select_result); // get the query result into array 
    
    //echo $select_result;
    
    //必须放在if之前，否则else无法使用变量
    //$result_array =array(); // define an empty array to store data
    
    
    $sessionid = session_id();	// Get the session id

    if(!is_array($result)){ // the file information is not in the database
		
        $mysql_insert = "INSERT INTO $tableName (file_id, user_id, file_md5, file_sha1, encrypt_level, encrypt_key) VALUES('$file_id', '$user_id', '$file_md5', '$file_sha1', '$encrypt_level', '$encrypt_key')";	// Insert string
        mysql_query($mysql_insert) 
            or die("Could not INSER INTO the $tableName table! ".mysql_error()."<br>"); // execute the INSERT operation
        
        $_SESSION['user_id'] = $result['user_id'];    
        $_SESSION['$sessionid'] = $sessionid;
	$target_path  = "./upload/"; // The folder to storage the file  
	$target_path = $target_path . basename( $_FILES['uploadedfile']['name']);  
	if(move_uploaded_file($_FILES['uploadedfile']['tmp_name'], $target_path)) {  
	   echo "The file ".  basename( $_FILES['uploadedfile']['name']). " has been uploaded";  
	}  else{  
	   echo "There was an error uploading the file, please try again!" . $_FILES['uploadedfile']['error'];  
	}  
?>  
