package com.wks.test.io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Administrator
 * 遍历出 此目录下的 , 匹配目录
 */
public final class Directory {
	
	public static File[] local(String start ,String regex) {
		return local(new File(start) , regex);
	}
	
	public static File[] local(File dir,final String regex) {
		return dir.listFiles(new FilenameFilter() {
			private Pattern pattern=Pattern.compile(regex);
			public boolean accept(File dir, String name) {
				return pattern.matcher(name).matches();
			}
		});
	}
	
	/**
	 * @author Administrator
	 * 存储 某目录下所有的dir 和file, 但并不是树状模型,而是全部存储在两个list
	 */
	public static class TreeInfo implements Iterable<File>{
		public List<File> files=new ArrayList<File>();
		public List<File> dirs=new ArrayList<File>();
		public Iterator<File> iterator() {
			return files.iterator();
		}
		void addAll(TreeInfo other) {
			files.addAll(other.files);
			dirs.addAll(other.dirs);
		}
		 @Override
		public String toString()  {
			return files +"\n" +dirs;
		}
	}
	
	
	/**
	 * 产生一个当前目录的 TreeInfo
	 * @return
	 */
	static TreeInfo resourceDirs(File startDir , String regex) {
		TreeInfo result= new TreeInfo();
		for(File item: startDir.listFiles()) {
			if(item.isDirectory()) {
				result.dirs.add(item);
				result.addAll(resourceDirs(item ,regex));
			}else{
				if(item.getName().matches(regex))
					result.files.add(item);
			}
							
		}//end_if
		return result;
	}
	
	public static TreeInfo walk(String path, String regex) {
		return resourceDirs(new File(path) ,regex);
	}
	
	public static TreeInfo walk(File start,String regex) {
		return resourceDirs(start , regex);
	}
	
	public static void main(String[] args) {
		String path="D:\\有道\\Dict";
		System.out.println(walk(path ,new String("\\S*")));
	}
}

