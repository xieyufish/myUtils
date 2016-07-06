package com.shell.util;

import java.security.MessageDigest;

/**
 * 加密工具
 * 暂时收录：MD5加密方式
 * @author shell
 */
public class EncryptUtil {
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	/**
	 * 转换字节数组为16进制字串
	 * 
	 * @param b
	 *            字节数组
	 * @return 16进制字串
	 */
	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	/** 
	 * 字符串加密
	 * 默认使用SysConstants.MD5_SOLT盐来加密
	 * @param origin  原字符串
	 * @return String 
	 */ 
	public static String MD5Encrypt(String origin) {
		return MD5Encrypt(origin, 1);
	}

	/** 
	 * 多次使用MD5加密,此方法默认使用SysConstants.MD5_SOLT盐来加密
	 * @param origin  原字符串
	 * @param times 加密次数
	 * @return String 
	 * @see MD5Encrypt(String, String, int)
	 */ 
	public static String MD5Encrypt(String origin, int times){
		return MD5Encrypt(origin, "shell", times);
	}
	
	/**
	 * md5加密
	 * @param origin  原字符串
	 * @param solt  加盐
	 * @param times  加密次数
	 * @return
	 */
	public static String MD5Encrypt(String origin, String solt, int times) {
		String resultString = null;
		StringBuilder stringBuilder = new StringBuilder(origin);
		stringBuilder.append(solt);

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(stringBuilder.toString()
					.getBytes()));
			for (int i = 1; i < times; i++) {
				resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resultString;
	}
	
	/**
	 * 对文件，图片等资源uri进行base64加密之后，左移相应的位数
	 * @param url   原url
	 * @param shift  左移位数
	 * @return  加密后的url字符串
	 */
	public static String encryptURL(String url) {
		// base64加密后的url
		String encryptURL = Base64.encode(url.getBytes());
		int length = encryptURL.length();
		int shift = 5;
		
		// 移位长度超过加密后字符串的长度，取与
		if (shift > length) {
			shift = shift & length;
		}
		
		// 移动位数等于加密后字符串的长度，取加密后长度减去一个差值
		if (shift == length) {
			shift = length - 3;
		}
		return encryptURL.substring(shift) + encryptURL.substring(0, shift);
	}
	
	/**
	 * 对加密后的uri进行解密
	 * @param encryptURL   加密后的url
	 * @param shift    移动的字符位数
	 * @return
	 */
	public static String decryptURL(String encryptURL) {
		if (encryptURL == null || "".equals(encryptURL.trim())) {
			return null;
		}
		int length = encryptURL.length();
		int shift = 5;
		
		if (shift > length) {
			shift = shift & length;
		}
		if (shift == length) {
			shift = length - 3;
		}
		String shiftString = encryptURL.substring(length - shift) 
				+ encryptURL.substring(0,length - shift);
		byte[] results = Base64.decode(shiftString);
		return new String(results);
	}
	
}
