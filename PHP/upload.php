<!--
    作者： 齐士垚
    日期： 2013.11.18
    功能： 处理文件上传，文件审核通过后，将上传文件保存在本地文件夹。
    Author: Qi Shiyao
    Data:   2013.11.18
    Function:   Handle the uploaded file, if the file passed the audit, then store the file into specified folder.
-->
<html>
    <head>
        <title>文件上传</title>
        <meta http-equiv="Content-Type" content="text/html;
            charset=UTF-8">
    </head>
        <body>
            <?php
                //Specify the folder and the name of the file to store
                $upload_dir = getcwd()."/upload_files/"; //Construct the filepath according to the current working directory
                //$upload_file = $upload_dir.iconv("UTF-8", "Gb2312", $_FILES["myfile"]["name"]); //Change the code type of the filename
                $upload_file = $upload_dir. $_FILES["myfile"]["name"]; //Construct the filepath to save the file
                
                $mysql_handle; //The handle of the MySQL database
                $serverAddress = "localhost"; //The address of the MySQL server
                $username = "root"; //The username of the MySQL server
                $password = "jack"; //The password of the MySQL server
                $databaseName="software"; //The name of the database
                $tableName = "softwareInfo"; //The name of the softwareInfo table
                
                //Move the temp file into the specified filepath
                if(0 == $_FILES["myfile"]["error"]) {

                    echo "<strong>文件上传成功！</strong><hr>";
                    
                    //Show the file infomation
                    echo "文件名：".$_FILES["myfile"]["name"]."<br>";
                    echo "临时保存文件名：".$_FILES["myfile"]["tmp_name"]."<br>";
                    echo "文件大小：" .($_FILES["myfile"]["size"]/1024)."KB<br>";
                    echo "文件种类：" . $_FILES["myfile"]["type"] . "<br>";
                    
                    if (file_exists($upload_file)) {   //文件已经存在
                        echo $_FILES["myfile"]["name"] . "已经存在！";
                    //    echo "软件已经通过审核！<br>";
                    } else { //文件不存在
                        move_uploaded_file($_FILES["myfile"]["tmp_name"], $upload_file);
                        echo "文件保存路径：" .$upload_file."<br>";
                    //    echo "软件正在审核中，请耐心等候！<br>";
                        
                    /**
                    Store the file information into the database
                    */
                    //Connect to the database server
                    $mysql_handle = mysql_connect($serverAddress, $username, $password)
                        or die("Could not connect to the database server!".mysql_error()."<br>"); //Could not connected to the database server
                    
                    /**
                    2013.11.20
                    //Select the database to work with
                    $selected = mysql_select_db($databaseName, $mysql_handle)
                        or die("Could not select $databaseName!<br>"); //Could not connect database
                    */
                    //If the database not exit, create it
                    /**
                    $sql_create = "Create data"; //Create database
                    if(!$selected) {
                        mysql_query();
                    }
                    */
                    /**
                    2013.11.20
                    // Indicate the status
                    if($selected) {
                        echo "connect success!<br>";
                        echo var_dump($_FILES['myfile']['name'])."<br>";
                    }
                    */
                    
                    //Get the upload file name to work with, in case that the sql not recognize it
                    
                    $filename = strval($_FILES['myfile']['name']);
                    $md5_value = md5_file($upload_file);
                    $sha1_value = sha1_file($upload_file);
                    
                    /**
                    Create the database if not exists
                    */
                    // Define the sql create database string for use
                    $mysql_create_database = "CREATE DATABASE IF NOT EXISTS $databaseName"; 
                    
                    // Execute the create database sql
                    mysql_query($mysql_create_database)
                        or die("Could not create the $databaseName database!");
                     
                    /**
                    Select the database
                    */
                    mysql_select_db($databaseName, $mysql_handle)
                        or die("Could not select the $databaseName database!");
                    
                    /**
                    Create the table
                    */
                    /**
                    id: software id
                    name: software name
                    version: software version
                    md5: software md5
                    sha1: software sha1
                    charset: utf8
                    */
                    $mysql_create_table = "CREATE TABLE IF NOT EXISTS $tableName (
                        id int(10) AUTO_INCREMENT, 
                        name varchar(40),
                        version varchar(40),
                        md5 varchar(32),
                        sha1 varchar(40),
                        PRIMARY KEY(id, name)                
                    )charset=utf8"; // Define the create table sql
                    //PRIMARY KEY(name, md5, sha1)
                    
                    mysql_query($mysql_create_table, $mysql_handle)
                        or die("Could not create the $tableName table!"); // Execute the create table sql
                    
                    /**
                    Judge if the software already in the table
                    */
                    /**
                    2013.11.20
                    $mysql_select_md5 = "SELECT md5 FROM $tableName"; // Define the select md5 value sql query string for use
                    $select_md5_result = mysql_query($mysql_select_md5); // Get the md5 value from the table
                    
                    $mysql_select_sha1 = "SELECT sha1 FROM $tableName"; // Define the select sha1 value sql query string for use
                    $select_sha1_result = mysql_query($mysql_select_sha1); // Get the sha1 value from the table
                    */
                    
                    /**
                    Insert the values into the softwareInfo table
                    */
                    //Define the sql insert string for use
                    $sql_insert="INSERT IGNORE INTO $tableName (name, version, md5,sha1)
                        VALUES ('$filename','1', '$md5_value', '$sha1_value')"; // The value string to be inserted into the database on condition that there are no records
                    
                    //echo $sql_insert."<br>";
                    
                    mysql_query($sql_insert)
                        or die("Could not insert into the $tableName table"); // Execute the insert function
                   
                    
                    // Echo the table content
                    echo "<br><strong>The content of the table $tableName is as followed:</strong><br>";
                    
                    /**
                    Select the values of the softwareInfo table
                    */
                    //Define the sql select string for use
                    $sql_select = "SELECT * from $tableName";
                    
                    //Execute the SQL query and return records
                    $result = mysql_query($sql_select)
                        or die(mysql_error()); //Get the select records

                    //Fetch tha data from the database 
                    while ($row = mysql_fetch_array($result)) {
                        echo "id：".$row{'id'}." name：".$row{'name'}
                            ." version：".$row{'version'}." md5：".$row{'md5'}
                                ." sha1：".$row{'sha1'}."<br>";
                    }
                   
                    //Close the connection
                    mysql_close($mysql_handle);
                    
                    /**
                    
                    //Insert information into databas
                   // mysql_query("INSERT INTO $databaseName ('name', 'version', 'md5', 'sha1') VALUES ($_FILES['myfile']['name'], '1', md5_file($upload_file), sha1_file($upload_file))");
                   
                    
                    //Echo the information
                    echo "成功插入数据库<br>";
                    */
                        
                    }
                    echo "<p><a href='javascript:history.back()'>继续上传</a></p>";
                } else {
                    echo "文件上传失败(".$_FILES["myfile"]["error"].")<br><br>";
                    echo "<p><a href='javascript:history.back()'>重新上传</p>";
                }
            ?>
    </body>
</html>
