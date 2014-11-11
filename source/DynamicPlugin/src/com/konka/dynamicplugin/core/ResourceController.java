package com.konka.dynamicplugin.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;


public final class ResourceController {
	private Dependence mDependence;
	private Map<String, AssetManager> mAssetMap;
	private Map<String, Resources> mResourcesMap;
	private Map<String, Theme> mThemeMap;
	private Map<String, ClassLoader> mClassLoaderMap;
	private Map<String, PluginInfo> mLoadedPluginMap;
	private String mCurrentPluginApk;

	public static final class Dependence {
		public ClassLoader mSuperClassLoader;
		public AssetManager mSupAssetManager;
		public Resources mSuperResources;
		public Theme mSuperTheme;

		public Dependence(ClassLoader loader, AssetManager asset, Resources res,
				Theme theme) {
			mSuperClassLoader = loader;
			mSupAssetManager = asset;
			mSuperResources = res;
			mSuperTheme = theme;
		}

	}

	public ResourceController(Dependence dependence) {
		mDependence = dependence;
		mClassLoaderMap = new HashMap<String, ClassLoader>();
		mAssetMap = new HashMap<String, AssetManager>();
		mResourcesMap = new HashMap<String, Resources>();
		mThemeMap = new HashMap<String, Resources.Theme>();
		mLoadedPluginMap = new HashMap<String, PluginInfo>();
	}

	public void installClassLoader(String apkPath, String dexPath) {
		DLClassLoader.getExistClassLoader(apkPath, dexPath, getSuperClassLoader());
	}

	public void loadPluginResource(PluginInfo pluginInfo) {
		try {
			final String apkPath = pluginInfo.getApkPath();
			final String dexPath = pluginInfo.getDexPath();
			// classloader
			mClassLoaderMap.put(apkPath, DLClassLoader.getExistClassLoader(apkPath,
					dexPath, getSuperClassLoader()));
			// asset
			AssetManager assetManager = AssetManager.class.newInstance();
			Method addAssetPath = assetManager.getClass().getMethod("addAssetPath",
					String.class);
			addAssetPath.invoke(assetManager, apkPath);
			mAssetMap.put(apkPath, assetManager);
			// resource
			Resources resources = new Resources(assetManager, getSuperResources()
					.getDisplayMetrics(), getSuperResources().getConfiguration());
			mResourcesMap.put(apkPath, resources);
			// theme
			Theme theme = resources.newTheme();
			theme.setTo(getSuperTheme());
			mThemeMap.put(apkPath, theme);
			// set loaded
			final String entryClass = pluginInfo.getEntryClass();
			mLoadedPluginMap.put(entryClass, pluginInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void unloadPluginResource(PluginInfo pluginInfo) {
		final String apkPath = pluginInfo.getApkPath();
		final String entryClass = pluginInfo.getEntryClass();
		mClassLoaderMap.remove(apkPath);
		mAssetMap.remove(apkPath);
		mResourcesMap.remove(apkPath);
		mThemeMap.remove(apkPath);
		mLoadedPluginMap.remove(entryClass);
	}

	public void holdPluginResource(PluginInfo pluginInfo) {
		mCurrentPluginApk = pluginInfo.getApkPath();
	}

	public void releasePluginResource(PluginInfo pluginInfo) {
		mCurrentPluginApk = "";
	}

	public ClassLoader getClassLoader() {
		ClassLoader classLoader = mClassLoaderMap.get(getCurrentCallerPlugin());
		if (null == classLoader) {
			classLoader = getSuperClassLoader();
		}
		return classLoader;
	}

	private ClassLoader getSuperClassLoader() {
		return mDependence.mSuperClassLoader;
	}

	public AssetManager getAssets() {
		AssetManager assetManager = mAssetMap.get(getCurrentCallerPlugin());
		if (null == assetManager) {
			assetManager = getSuperAssets();
		}
		return assetManager;
	}

	private AssetManager getSuperAssets() {
		return mDependence.mSupAssetManager;
	}

	public Resources getResources() {
		Resources resources = mResourcesMap.get(getCurrentCallerPlugin());
		if (null == resources) {
			resources = getSuperResources();
		}
		return resources;
	}

	private Resources getSuperResources() {
		return mDependence.mSuperResources;
	}

	public Theme getTheme() {
		Theme theme = mThemeMap.get(getCurrentCallerPlugin());
		if (null == theme) {
			theme = getSuperTheme();
		}
		return theme;
	}

	private Theme getSuperTheme() {
		return mDependence.mSuperTheme;
	}

	private String getCurrentCallerPlugin() {
		final StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		final int size = elements.length;
		PluginInfo info = null;
		String mainClassName = "";
		for (int i = 0; i < size; i++) {
			String className = elements[i].getClassName();
			if (className.contains("$")) {
				// 如果是在内部类中调用，依然考察主类名。
				mainClassName = className.substring(0, className.lastIndexOf("$"));
			} else {
				mainClassName = className;
			}
			info = mLoadedPluginMap.get(mainClassName);
			if (null != info) {
				break;
			}
		}
		return (null != info) ? info.getApkPath() : mCurrentPluginApk;
	}

}
