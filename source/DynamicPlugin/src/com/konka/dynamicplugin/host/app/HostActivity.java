package com.konka.dynamicplugin.host.app;

import com.konka.dynamicplugin.host.IHost;
import com.konka.dynamicplugin.host.PluginManager;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

public abstract class HostActivity extends Activity implements IHost {
	private PluginManager mPluginManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPluginManager = PluginManager.getInstance();
		mPluginManager.setSuperAssetManager(super.getAssets());
		mPluginManager.setSuperResources(super.getResources());
		mPluginManager.setSuperClassLoader(super.getClassLoader());
		mPluginManager.findPlugins(getApplicationContext());
	}

	@Override
	public void setContentView(int layoutResID) {
		mPluginManager.setUsePluginResourcesEnable(false);
		super.setContentView(layoutResID);
		mPluginManager.setUsePluginResourcesEnable(true);
	}

	@Override
	public View findViewById(int id) {
		mPluginManager.setUsePluginResourcesEnable(false);
		View v = super.findViewById(id);
		mPluginManager.setUsePluginResourcesEnable(true);
		return v;
	}

	@Override
	public Resources getResources() {
		return (null != mPluginManager) ? mPluginManager.getPluginResources()
				: super.getResources();
	}

	@Override
	public AssetManager getAssets() {
		return (null != mPluginManager) ? mPluginManager.getPluginAssetManager()
				: super.getAssets();
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
