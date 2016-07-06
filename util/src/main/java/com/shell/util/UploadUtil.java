package com.shell.util;

import java.io.File;
import java.util.List;

public class UploadUtil {

	/**
	 * 文件允许格式
	 */
	public static String[] FILE_TYPE = { ".rar", ".doc", ".docx", ".zip",
			".pdf", ".txt", ".swf", ".wmv", ".mp3", ".mp4", ".xls", ".xlsx" };

	
	/**
	 * 图片允许格式
	 */
	public static String[] PHOTO_TYPE = { ".gif", ".png", ".jpg", ".jpeg",
			".bmp" };

	/** 
	 * @Title: isFileType 
	 * @Description: 是否为可控文件类型
	 * @param 
	 * @return boolean 
	 * @throws 
	 * @author hewp
	 */ 
	public static boolean isFileType(String fileName, String[] typeArray) {
		for (String type : typeArray) {
			if (fileName.toLowerCase().endsWith(type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 递归获得目录的所有地址
	 * 
	 * @param realpath
	 * @param files
	 * @param fileType
	 * @return
	 */
	public static List<java.io.File> getFiles(String realpath,
			List<File> files, String[] fileType) {
		File realFile = new File(realpath);
		if (realFile.isDirectory()) {
			File[] subfiles = realFile.listFiles();
			for (File file : subfiles) {
				if (file.isDirectory()) {
					getFiles(file.getAbsolutePath(), files, fileType);
				} else {
					if (isFileType(file.getName(), fileType)) {
						files.add(file);
					}
				}
			}
		}
		return files;
	}

	/** 
	 * @Title: getUploadPath 
	 * @Description: 获取上传文件的绝对路径
	 * @param 
	 * @return String 
	 * @throws 
	 * @author hewp
	 */ 
	public static String getUploadPath(String relativePath) {
		String path = File.separator + relativePath;
		File tempFile = new File(path);
		if (!tempFile.exists()) {
			tempFile.mkdirs();
		}
		return path;
	}
	
	/** 
	 * @Title: getUploadPath 
	 * @Description: 获取上传文件的绝对路径
	 * @param 
	 * @return String 
	 * @throws 
	 * @author hewp
	 */ 
	public static String getUploadPath() {
		return getUploadPath("");
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @return string
	 */
	public static String getFileExt(String fileName) {
		return fileName.substring(fileName.lastIndexOf("."));
	}

	/**
	 * 删除物理文件
	 * 
	 * @param path
	 */
	public static void deleteFile(String path) {
		File file = new File(path);
		file.delete();
	}

	/** 
	 * @Title: isImg 
	 * @Description: 判断是否为图片
	 * @param 
	 * @return boolean 
	 * @throws 
	 * @author hewp
	 */ 
	public static boolean isImg(String fileName){
		String fileExt = getFileExt(fileName);
		for(int i = 0; i < PHOTO_TYPE.length; i++){
			if(fileExt.equalsIgnoreCase(PHOTO_TYPE[i])){
				return true;
			}
		}
		return false;
	}
	
	
	/** 
	 * @Title: getContentType 
	 * @Description: 根据扩展名获取contentType
	 * @param 
	 * @return String 
	 * @throws 
	 * @author hewp
	 */ 
	public static String getContentType(String fileExt){
		return null;
	}
	
	
	/** 
	 * @Title: getContentType 
	 * @Description: 根据扩展名获取contentTypeEnum
	 * @param 
	 * @return String 
	 * @throws 
	 * @author hewp
	 */ 
	/*public static ContentTypeEnum getContentTypeEnum(String fileName){
		ContentTypeEnum[] contentTypes = ContentTypeEnum.values();
		String fileExt = getFileExt(fileName);
		for(ContentTypeEnum contentType: contentTypes){
			if(fileExt.equalsIgnoreCase("." + contentType.name())){
				return contentType;
			}
		}
		return null;
	}*/
}
