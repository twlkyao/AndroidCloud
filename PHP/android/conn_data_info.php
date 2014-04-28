<?php
    /*****************************
    *Connect the database
    *****************************/
    $serverAddress = "localhost";       // database server address
    $databaseUser = "root";               // database username
    $databasePassword = "jack";        // database password
    //$prefix = "users_";                     // database prefix
    $databaseName = "file_data_info";            // database name
    $tableName = "files";                   // table name
    
    $mysql_handle = mysql_connect($serverAddress, $databaseUser, $databasePassword)
        or die("Cann't connect to the database server!". mysql_error()."<br>"); // connect to the database server
    mysql_query("set character set 'utf8'");//读库 
	mysql_query("set names 'utf8'");//写库 
					
    /**
    Create the database
    */
    // Define the sql create database string for use
    $mysql_create_database = "CREATE DATABASE IF NOT EXISTS $databaseName"; 
                    
    // Execute the create database sql
    mysql_query($mysql_create_database)
        or die("Could not create the $databaseName database!".mysql_error()."<br>");
                     
    /**
    Select the database
    */
    mysql_select_db($databaseName, $mysql_handle)
        or die("Could not select the $databaseName database!".mysql_error()."<br>");
    
     /**
    Create the table
    */
    /**
    file_id: file id
    user_id: user id
    file_md5: file md5
    file_sha1: file sha1
	encrypt_level: encrypt level
	encrypt_key: encrypt key
	primary key: file_id, user_id, file_md5, file_sha1
    charset: utf8
    */
    $mysql_create_table = "CREATE TABLE IF NOT EXISTS $tableName (
        file_id int(20) AUTO_INCREMENT, 
        user_id int(20),
        file_md5 char(32),
        file_sha1 char(40),
        encrypt_level int(1),
        encrypt_key varchar(40),
        PRIMARY KEY(file_id, user_id, file_md5, file_sha1)                
    )charset=utf8"; // Define the create table sql
    //PRIMARY KEY(name, md5, sha1)
                    
     mysql_query($mysql_create_table, $mysql_handle)
        or die("Could not create the $tableName table!".mysql_error()."<br>"); // Execute the create table sql
    
    // Write into the database
    mysql_query("set names 'UTF-8'");
    
    /**
    //Change the charset
    mysql_query("set character set 'utf-8'");    
    */
?>
