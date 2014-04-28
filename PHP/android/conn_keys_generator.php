<?php

	// The encrypt key set.
    $encrypt_key_set = array(
	    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
	    'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
	    'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D',
	    'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
	    'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
	    'Y', 'Z'
	);
   
    // The encrypt key length of different algorithm.
    $encrypt_key_length = array(
	    "0" => 12, // The encrypt key length of "DESede" is 192 bits(24 bytes, 12 chars).
	    "1" => 8 // The encrypt key length of "AES" is 128 bits(16 bytes, 8 chars).
    );
?>
