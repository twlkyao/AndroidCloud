package com.twlkyao.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.util.Log;

/**
 * @author Shiyao Qi
 * @date 2013.12.16
 * @email qishiyao2008@126.com
 */
/**
 * Implement the file encryption/decryption method.
 */
public class FileDEncryption {
	
		 /*
	      * 根据字符串生成密钥字节数组 
	      * @param keyStr 密钥字符串
	      * @return 
	      * @throws UnsupportedEncodingException
	      */
	/**
	 * Generate the byte array according to the string for TripleDES.
	 * @param keyStr The string.
	 * @return The generated byte array.
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] build3DesKey(String keyStr) throws UnsupportedEncodingException{
		byte[] key = new byte[24];    //声明一个24位的字节数组，默认里面都是0
		byte[] temp = keyStr.getBytes("UTF-8");    //将字符串转成字节数组
		
		/*
		 * 执行数组拷贝
		 * System.arraycopy(源数组，从源数组哪里开始拷贝，目标数组，拷贝多少位)
		 */
		if(key.length > temp.length){
			//如果temp不够24位，则拷贝temp数组整个长度的内容到key数组中
			System.arraycopy(temp, 0, key, 0, temp.length);
		}else{
			//如果temp大于24位，则拷贝temp数组24个长度的内容到key数组中
			System.arraycopy(temp, 0, key, 0, key.length);
		}
		return key;
	}
	
	/**
	 * Use Algorithm:algorithm and Key:encKey to encrypt
	 * the file:srcFilepath and store it in destFilepath.
	 * @param srcFilepath The file path of the file to be encrypted.
	 * @param algorithm The algorithm to be used.
	 * @param encKey The encrypt key to be used.
	 * @param destFilepath The file path to save the encrypted file.
	 * @return True, if the encryption succeeds.
	 */
	public boolean Encryption (String srcFilepath, String algorithm, String encKey, String destFilepath) {
		
		boolean flag = true;
		SecretKey enkey;
		try {
			enkey = new SecretKeySpec(build3DesKey(encKey), algorithm);
			Cipher cipher = Cipher.getInstance(algorithm); // Creates a new Cipher for the specified algorithm.
			cipher.init(Cipher.ENCRYPT_MODE, enkey); // Initial to ENCRYTP_MODE
			
			InputStream is = new FileInputStream(srcFilepath);
			
			File destFile = new File(destFilepath); // Create a new File instance.
			if(!destFile.exists()) { // Only create a new file, if the file does not exists.
				try {
					destFile.createNewFile(); // Create a new, empty file.
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
//					return false;
				}
			}
			

			OutputStream out = new FileOutputStream(destFile);
			CipherInputStream cis = new CipherInputStream(is, cipher);
			byte[] buffer = new byte[1024];
			int r;
			while ((r = cis.read(buffer)) > 0) {
				out.write(buffer, 0, r);
			}
			cis.close();
			is.close();
			out.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flag = false;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flag = false;
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flag = false;
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flag = false;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			Log.e("FileNotFound", e.toString());
			flag = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flag = false;
		}
		
		return flag;
	}
	
	/**
	 * Use Algorithm:algorithm and Key:encKey to decrypt
	 * the file:srcFilepath and store it in destFilepath.
	 * @param srcFilepath The file path of the file to be decrypted.
	 * @param algorithm The algorithm to be used.
	 * @param encKey The encrypt key to be used.
	 * @param destFilepath The file path to save the decrypted file.
	 * @return The decrypt status.
	 */
	public boolean Decryption(String srcFilepath, String algorithm, String decKey, String destFilepath) {
		
		boolean flag = true;
		SecretKey deskey;
		try {
			deskey = new SecretKeySpec(build3DesKey(decKey), algorithm);
			Cipher cipher = Cipher.getInstance(algorithm); // Creates a new Cipher for the specified algorithm.
			cipher.init(Cipher.DECRYPT_MODE, deskey); // Initial to ENCRYTP_MODE
			InputStream is = new FileInputStream(srcFilepath);
			File destFile = new File(destFilepath);

			if(!destFile.exists()){
				destFile.createNewFile();
			}
			OutputStream out = new FileOutputStream(destFile);
			CipherOutputStream cos = new CipherOutputStream(out, cipher);
			byte[] buffer = new byte[1024];
			int r;
			while ((r = is.read(buffer)) >= 0) {
				cos.write(buffer, 0, r);
			}
				cos.close();
				out.close();
				is.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flag = false;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flag = false;
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flag = false;
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flag = false;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flag = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flag = false;
		}
		
		return flag;
	}
}
