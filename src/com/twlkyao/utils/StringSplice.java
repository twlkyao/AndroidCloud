package com.twlkyao.utils;

public class StringSplice {
	public static String stringSplice(String a, String b) {
		String result = "";
		char[] chara = a.toCharArray();
		char[] charb = b.toCharArray();
		int length = (chara.length > charb.length) ? charb.length : chara.length;
		
		for(int i = 0; i < length; i ++) {
			chara[i] = (char) ((chara[i] + charb[i]) %128);
		}
		result = String.valueOf(chara);
		return result;
	}
}
