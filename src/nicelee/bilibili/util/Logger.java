package nicelee.bilibili.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	private final static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 测试用
	 * @param str
	 */
	public static void print(Object str) {
		System.out.print(str);
	}
	/**
	 * 测试用
	 * @param str
	 */
	public static void println() {
		System.out.println();
	}
	
	/**
	 * 测试用
	 * @param str
	 */
	public static void printf(String str, Object... obj) {
		StackTraceElement ele = Thread.currentThread().getStackTrace()[2];
		String file = ele.getFileName();
		file = file.substring(0, file.length() - 5);
		String method = ele.getMethodName();
		int line = ele.getLineNumber();
		String preStr = String.format(str, obj);
		String result = String.format(logStr(), file, method, line, preStr);
		System.out.println(result);
	}
	
	/**
	 * 测试用
	 * @param str
	 */
	public static void println(String str) {
		StackTraceElement ele = Thread.currentThread().getStackTrace()[2];
		String file = ele.getFileName();
		file = file.substring(0, file.length() - 5);
		String method = ele.getMethodName();
		int line = ele.getLineNumber();
		String result = String.format(logStr(), file, method, line, str);
		System.out.println(result);
	}
	/**
	 * 测试用
	 * @param str
	 */
	public static void println(Object obj) {
		StackTraceElement ele = Thread.currentThread().getStackTrace()[2];
		String file = ele.getFileName();
		file = file.substring(0, file.length() - 5);
		String method = ele.getMethodName();
		int line = ele.getLineNumber();
		String result = String.format(logStr(), file, method, line, obj.toString());
		System.out.println(result);
	}

	private static String logStr(){
		return sdf.format(new Date())+" %s-%s/%d : %s";
	}
}
