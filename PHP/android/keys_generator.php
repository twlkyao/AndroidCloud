<?php
	/**
	The key is generated according to
	the base_key(passed by user) and
	the generated key related with time.
	If the key is not exist, generate it, 
	or return it when it is exist.
	*/
	
    header("Content-Type: text/html; charset=utf-8");

    session_start(); // start the seesion
    
	$encrypt_level = $_POST["encrypt_level"]; // The encrypt level of the file.
	
	// Include the key generator php script.
	include_once('conn_keys_generator.php');
	
	$key_length = $encrypt_key_length[$encrypt_level]; // Get the encrypt key length.
	
	$key_set_size = count($encrypt_key_set); // Get the size of key set.
	
	// Join the random chars to get the encrypt key. 
	for($i = 0; $i < $key_length; $i++) {
		$index = rand(0, $key_set_size); // Get the random index.
		$encrypt_key .= $encrypt_key_set[$index]; // Join the random chars to the $encrypt_key.
	}
	
	        
    //$sessionid = session_id();	// Get the session id.
        
    // Encode the result to JSON.
    $result_array = array(  
        'flag' => 'success',
		'encrypt_key' => $encrypt_key
       // 'sessionid'=>$sessionid  
    ); 
    echo json_encode($result_array);	// Return the result encoded in JSON
        
   
?>
