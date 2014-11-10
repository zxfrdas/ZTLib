package com.konka.dynamicplugin;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;

import com.konka.dynamicplugin.core.DLClassLoader;

public class ResourceController {
	private Dependence mDependence;
	private Map<String, AssetManager> mAssetMap;
	private Map<String, Resources> mResourcesMap;
	private Map<String, Theme> mThemeMap;
	private Map<String, ClassLoader> mClassLoaderMap;

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
	}

	public void installClassLoader(Context context, String apkPath) {
		DLClassLoader.createClassLoader(apkPath, context.getApplicationContext(),
				mDependence.mSuperClassLoader);
	}

	public void loadPluginResource(PluginInfo2 pluginInfo) {
		try {
			// classloader
			mClassLoaderMap.put(pluginInfo.getApkPath(), DLClassLoader
					.getExistClassLoader(pluginInfo.getApkPath(),
							pluginInfo.getDexPath(), getSuperClassLoader()));
			// asset
			AssetManager assetManager = AssetManager.class.newInstance();
			Method addAssetPath = assetManager.getClass().getMethod("addAssetPath",
					String.class);
			addAssetPath.invoke(assetManager, pluginInfo.getApkPath());
			mAssetMap.put(pluginInfo.getApkPath(), assetManager);
			// resource
			Resources resources = new Resources(assetManager, getSuperResources()
					.getDisplayMetrics(), getSuperResources().getConfiguration());
			mResourcesMap.put(pluginInfo.getApkPath(), resources);
			// theme
			Theme theme = resources.newTheme();
			theme.setTo(getSuperTheme());
			mThemeMap.put(pluginInfo.getApkPath(), theme);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ClassLoader getSuperClassLoader() {
		return mDependence.mSuperClassLoader;
	}

	public AssetManager getSuperAssetManager() {
		return mDependence.mSupAssetManager;
	}

	public Resources getSuperResources() {
		return mDependence.mSuperResources;
	}

	public Theme getSuperTheme() {
		return mDependence.mSuperTheme;
	}

}
