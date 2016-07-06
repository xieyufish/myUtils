package com.shell.util;

import java.io.File;

public class FileUtil {
	
	/**
	 * 获取资源存储的绝对根路径, 配置文件config.properties#resource.storage.dir属性
	 * 
	 * @return
	 */
	public static String getRootDirectory() {
		//TODO 替换成正式生产环境的根路径
		return "";
	}

	/**
	 * 创建目录
	 * 
	 * @param dirPath 目录路径
	 * @return
	 */
	public static File mkdir(String dirPath) {

		File file = new File(dirPath);
		
		if(!file.isAbsolute()) {
			file = new File(getRootDirectory(), dirPath);
		}
		
		if (!file.exists() || !file.isDirectory()) {
			mkdir(file.getParent());
			file.mkdir();
		}
		return file;
	}
	
	/**
	 * 创建目录
	 * @param file
	 * @return
	 */
	public static File mkdir(File file) {
		return mkdir(file.getPath());
	}
	
	/**
	 * 重命名目录
	 * 
	 * @param sourcePath 源文件
	 * @param destPath 重命名文件
	 */
	public static void rename(String sourceFile, String destFile) {

//		sourcePath = getRootDirectory() + sourcePath;
//		destPath = getRootDirectory() + destPath;
//		File book = new File(sourcePath);
//		File destBookFile = new File(destPath);
//		book.renameTo(destBookFile);
		File source = new File(sourceFile);
		File dest = new File(destFile);
		
		if (!source.isAbsolute()) {
			source = new File(getRootDirectory(), sourceFile);
		}
		
		if (!dest.isAbsolute()) {
			dest = new File(getRootDirectory(), destFile);
		}
		
		source.renameTo(dest);
	}
	
	public static void rename(File source, File dest) {
		rename(source.getPath(), dest.getPath());
	}
	
	/**
	 * 删除文件或者目录
	 * 
	 * @param file
	 */
	public static void delete(File file) {
		if (!file.isAbsolute()) {
			file = new File(getRootDirectory(), file.getPath());
		}
		
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				delete(new File(file, children[i]));
			}
			file.delete();
		} else if (file.exists()) {
			file.delete();
		}
	}
	
	public static void delete(String path) {
		delete(new File(path));
	}
	
	// TODO  保存文件
	public static void save() {
		
	}
}
