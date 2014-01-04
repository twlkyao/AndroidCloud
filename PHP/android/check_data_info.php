<?php
    header("Content-Type: text/html; charset=utf-8") ;

    session_start(); // start the seesion
    
    /*mobile version */
    //$username = htmlspecialchars($_POST["username"]);
	$user_id = $_POST["user_id"];            // The file owner's id
	$file_md5 = $_POST["file_md5"];		// file md5 value	
	$file_sha1 = $_POST["file_sha1"];		// file sha1 value
	

    /*web version*/
    //$username = htmlspecialchars(_get("username"));
    //$password=_get("password");

    // include the database connect php script
    include_once('conn_data_info.php');

    // check whether the username and the password are correct
    $mysql_select = "SELECT user_id , file_md5, file_sha1, encrypt_level, encrypt_key
		FROM $tableName WHERE user_id = '$user_id' AND file_md5 = '$file_md5' AND file_sha1 = '$file_sha1'"; // define the select sql query string
    $select_result= mysql_query($mysql_select)
        or die("Could not execute the SELECT operation! ".mysql_error()."<br>"); // get the SELECT result
    $result = mysql_fetch_array($select_result); // get the query result into array 
        
    
    $sessionid = session_id();	// Get the session id

    if(!is_array($result)){ // The file information is not in the database, that is the md5 and sha1 checksum is not identical.
        
        $result_array = array(  
            'flag' => 'false',
            'sessionid'=>$sessionid  
        ); 
        echo json_encode($result_array);	// Return the result encoded in JSON.
        
    } else { // The file information is already in the database and the md5 and sha1 checksum is identical.
	   
        $result_array = array(  
			'flag' => 'true',
			'encrypt_level' => $result['encrypt_level'],
			'encrypt_key' => $result['encrypt_key'],
            'sessionid'=>$sessionid
        ); 
        echo json_encode($result_array); // Return the result encoded in JSON.
    }

    mysql_close($mysql_handle); // close the connection
?>
