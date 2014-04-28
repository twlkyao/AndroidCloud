<?php
    header("Content-Type: text/html; charset=utf-8") ;

    session_start(); // start the seesion
    
    /*mobile version */
    //$username = htmlspecialchars($_POST["username"]);
    $username = $_POST["username"];
    $password=$_POST["password"];
    $email=$_POST["email"];
    
    $password = md5($password); // md5 the $password

    /*web version*/
    //$username = htmlspecialchars(_get("username"));
    //$password=_get("password");

    // include the database connect php script
    include_once('conn_member.php');

    // check whether the username and the password are correct
    $mysql_select = "SELECT uid ,username FROM $tableName WHERE username='$username'"; // define the select sql query string
    $select_result= mysql_query($mysql_select)
        or die("Could not execute the SELECT operation! ".mysql_error()."<br>"); // get the SELECT result
    $result = mysql_fetch_array($select_result); // get the query result into array 
    
    //必须放在if之前，否则else无法使用变量
    //$result_array =array(); // define an empty array to store data
    
    $sessionid = session_id();

    if(!is_array($result)){ // the user to register is not in the database
         
        // no same user
        $mysql_insert = "INSERT INTO $tableName (username, password, email) VALUES('$username', '$password', '$email')";
		
        mysql_query($mysql_insert) 
            or die("Could not INSER INTO the $tableName table! ".mysql_error()."<br>"); // execute the INSERT operation
        
        $_SESSION['username'] = $username;	// set $username as the username
        //$_SESSION['uid'] = $result['uid'];    		// set SESSION['uid'] as the uid
        $_SESSION['$sessionid'] = $sessionid;	// set SESSION['$sessionid'] as the session id
        
        $result_array = array(  
            'flag'=>'success',
            'username'=>$username,  
        //    'userid'=>$result['uid'],  
            'sessionid'=>$sessionid  
        ); 
        echo json_encode($result_array); 
        
    } else { // the user is already in the database
        
        $_SESSION['username'] = $result['user_name']; 	// set $result['user_name'] as the username
        $_SESSION['uid'] = $result['uid'];			// set SESSION['uid'] as the uid
        $_SESSION['$sessionid'] = $sessionid;	// set SESSION['$sessionid'] as the session id
        
        $result_array = array(  
            'flag'=>'fail', 
            'username'=>$username,  
        //    'userid'=>$result['uid'],  
            'sessionid'=>$sessionid 
        ); 
        echo json_encode($result_array);  
    }

    mysql_close($mysql_handle); // close the connection
?>
