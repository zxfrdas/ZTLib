package com.konka.dynamicplugin.host.app;

import android.app.Service;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;

import com.konka.dynamicplugin.host.IHost;
import com.konka.dynamicplugin.host.PluginManager;

/**
 * 宿主应用中启动需使用插件视图并依赖于Service的控件的Service请继承此类
 */
public abstract class HostService extends Service implements IHost {
	private PluginManager mPluginManager;

	@Override
	public void onCreate() {
		super.onCreate();
		mPluginManager = PluginManager.getInstance();
		mPluginManager.setSuperAssetManager(super.getAssets());
		mPluginManager.setSuperResources(super.getResources());
		mPluginManager.setSuperTheme(super.getTheme());
		mPluginManager.setSuperClassLoader(super.getClassLoader());
		mPluginManager.initPlugins(getApplicationContext());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mPluginManager.reset();
	}

	@Override
	public final AssetManager getAssets() {
		return (null != mPluginManager) ? mPluginManager.getPluginAssetManager() : super
				.getAssets();
	}

	@Override
	public final Resources getResources() {
		return (null != mPluginManager) ? mPluginManager.getPluginResources() : super
				.getResources();
	}

	@Override
	public final Theme getTheme() {
		return (null != mPluginManager) ? mPluginManager.getPluginTheme() : super.getTheme();
	}

	@Override
	public final ClassLoader getClassLoader() {
		return (null != mPluginManager) ? mPluginManager
				.getPluginClassLoader(getApplicationContext()) : super.getClassLoader();
	}

	@Override
	public final PluginManager getPluginManager() {
		return mPluginManager;
	}

	@Override
	public final Context getHostContext() {
		return this;
	}

}
