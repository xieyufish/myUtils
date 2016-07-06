package com.shell.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 压缩工具 -- 可以使用apach提供的压缩工具包就不用自己写了
 * @author shell
 */
public class ZipUtil {
	
	/**
	 * 压缩目录
	 * 
	 * @param directory 待压缩文件目录- 可以是绝对路径, 也可以是相对路径
	 * @param compressedFileName 压缩文件的名字
	 */
	public void compressed(String directory, String compressedFileName) {

		String filePath = directory + File.separator + compressedFileName;
		String bookDir = directory;
		if (!new File(filePath).isAbsolute()) {
			filePath = FileUtil.getRootDirectory() + directory + File.separator + compressedFileName;
			bookDir = FileUtil.getRootDirectory() + directory;
		}
		File zipFile = new File(filePath);

		if (zipFile.exists()) {
			zipFile.delete();
		}
		List<String> fileNames = listAllFiles(bookDir);
		int length = 1024;
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(filePath))) {

			for (String fileName : fileNames) {
				BufferedInputStream bis = new BufferedInputStream(
						new FileInputStream(fileName));
				ZipEntry entry = new ZipEntry(fileName.replace(bookDir + File.separator, ""));
				zos.putNextEntry(entry);
				byte data[] = new byte[length];
				int count = bis.read(data, 0, length);
				while (count != -1) {
					zos.write(data, 0, count);
					count = bis.read(data, 0, length);
				}
				bis.close();
				zos.closeEntry();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取绝对路径下所有文件的绝对路径
	 * 
	 * @param directory 传入的绝对路径
	 * @return
	 */
	private List<String> listAllFiles(String directory) {

		List<String> fileNames = new ArrayList<>();
		File file = new File(directory);
		if (!file.exists()) {
			return fileNames;
		}
		if (file.isDirectory()) {
			File[] files = file.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {

					return !name.endsWith(".zip");
				}
			});
			for (File file2 : files) {
				fileNames.addAll(listAllFiles(file2.getAbsolutePath()));
			}
		} else {
			fileNames.add(file.getAbsolutePath());
		}
		return fileNames;
	}
}
