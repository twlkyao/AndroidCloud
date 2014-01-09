<?php
    /*****************************
    *数据库连接
    *****************************/
    $serverAddress = "localhost"; //database server address
    $databaseUser = "root"; // database username
    $databasePassword = "jack"; // database password
    //$prefix = "users_"; // database prefix
    $databaseName = "member"; // database name
    $tableName = "users";
    
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
    uid: user id
    username: username
    password: user password
    email: user email
	primary key: uid, username
	charset: utf8
    */
    $mysql_create_table = "CREATE TABLE IF NOT EXISTS $tableName (
        uid int(20) AUTO_INCREMENT, 
        username varchar(40) NOT NULL,
        password varchar(32) NOT NULL,
        email varchar(40) NULL,
        PRIMARY KEY(uid, username)                
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
