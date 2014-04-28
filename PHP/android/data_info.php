<?php
    header("Content-Type: text/html; charset=utf-8") ;

    session_start(); // start the seesion
    
    /*mobile version */
    //$username = htmlspecialchars($_POST["username"]);
	$user_id = $_POST["user_id"];            // The file owner's id
	$file_md5 = $_POST["file_md5"];		// file md5 value	
	$file_sha1 = $_POST["file_sha1"];		// file sha1 value
	$encrypt_level = $_POST["encrypt_level"];	// file encrypt level
	$encrypt_key = $_POST["encrypt_key"];		// file encrypt key

    /*web version*/
    //$username = htmlspecialchars(_get("username"));
    //$password=_get("password");

    // include the database connect php script
    include_once('conn_data_info.php');

    // check whether the username and the password are correct
    $mysql_select = "SELECT file_id, user_id , file_md5, file_sha1, encrypt_level, encrypt_key 
		FROM $tableName WHERE user_id = '$user_id' AND file_md5 = '$file_md5' AND file_sha1 = '$file_sha1'"; // define the select sql query string
    $select_result= mysql_query($mysql_select)
        or die("Could not execute the SELECT operation! ".mysql_error()."<br>"); // get the SELECT result
    $result = mysql_fetch_array($select_result); // get the query result into array 
        
    
    $sessionid = session_id();	// Get the session id

    if(!is_array($result)){ // The file information is not in the database.
		
        $mysql_insert = "INSERT INTO $tableName (file_id, user_id, file_md5, file_sha1, encrypt_level, encrypt_key)
			VALUES('$file_id', '$user_id', '$file_md5', '$file_sha1', '$encrypt_level', '$encrypt_key')";	// Insert string
        mysql_query($mysql_insert) 
            or die("Could not INSER INTO the $tableName table! ".mysql_error()."<br>"); // execute the INSERT operation
        
        $_SESSION['user_id'] = $result['user_id'];    
        $_SESSION['$sessionid'] = $sessionid;
        
        $result_array = array(  
            'flag' => 'insert',
			'file_id' => $result['file_id'],
            'user_id'=> $user_id,  
			'file_md5' => $file_md5,
			'file_sha1' => $file_sha1,
			'encrypt_level' => $encrypt_level,
			'encrypt_key' => $encrypt_key,
            'sessionid'=>$sessionid  
        ); 
        echo json_encode($result_array);	// Return the result encoded in JSON
        
    } else { // The file information is already in the database, update the file information
		
        $mysql_update = "UPDATE $tableName SET encrypt_level ='$encrypt_level', encrypt_key = '$encrypt_key'
			WHERE file_md5 = '$file_md5' AND file_sha1 = '$file_sha1' AND user_id = '$user_id'";	// Update string
				
        mysql_query($mysql_update) 
            or die("Could not INSER INTO the $tableName table! ".mysql_error()."<br>"); // execute the IUPDATE operation
		
        $_SESSION['$sessionid'] = $sessionid;
        
        $result_array = array(  
			'flag' => 'update',
			'file_id' => $result['file_id'],
            'user_id'=> $result['user_id'],  
			'file_md5' => $result['file_md5'],
			'file_sha1' => $result['file_sha1'],
			'encrypt_level' => $encrypt_level,
			'encrypt_key' => $encrypt_key,
            'sessionid'=>$sessionid
        ); 
        echo json_encode($result_array);  
    }

    mysql_close($mysql_handle); // close the connection
?>
