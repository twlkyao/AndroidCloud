<?php
    /*****************************
    *数据库连接
    *****************************/
    $serverAddress = "localhost";       // database server address
    $databaseUser = "root";               // database username
    $databasePassword = "jack";        // database password
    //$prefix = "users_";                     // database prefix
    $databaseName = "file_data";            // database name
    $tableName = "encrypt_key";                   // table name
    
    $mysql_handle = mysql_connect($serverAddress, $databaseUser, $databasePassword)
        or die("Cann't connect to the database server!". mysql_error()."<br>"); // connect to the database server
    
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
	password_id: The id of the password
    file_id: file id: The id of the file
    user_id: user id: The id of the user
	encrypt_key: The generated encrypt_key
	primary key: password_id, file_id, user_id
    charset: utf8
    */
    $mysql_create_table = "CREATE TABLE IF NOT EXISTS $tableName (
        password_id int(20) AUTO_INCREMENT, 
		file_id int(20),
        user_id int(20),
		encrypt_key varchar(32),
        PRIMARY KEY(password_id, file_id, user_id)                
    )charset=utf8"; // Define the create table sql
    //PRIMARY KEY(name, md5, sha1)
                    
     mysql_query($mysql_create_table, $mysql_handle)
        or die("Could not create the $tableName table!".mysql_error()."<br>"); // Execute the create table sql
    
    //写库
    mysql_query("set names 'UTF-8'");
    
    /**
    //字符转换，读库
    mysql_query("set character set 'utf-8'");    
    */
?>
