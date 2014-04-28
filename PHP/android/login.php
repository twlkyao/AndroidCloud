<?php

    header("Content-Type: text/html; charset=utf-8") ; // set the php encoding type to utf8
    
    session_start(); // start a session
    
    //$Action = isset($_GET["action"]) ? $_GET["action"] : null; // judge if the $_GET["action"] is set
    
	 // include the mysql connection php script
    include_once('conn_member.php');
	
    /*mobile version */
	//  $username = htmlspecialchars($_POST["username"]);
    $username = $_POST['username']; // get username
    $password=$_POST['password']; // get password

    /*website version*/
    //$username = htmlspecialchars(_get("username"));
    //$password=_get("password");

    /**
    // check whether the username and the password are correct
    $msyql_select = "SELECT uid ,username, password FROM $tableName
        WHERE username='$username' AND password='$password' LIMIT 1"; // define the select sql query string
    */
    


     // check whether the username and the password are correct
    $msyql_select = "SELECT uid, username, password FROM $tableName
        WHERE username='$username'"; // define the select sql query string
    $select_result = mysql_query($msyql_select); // get the query result
    $result = mysql_fetch_array($select_result); // get the query result into array
    
    //$result_array = array(); // empty array to store the result
    
    $sessionid=session_id(); // get the session id
    
    
    if(is_array($result)) { // result is an array
        if(md5($password) == $result['password']) { // password pass
            
            //login succeeded
            $_SESSION['username'] = $username; // set $username as the username
            //$_SESSION['uid'] = $result['uid']; // set SESSION['uid'] as the uid
            $_SESSION['$sessionid'] = $sessionid; // set SESSION['$sessionid'] as the session id
            
            $result_array  = array(  
                'flag'=>'success',
                'username'=>$username,  
                'uid'=>$result['uid'],  
                'sessionid'=>$sessionid
            ); // set the array value, especially the flag
            
            echo json_encode($result_array); // encode $arr into json type
                
        } else {
            
             //login succeeded
            $_SESSION['username'] = $username; // set $username as the username
            $_SESSION['uid'] = $result['uid']; // set SESSION['uid'] as the uid
            $_SESSION['$sessionid'] = $sessionid; // set SESSION['$sessionid'] as the session id
            $result_array  = array(  
            'flag'=>'fail',
            'username'=>$username,  
            'uid'=>$result['uid'],  
            'sessionid'=>$sessionid 
        ); // set the array value, especially the flag
        
        echo json_encode($result_array); // encode $arr into json type
        }
    }
     
    
    $arr=array(); // empty array
    
    //必须放在if之前，否则else无法使用变量
    // must put befor if sentence

    mysql_close($mysql_handle); // close the connection

?>
