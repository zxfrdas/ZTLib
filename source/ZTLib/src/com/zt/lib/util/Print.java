package com.zt.lib.util;

import android.util.Log;

public class Print {
	private static final String COLOR_CYAN = "\033[0;36m";
	private static final String COLOR_GREEN = "\033[0;32m";
	private static final String COLOR_RED = "\033[0;31m";
	private static final String COLOR_BLUE = "\033[1;34m";
	private static final String COLOR_CLOSE = "\033[0m";
	private static boolean debug = true;
	private static boolean color = false;
	private static String TAG = "";

	public static void setTAG(String tag) {
		TAG = tag;
	}

	public static void setEnable(final boolean enable) {
		debug = enable;
	}

	public static void setColorEnable(final boolean enable) {
		color = enable;
	}

	public static void d(final Object msg) {
		d(TAG, msg);
	}

	public static void d(String TAG, Object msg) {
		if (debug) {
			final StackTraceElement[] elements = Thread.currentThread().getStackTrace();
			final int size = elements.length;
			StackTraceElement line = null;
			for (int i = 0; i < size; i ++) {
				String file = elements[i].getFileName();
				String method = elements[i].getMethodName();
				if (i < 2) {
					continue;
				} else if (!"Print.java".equalsIgnoreCase(file)
						&& !"d".equalsIgnoreCase(method)) {
					line = elements[i];
					break;
				}
			}
			if (null != line) {
				StringBuilder sb = new StringBuilder();
				if (color) {
					sb.append(COLOR_CYAN);
				}
				sb.append(" ").append(line.getFileName());
				if (color) {
					sb.append(COLOR_GREEN);
				}
				sb.append(" ").append(line.getMethodName()).append("()");
				if (color) {
					sb.append(COLOR_RED);
				}
				sb.append(" ").append(line.getLineNumber());
				if (color) {
					sb.append(COLOR_BLUE);
				}
				sb.append(" Msg[");
				if (color) {
					sb.append(COLOR_CLOSE);
				}
				sb.append(msg.toString());
				if (color) {
					sb.append(COLOR_BLUE);
				}
				sb.append("]");
				if (color) {
					sb.append(COLOR_CLOSE);
				}
				Log.d(TAG, sb.toString());
			}
		}
	}

	public static void e(final Exception e) {
		if (debug) {
			Log.e(TAG, e.getMessage(), e.getCause());
		}
	}

}
