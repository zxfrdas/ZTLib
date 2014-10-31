package com.konka.dynamicplugin.host.app;

import android.app.Service;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.konka.dynamicplugin.host.IHost;
import com.konka.dynamicplugin.host.PluginManager;

public abstract class HostService extends Service implements IHost {
	private PluginManager mPluginManager;

	@Override
	public void onCreate() {
		super.onCreate();
		mPluginManager = PluginManager.getInstance();
		mPluginManager.setSuperAssetManager(super.getAssets());
		mPluginManager.setSuperResources(super.getResources());
		mPluginManager.setSuperClassLoader(super.getClassLoader());
		mPluginManager.findPlugins(getApplicationContext());
	}

	@Override
	public AssetManager getAssets() {
		return (null != mPluginManager) ? mPluginManager.getPluginAssetManager()
				: super.getAssets();
	}

	@Override
	public Resources getResources() {
		return (null != mPluginManager) ? mPluginManager.getPluginResources()
				: super.getResources();
	}

	@Override
	public ClassLoader getClassLoader() {
		return (null != mPluginManager) ? mPluginManager
				.getPluginClassLoader(getApplicationContext()) : super
				.getClassLoader();
	}

	@Override
	public PluginManager getPluginManager() {
		return mPluginManager;
	}

}
