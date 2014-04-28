<?php
	header("Content-Type: text/html; charset=utf-8") ;

   // session_start(); // Start the seesion

	//$user_id = $_POST["user_id"]; // The file owner's id
	

	$target_path = getcwd() . "/upload/"; // Construct a directory path according to the user_id.
	
	if(!file_exists($target_path)) { // The directory does not exist.
		if(mkdir($target_path, 0755)) { // Create the directory.
		
			// The file save path is the upload file name, basename()returns the name of the file.
			$target_path = $target_path . basename( $_FILES['uploadedfile']['name']);
			
			if(0 == $_FILES['uploadedfile']['error']) { // If the file is already exist, overwrite it.
			
				move_uploaded_file($_FILES["uploadedfile"]["tmp_name"], $target_path);
				
				//echo "File saved at:" .$target_path."<br>";
				
				$result_array = array(  
					'flag' => 'upload_succeed',
				
					//'user_id'=> $user_id,  
					//'sessionid'=>$sessionid  
				); 
				
				echo json_encode($result_array);	// Return the result encoded in JSON
			} else { // File upload failed.
			
				$result_array = array(  
					'flag' => 'upload_failed',
				
					//'user_id'=> $user_id,  
					//'sessionid'=>$sessionid  
				); 
				
				echo json_encode($result_array);	// Return the result encoded in JSON
			}
		} else { // Create directory failed.
			$result_array = array(  
					'flag' => 'mkdir_failed',
				
					//'user_id'=> $user_id,  
					//'sessionid'=>$sessionid  
				); 
				
			echo json_encode($result_array);	// Return the result encoded in JSON
		}
	} else {
		// The file save path is the upload file name, basename()returns the name of the file.
			$target_path = $target_path . basename( $_FILES['uploadedfile']['name']);
			
			if(0 == $_FILES['uploadedfile']['error']) { // If the file is already exist, overwrite it.
			
				move_uploaded_file($_FILES["uploadedfile"]["tmp_name"], $target_path);
				
				//echo "File saved at:" .$target_path."<br>";
				
				$result_array = array(  
					'flag' => 'upload_succeed',
				
					//'user_id'=> $user_id,  
					//'sessionid'=>$sessionid  
				); 
				
				echo json_encode($result_array);	// Return the result encoded in JSON
			} else { // File upload failed.
			
				$result_array = array(  
					'flag' => 'upload_failed',
				
					//'user_id'=> $user_id,  
					//'sessionid'=>$sessionid  
				); 
				
				echo json_encode($result_array);	// Return the result encoded in JSON
			}
	}
	
	
	
	/**
	$target_path  = getcwd() . "/upload/" . $dirname . "/"; // The directory to stoage the uploaded file 
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
	*/       
?>
