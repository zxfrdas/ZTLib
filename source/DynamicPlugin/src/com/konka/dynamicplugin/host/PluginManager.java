package com.konka.dynamicplugin.host;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.view.View;

import com.konka.dynamicplugin.core.DLClassLoader;
import com.konka.dynamicplugin.core.DLUtils;
import com.konka.dynamicplugin.plugin.IPlugin;

/**
 * 管理所有插件的路径、资源系统、类加载器。
 * <p>
 * 用于载入插件，获取插件视图，切换插件/宿主资源系统。
 */
public class PluginManager {
	private List<PluginInfo> mPlugins;
	private AssetManager mSupAssetManager;
	private Resources mSuperResources;
	private Theme mSuperTheme;
	private ClassLoader mSuperClassLoader;
	private Map<String, AssetManager> mAssetMap;
	private Map<String, Resources> mResourcesMap;
	private Map<String, Theme> mThemeMap;
	private String mCurPluginApkPath;
	private boolean mIsUsePluginResources;
	private Map<String, IPlugin> mActivePluginMap;

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
		mThemeMap = new HashMap<String, Resources.Theme>();
		mActivePluginMap = new HashMap<String, IPlugin>();
		mIsUsePluginResources = false;
	}

	public void setSuperAssetManager(AssetManager assetManager) {
		mSupAssetManager = assetManager;
	}

	public void setSuperResources(Resources resources) {
		mSuperResources = resources;
	}

	public void setSuperTheme(Theme theme) {
		mSuperTheme = theme;
	}

	public void setSuperClassLoader(ClassLoader classLoader) {
		mSuperClassLoader = classLoader;
	}

	/**
	 * 设置使用插件的资源系统，还是宿主的资源系统
	 * 
	 * @param enable
	 *            {@code true}使用插件的系统，{@code false}使用宿主的系统
	 */
	public void setUsePluginResourcesEnable(boolean enable) {
		mIsUsePluginResources = enable;
	}

	/**
	 * 初始话本地所有存在的插件，获取所有插件信息，初始化每个插件的资源系统。
	 * 
	 * @param context
	 *            Application的Context即可
	 */
	public void initPlugins(Context context) {
		File dexFolder = context.getDir("dex", Context.MODE_PRIVATE);
		File pluginFolder = context.getDir("plugins", Context.MODE_PRIVATE);
		File[] pluginFiles = pluginFolder.listFiles();
		mPlugins.clear();
		for (File plugin : pluginFiles) {
			PluginInfo info = new PluginInfo();

			String apkPath = plugin.getAbsolutePath();
			String appName = DLUtils.getAppLabel(context, apkPath).toString();
			String pluginClassName = DLUtils.getAppDescription(context, apkPath)
					.toString();
			String apkName = apkPath.substring(
					apkPath.lastIndexOf(File.separator) + 1,
					apkPath.lastIndexOf("."));

			info.setApkPath(apkPath);
			info.setName(appName);
			info.setPluginClassName(pluginClassName);
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
			Theme theme = resources.newTheme();
			theme.setTo(mSuperTheme);
			mThemeMap.put(info.getApkPath(), theme);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取目前本地存在的所有插件
	 * 
	 * @return 所有插件的信息集合
	 */
	public List<PluginInfo> getPluginInfos() {
		return mPlugins;
	}

	/**
	 * 获取指定插件对应的视图
	 * 
	 * @param context
	 *            使用{@code getHostContext}获取宿主的context对象
	 * @param info
	 *            指定的插件
	 * @return 插件提供的视图
	 * @see IHost
	 */
	public View getPluginView(Context context, PluginInfo info) {
		mCurPluginApkPath = info.getApkPath();
		IPlugin plugin = mActivePluginMap.get(mCurPluginApkPath);
		if (null == plugin) {
			// plugin is not active, launch it
			plugin = launchPlugin(context, info);
			if (null == plugin) throw new NullPointerException("Plugin载入失败");
			mActivePluginMap.put(mCurPluginApkPath, plugin);
		}
		View plugin = launchPlugin(context, info).getPluginView();
		reset();
		return plugin; 
	}

	private IPlugin launchPlugin(Context context, PluginInfo info) {
		IPlugin plugin = null;
		try {
			Class<?> localClass = getPluginClassLoader(context).loadClass(
					info.getPluginClassName());
			Constructor<?> localConstructor = localClass
					.getConstructor(new Class[] {});
			Object instance = localConstructor.newInstance(new Object[] {});
			plugin = (IPlugin) instance;
			mActivePluginMap.put(info.getApkPath(), plugin);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return plugin;
	}

	/**
	 * 宿主页面退出(onDestroy)时调用
	 */
	public void reset() {
		mCurPluginApkPath = "";
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

	public Theme getPluginTheme() {
		Theme theme = null;
		if (mIsUsePluginResources) {
			theme = mThemeMap.get(mCurPluginApkPath);
		}
		return (null != theme) ? theme : mSuperTheme;
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
