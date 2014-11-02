package com.konka.dynamicplugin.host.app;

import com.konka.dynamicplugin.host.IHost;
import com.konka.dynamicplugin.host.PluginManager;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.view.View;

/**
 * 宿主应用中需使用插件视图，或启动需使用插件视图并依赖于Activity的控件的Acitivity请继承此类
 */
public abstract class HostActivity extends Activity implements IHost {
	private PluginManager mPluginManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPluginManager = PluginManager.getInstance();
		mPluginManager.setSuperAssetManager(super.getAssets());
		mPluginManager.setSuperResources(super.getResources());
		mPluginManager.setSuperTheme(super.getTheme());
		mPluginManager.setSuperClassLoader(super.getClassLoader());
		mPluginManager.initPlugins(getApplicationContext());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPluginManager.reset();
	}

	@Override
	public final void setContentView(int layoutResID) {
		mPluginManager.setUsePluginResourcesEnable(false);
		super.setContentView(layoutResID);
		mPluginManager.setUsePluginResourcesEnable(true);
	}

	@Override
	public final View findViewById(int id) {
		mPluginManager.setUsePluginResourcesEnable(false);
		View v = super.findViewById(id);
		mPluginManager.setUsePluginResourcesEnable(true);
		return v;
	}

	@Override
	public final Resources getResources() {
		return (null != mPluginManager) ? mPluginManager.getPluginResources() : super
				.getResources();
	}

	@Override
	public final AssetManager getAssets() {
		return (null != mPluginManager) ? mPluginManager.getPluginAssetManager() : super
				.getAssets();
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
