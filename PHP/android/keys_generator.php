<?php
	/**
	The key is generated according to
	the base_key(passed by user) and
	the generated key related with time.
	If the key is not exist, generate it, 
	or return it when it is exist.
	*/
	
    header("Content-Type: text/html; charset=utf-8") ;

    session_start(); // start the seesion
    
	/**
	key_id: The id of the key
    file_id: file id: The id of the file
    user_id: user id: The id of the user
	key: The generated key
	primary key: key_id, file_id, user_id
    charset: utf8
    */
	
    /*mobile version */
	$file_id = $_POST["file_id"];				// The key id
	$user_id = $_POST["user_id"];			// The user id indicate who owns the file
	$base_key = $_POST["base_key"]; // The key passed by user
	
    // include the database connect php script
    include_once('conn_key_generator.php');

    // check whether the username and the key are correct
    $mysql_select = "SELECT file_id, user_id, key FROM $tableName WHERE file_id = '$file_id' AND user_id = '$user_id'"; // define the select sql query string
    $select_result= mysql_query($mysql_select)
        or die("Could not execute the SELECT operation! ".mysql_error()."<br>"); // get the SELECT result
    $result = mysql_fetch_array($select_result); // get the query result into array 
    
    $sessionid = session_id();	// Get the session id

    if(!is_array($result)){ // The key information is not in the database
		
		$generated_key = time();			// Get the system time measured in seconds
		
		echo "time:" . $generated_key . "<br>";
	
		$key = $base_key + $generated_key;	// Splice the base_key and the generated key
		$key = md5($key);		// Get the key
		
        $mysql_insert = "INSERT INTO $tableName (file_id, user_id, key) VALUES('$file_id', '$user_id', '$key)";	// Define the insert sql query string
        mysql_query($mysql_insert) 
            or die("Could not INSER INTO the $tableName table! ".mysql_error()."<br>"); // execute the INSERT operation
			
        $_SESSION['user_id'] = $result['user_id'];    
        $_SESSION['$sessionid'] = $sessionid;
        
        $result_array = array(  
            'flag' => 'generate',
			'file_id' => $result['file_id'],
            'user_id'=> $user_id, 
			'key' => $key,
			
            'sessionid'=>$sessionid  
        ); 
        echo json_encode($result_array);	// Return the result encoded in JSON
        
    } else { // The file information is already in the database, update the file information
		
		$_SESSION['user_id'] = $result['user_id'];    
        $_SESSION['$sessionid'] = $sessionid;
        
        $result_array = array(  
			'flag' => 'exist',
			'file_id' => $result['file_id'],
            'user_id'=> $result['user_id'],  
			'key' => $result['key'],
			
            'sessionid'=>$sessionid
        ); 
        echo json_encode($result_array);  
    }

    mysql_close($mysql_handle); // close the connection
?>