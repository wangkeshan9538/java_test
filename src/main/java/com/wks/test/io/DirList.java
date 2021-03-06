package com.wks.test.io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * @author Administrator
 * 使用 DirFilter 来进行过滤File
 */
public class DirList {
	public static void main(String[] args) {
		File path=new File("L:\\code\\java\\test\\src\\main\\java\\com\\wks\\test\\io");
		String[] list;
		for(String s:path.list() ) {
			System.out.println(s);
		}
		list=path.list(new DirFilter("\\S*\\.java"));
		Arrays.sort(list , String.CASE_INSENSITIVE_ORDER); //大小写不敏感排序
		for(String s:list) {
			System.out.println(s);
		}
	}
}

class DirFilter implements FilenameFilter{
	private Pattern pattern;

	public DirFilter(String regex ) {
		pattern =Pattern.compile(regex);
	}
	public boolean accept(File dir, String name) {
		return pattern.matcher(name).matches();
	}
}
