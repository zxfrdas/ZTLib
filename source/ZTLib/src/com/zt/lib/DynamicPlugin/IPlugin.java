package com.zt.lib.DynamicPlugin;

import android.content.Context;

public interface IPlugin {

	void setContext(Context context);
	<T> T getPluginView();
	
}
