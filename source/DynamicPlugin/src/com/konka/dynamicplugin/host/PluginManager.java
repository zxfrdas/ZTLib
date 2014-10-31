package com.konka.dynamicplugin.host;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.konka.dynamicplugin.core.DLClassLoader;
import com.konka.dynamicplugin.core.DLUtils;
import com.konka.dynamicplugin.plugin.IPlugin;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;

public class PluginManager {
	private List<PluginInfo> mPlugins;
	private AssetManager mSupAssetManager;
	private Resources mSuperResources;
	private ClassLoader mSuperClassLoader;
	private Map<String, AssetManager> mAssetMap;
	private Map<String, Resources> mResourcesMap;
	private String mCurPluginApkPath;
	private boolean mIsUsePluginResources;
	
	private static class InstanceHolder {
		private static PluginManager sInstance = new PluginManager();
	}

	public static PluginManager getInstance() {
		return InstanceHolder.sInstance;
	}

	private PluginManager() {
		mPlugins = new ArrayList<PluginInfo>();
		mAssetMap = new HashMap<String, AssetManager>();
		mResourcesMap = new HashMap<String, Resources>();
		mIsUsePluginResources = false;
	}

	public void setSuperAssetManager(AssetManager assetManager) {
		mSupAssetManager = assetManager;
	}

	public void setSuperResources(Resources resources) {
		mSuperResources = resources;
	}

	public void setSuperClassLoader(ClassLoader classLoader) {
		mSuperClassLoader = classLoader;
	}

	public void setUsePluginResourcesEnable(boolean enable) {
		mIsUsePluginResources = enable;
	}

	public void findPlugins(Context context) {
		File dexFolder = context.getDir("dex", Context.MODE_PRIVATE);
		File pluginFolder = context.getDir("plugins", Context.MODE_PRIVATE);
		File[] pluginFiles = pluginFolder.listFiles();
		mPlugins.clear();
		for (File plugin : pluginFiles) {
			PluginInfo info = new PluginInfo();

			String apkPath = plugin.getAbsolutePath();
			PackageInfo packageInfo = DLUtils.getPackageInfo(context, apkPath);
			String appName = DLUtils.getAppLabel(context, apkPath).toString();
			String packageName = packageInfo.applicationInfo.packageName;
			String apkName = apkPath.substring(
					apkPath.lastIndexOf(File.separator) + 1,
					apkPath.lastIndexOf("."));

			info.setApkPath(apkPath);
			info.setName(appName);
			info.setPackageName(packageName);
			info.setDexPath(dexFolder.getAbsolutePath() + File.separator + apkName
					+ ".dex");
			loadResources(context, info);
			mPlugins.add(info);
		}
	}

	private void loadResources(Context context, PluginInfo info) {
		try {
			AssetManager assetManager = AssetManager.class.newInstance();
			Method addAssetPath = assetManager.getClass().getMethod("addAssetPath",
					String.class);
			addAssetPath.invoke(assetManager, info.getApkPath());
			mAssetMap.put(info.getApkPath(), assetManager);
			Resources resources = new Resources(assetManager,
					mSuperResources.getDisplayMetrics(),
					mSuperResources.getConfiguration());
			mResourcesMap.put(info.getApkPath(), resources);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List<PluginInfo> getPluginInfos() {
		return mPlugins;
	}

	public View getPluginView(Context context, PluginInfo info) {
		mCurPluginApkPath = info.getApkPath();
		IPlugin plugin = launchPlugin(context, info);
		return plugin.getPluginView();
	}

	private IPlugin launchPlugin(Context context, PluginInfo info) {
		IPlugin plugin = null;
		try {
			Class<?> localClass = getPluginClassLoader(context).loadClass(
					info.getPackageName() + ".PluginImpl");
			Constructor<?> localConstructor = localClass
					.getConstructor(new Class[] {});
			Object instance = localConstructor.newInstance(new Object[] {});
			plugin = (IPlugin) instance;
			Log.d("print", "instance = " + instance);
			plugin.setContext(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return plugin;
	}

	public AssetManager getPluginAssetManager() {
		AssetManager assetManager = null;
		if (mIsUsePluginResources) {
			assetManager = mAssetMap.get(mCurPluginApkPath);
		}
		return (null != assetManager) ? assetManager : mSupAssetManager;
	}

	public Resources getPluginResources() {
		Resources resources = null;
		if (mIsUsePluginResources) {
			resources = mResourcesMap.get(mCurPluginApkPath);
		}
		return (null != resources) ? resources : mSuperResources;
	}

	public ClassLoader getPluginClassLoader(Context context) {
		ClassLoader loader = null;
		if (mIsUsePluginResources) {
			if (null != mCurPluginApkPath && !mCurPluginApkPath.isEmpty()) {
				loader = DLClassLoader.getClassLoader(mCurPluginApkPath,
						context.getApplicationContext(), mSuperClassLoader);
			}
		}
		return (null != loader) ? loader : mSuperClassLoader;
	}

}
