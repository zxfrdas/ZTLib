package com.konka.dynamicplugin.host.app;

import android.app.Application;

import com.konka.dynamicplugin.PluginManager2;
import com.konka.dynamicplugin.ResourceController;
import com.konka.dynamicplugin.ResourceController.Dependence;

public class HostApplication extends Application {

	@Override
	public void onCreate() {
		PluginManager2 manager2 = PluginManager2.getInstance();
		manager2.setResourceController(new ResourceController(new Dependence(
				getClassLoader(), getAssets(), getResources(), getTheme())));
		super.onCreate();
	}
	
}
