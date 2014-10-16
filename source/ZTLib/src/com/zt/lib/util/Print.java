package com.zt.lib.util;

import android.util.Log;

public class Print {
	private static final String COLOR_CYAN = "\033[0;36m";
	private static final String COLOR_GREEN = "\033[0;32m";
	private static final String COLOR_RED = "\033[0;31m";
	private static final String COLOR_BLUE = "\033[1;34m";
	private static final String COLOR_CLOSE = "\033[0m";
	private static boolean debug = true;
	private static String TAG = "";

	public static void setTAG(String tag) {
		TAG = tag;
	}

	public static void setEnable(final boolean enable) {
		debug = enable;
	}
	
	public static void d(final Object msg) {
		if (debug) {
			d(TAG, msg);
		}
	}

	public static void d(String TAG, Object msg) {
		if (debug) {
			final StackTraceElement line = Thread.currentThread().getStackTrace()[3];
			if (null != line) {
				Log.d(TAG,
						COLOR_CYAN + " " + line.getFileName() + COLOR_GREEN + " "
								+ line.getMethodName() + "()" + COLOR_RED + " "
								+ line.getLineNumber() + COLOR_BLUE + "  Msg["
								+ COLOR_CLOSE + msg.toString() + COLOR_BLUE + "]"
								+ COLOR_CLOSE);
			}
		}
	}
	
	public static void e(final Exception e) {
		if (debug) {
			Log.e(TAG, e.getMessage(), e.getCause());
		}
	}

}
