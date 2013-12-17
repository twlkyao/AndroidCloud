<?php
	header("Content-Type: text/html; charset=utf-8") ;

    /*
     session_start(); // start the seesion
    
	$user_id = $_POST["user_id"];            // The file owner's id
	$user_name = $_POST["user_name"];	// The file owner's name
	$dirname = 'testdir';
	if (mkdir($dirname, 0755)) {
		echo "目录 $dirname 创建成功";
	} else {
    echo "目录 $dirname 创建失败";
	}
	 */

	$target_path  = getcwd()."/upload/"; // The directory to stoage the uploaded file 
	//echo "target_path:".$target_path."<br>";
	
	$target_path = $target_path . basename( $_FILES['uploadedfile']['name']); // The file save path is the upload file name, basename()returns the name of the file.
	if(0 == $_FILES['uploadedfile']['error']) {
		 echo "Upload succeeded!";
		 if (file_exists($target_path)) {   // The file is already exist.
			echo $_FILES["uploadedfile"]["name"] . "is already exist！";
             
         } else { // The file is not exist
			move_uploaded_file($_FILES["uploadedfile"]["tmp_name"], $target_path);
			echo "File saved at:" .$target_path."<br>";
		}
	}        
?>
