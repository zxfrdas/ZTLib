package com.zt.lib.util;

import android.util.Log;

public class Print {

	private static boolean printable = true;
	private static String TAG = "";

	public static void setTAG(String tag) {
		TAG = tag;
	}

	public static void enable() {
		printable = true;
	}

	public static void disable() {
		printable = false;
	}

	public static void d(String msg) {
		if (printable) {
			StackTraceElement line = new Throwable().getStackTrace()[1];
			if (line != null) {
				Log.d(TAG, "[" + line.getFileName() + ":" + line.getLineNumber()
						+ "]: " + line.getMethodName() + "() " + msg);
			}
		}
	}

	public static void d(String TAG, String msg) {
		if (printable) {
			StackTraceElement line = new Throwable().getStackTrace()[1];
			if (line != null) {
				Log.d(TAG, "[" + line.getFileName() + ":" + line.getLineNumber()
						+ "]: " + line.getMethodName() + "() " + msg);
			}
		}
	}

	public static void d(Object o) {
		if (printable) {
			StackTraceElement line = new Throwable().getStackTrace()[1];
			if (line != null) {
				Log.d(TAG, "[" + line.getFileName() + ":" + line.getLineNumber()
						+ "]: " + line.getMethodName() + "() " + o.toString());
			}
		}
	}

	public static void w(String msg) {
		if (printable) {
			StackTraceElement line = new Throwable().getStackTrace()[1];
			if (line != null) {
				Log.d(TAG, "[" + line.getFileName() + ":" + line.getLineNumber()
						+ "]: " + line.getMethodName() + "() " + msg);
			}
		}
	}

	public static void i(String msg) {
		if (printable) {
			StackTraceElement line = new Throwable().getStackTrace()[1];
			if (line != null) {
				Log.d(TAG, "[" + line.getFileName() + ":" + line.getLineNumber()
						+ "]: " + line.getMethodName() + "() " + msg);
			}
		}
	}

}
