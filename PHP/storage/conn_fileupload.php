<?php
	/*****************************
    *Connect the database
    *****************************/
    $serverAddress = "localhost";       // database server address
    $databaseUser = "root";               // database username
    $databasePassword = "jack";        // database password
    //$prefix = "users_";                     // database prefix
    $databaseName = "file_upload_data";            // database name
    $tableName = "file_upload";                   // table name
    
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
    file_id: file id
    user_id: user id
    file_path: the filepath
    file_name: the filename
	primary key: file_id, user_id, file_path, file_sha1
    charset: utf8
    */
    $mysql_create_table = "CREATE TABLE IF NOT EXISTS $tableName (
        file_id int(20) AUTO_INCREMENT, 
        user_id int(20),
        file_path varchar(256),
        file_md5 varchar(32),
        file_sha1 varchar(40),
        encrypt_level varchar(3),
        encrypt_key varchar(40),
        PRIMARY KEY(file_id, user_id, file_md5, file_sha1)                
    )charset=utf8"; // Define the create table sql
    //PRIMARY KEY(name, md5, sha1)
                    
     mysql_query($mysql_create_table, $mysql_handle)
        or die("Could not create the $tableName table!".mysql_error()."<br>"); // Execute the create table sql
    
    // Write into the database
    mysql_query("set names 'UTF-8'");
?>
