package com.konka.dynamicplugin.plugin;

import android.content.Context;

public interface IPlugin {

	void setContext(Context context);
	<T> T getPluginView();
	
}
