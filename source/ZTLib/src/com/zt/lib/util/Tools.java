package com.zt.lib.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Tools {

	public static boolean isStringNotEmpty(String str)
	{
		return null != str && !str.isEmpty();
	}
	
	public static boolean isNetworkAvailable(Context context)
	{
		// 获取网络连接管理者
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
			return false;
		} else {
			// 获取网络的状态信息
			NetworkInfo ci = cm.getActiveNetworkInfo();
			if (ci != null && ci.isConnected() && ci.isAvailable()) {
				return true;
			} else {
				return false;
			}
		}
	}
	
}
