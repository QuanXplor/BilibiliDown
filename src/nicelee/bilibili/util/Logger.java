package nicelee.bilibili.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	private final static SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	final static boolean mute;
	static {
		mute = !"true".equals(System.getProperty("bilibili.prop.log", "true"));
	}

	public static void print(Object str) {
		if (mute)
			return;
		System.out.print(str);
	}

	public static void println() {
		if (mute)
			return;
		System.out.println();
	}

	public static void printf(String str, Object... obj) {
		if (mute)
			return;
		StackTraceElement ele = Thread.currentThread().getStackTrace()[2];
		String file = ele.getFileName();
		file = file.substring(0, file.length() - 5);
		String method = ele.getMethodName();
		int line = ele.getLineNumber();
		String preStr = String.format(str, obj);
		String result = String.format(logStr(), file, method, line, preStr);
		System.out.println(result);
	}

	public static void println(String str) {
		if (mute)
			return;
		StackTraceElement ele = Thread.currentThread().getStackTrace()[2];
		String file = ele.getFileName();
		file = file.substring(0, file.length() - 5);
		String method = ele.getMethodName();
		int line = ele.getLineNumber();
		String result = String.format(logStr(), file, method, line, str);
		System.out.println(result);
	}

	public static void println(Object obj) {
		if (mute)
			return;
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
