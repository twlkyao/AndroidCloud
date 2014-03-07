/**
 * @Author:		Shiyao Qi
 * @Date:		2014.03.07
 * @Function:	Implement the encrypt and decryption algorithms.
 * 				The lib shoud provide the common algorithms for
 * 				encryption and decryption, choose the algorithms
 * 				according to the file size, file type, encrypt level,
 * 				and decide which folder to encrypt and decrypt, whether
 *				the source file shoud be deleted, where to store the destion file.
 * @Params:	The encrypt and decrypt algorithms shoud provide the following params:
 * 			1. 
 * String srcFilePath,
			int encrypt_level, String encrypt_key,
			String destFilePath, String uploadFileInfoUrl
			
			
			srcFilePath, constantVariables.algorithms[encrypt_level],
				constantVariables.keys[encrypt_level], destFilePath)
				
				
 * @Email: qishiyao2008@126.com
 */
package com.twlkyao.utils;

public class DEncryptionLib {
	
	/**
	 * 
	 * @param srcFilePath The file path of the file to be encrypted.
	 * @param encryptKey The encrypt key.
	 * @param desFilePath The destination of the encrypted file to store.
	 * @param delSrc True, if should delete the source file.
	 * @return True, if the encryption is succeeded.
	 */
	public boolean AES(String srcFilePath, String encryptKey, String desFilePath, boolean delSrc) {
		return true;
	}
}
