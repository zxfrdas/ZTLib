package com.konka.dynamicplugin.core;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.konka.dynamicplugin.database.PluginInfo2DAO;
import com.konka.dynamicplugin.database.PluginInfo2Proxy;
import com.konka.dynamicplugin.plugin.IPlugin;
import com.zt.lib.database.condition.Condition;
import com.zt.lib.database.dao.IDAO;

/**
 * 插件管理类。提供插件的安装/卸载/更新，启用/禁用，获取视图等操作方法。
 * 插件的基本生命周期为：未记录->未安装->已安装->未启用->已启动->被宿主获取并显示。
 * <p>
 * 未记录：指插件APK存在于插件目录下，没有添加进宿主的插件数据库。
 * <p>
 * 未安装：指插件APK存在于插件目录下，已经添加进插件数据库。
 * <p>
 * 已安装：指插件APK已经被导出了对应dex文件。
 * <p>
 * 未启动：指已安装但未标记为启用的插件。宿主UI不应获取其视图显示。
 * <p>
 * 已启动：指已安装并且标记为启用的插件。宿主UI应当获取其视图显示。
 * <p>
 * 一个标准的流程为：应用感知到新（或新版本）插件到来，用户选择安装、启用。 当下次宿主页面呈现出来时，此新插件提供的视图应该显示在宿主页面中。
 */
public final class PluginManager {
	private static final String TAG = PluginManager.class.getSimpleName();
	private static final String SYSTEM_PLUGIN_PATH = "/data/misc/konka/plugins/plugin";
	private IDAO<PluginInfo> mPluginDB;
	private ResourceController mResController;

	private static class InstanceHolder {
		private static PluginManager sInstance = new PluginManager();
	}

	public static PluginManager getInstance() {
		return InstanceHolder.sInstance;
	}

	private PluginManager() {
	}

	/**
	 * 设置宿主资源，供资源控制器进行动态切换
	 * 
	 * @param controller
	 *            宿主/插件资源控制器
	 */
	public void setResourceController(ResourceController controller) {
		mResController = controller;
	}

	/**
	 * 构建宿主应用的插件数据库。
	 * <p>
	 * 如果无数据，则在指定路径下查找插件APK文件并解析插入数据库。
	 * <p>
	 * 路径1:{@code /data/misc/konka/plugins/plugin/}
	 * <p>
	 * 路径2:{@code /data/data/packageName/app_plugins/}
	 * 
	 * @param context
	 *            {@code getApplicationContext()}即可
	 */
	public void initPlugins(Context context) {
		final Context cxt = context.getApplicationContext();
		final File apkDir = checkStoragePathExist(cxt);
		if (!pluginDatabaseExist(cxt)) {
			Log.d(TAG, "plugin Database is Empty");
			File[] apks = apkDir.listFiles();
			Log.d(TAG, "get plugin apks from defalut path");
			for (File f : apks) {
				Log.d(TAG, f.getAbsolutePath());
			}
			savePluginsInfo(cxt, apks);
			Log.d(TAG, "save plugins info done");
		} else {
			// check plugin dir last modify time
			final long dirLastModify = apkDir.lastModified();
			final long recLastModify = getRecordedLastModify(context);
			final long gap = 10 * 1000; // 10s
			if (dirLastModify - recLastModify >= gap) {
				// plugin dir newer then recorded, means there has changed
				// check to know whether there has new plugin or lost
				Log.d(TAG, "plugin dir has been modified");
				final List<PluginInfo> existPlugins = parseAllExistPluginsInfo(cxt,
						apkDir.listFiles());
				final List<PluginInfo> recordedPlugins = getAllRecordedPlugins();
				final List<PluginInfo> recordedAndExist = findIntersection(
						recordedPlugins, existPlugins);
				if (recordedAndExist.isEmpty()) {
					// delete all recorded and uninstall, insert new
					for (PluginInfo info : recordedPlugins) {
						Log.d(TAG,
								"delete outdate recorded plugin = "
										+ info.getApkPath());
						new File(info.getApkPath()).delete();
					}
					mPluginDB.deleteAll();
					mPluginDB.insert(existPlugins);
				} else if (recordedAndExist.size() != recordedPlugins.size()) {
					// delete not exist
					if (recordedPlugins.removeAll(recordedAndExist)) {
						deleteFromRecord(recordedPlugins);
					}
					// insert not record
					if (existPlugins.removeAll(recordedAndExist)) {
						insertIntoRecord(existPlugins);
					}
				} else {
					// update all
					updateRecorded(existPlugins);
				}
			}
		}
		updateRecordedLastModify(context, apkDir.lastModified());
	}

	private boolean pluginDatabaseExist(Context context) {
		mPluginDB = PluginInfo2DAO.getInstance(context);
		if (0 == mPluginDB.getCount()) {
			return false;
		}
		return true;
	}

	private File checkStoragePathExist(Context context) {
		File systemPluginPath = new File(SYSTEM_PLUGIN_PATH);
		if (systemPluginPath.exists()) {
			return systemPluginPath;
		} else {
			File localPluginPath = context.getDir("plugins", Context.MODE_PRIVATE);
			return localPluginPath;
		}
	}

	private void savePluginsInfo(Context context, File[] apks) {
		List<PluginInfo> pluginInfos = parseAllExistPluginsInfo(context, apks);
		mPluginDB.insert(pluginInfos);
	}

	private List<PluginInfo> parseAllExistPluginsInfo(Context context, File[] apks) {
		List<PluginInfo> pluginInfos = new ArrayList<PluginInfo>(apks.length);
		for (File apk : apks) {
			PluginInfo info = parsePluginInfo(context, apk);
			// add list
			pluginInfos.add(info);
		}
		return pluginInfos;
	}

	private PluginInfo parsePluginInfo(Context context, File apk) {
		PluginInfo info = new PluginInfo();
		// apk path
		final String apkPath = apk.getAbsolutePath();
		info.setApkPath(apkPath);
		// apk title
		final String title = DLUtils.getAppLabel(context, apkPath).toString();
		info.setTitle(title);
		// apk entry class
		final String pluginClassName = DLUtils.getAppDescription(context, apkPath)
				.toString();
		info.setEntryClass(pluginClassName);
		// apk dex path
		final String apkName = apkPath.substring(
				apkPath.lastIndexOf(File.separator) + 1, apkPath.lastIndexOf("."));
		String dexPath = context.getDir("dex", Context.MODE_PRIVATE)
				.getAbsolutePath();
		info.setDexPath(dexPath + File.separator + apkName + ".dex");
		// apk icon
		Drawable icon = DLUtils.getAppIcon(context, apkPath);
		info.setIcon(icon);
		// apk version
		int version = DLUtils.getAppVersion(context, apkPath);
		info.setVersion(version);
		return info;
	}

	private List<PluginInfo> findIntersection(List<PluginInfo> one,
			List<PluginInfo> another) {
		List<PluginInfo> result = new ArrayList<PluginInfo>();
		for (PluginInfo f : another) {
			final String apkPath = f.getApkPath();
			for (PluginInfo info : one) {
				if (apkPath.equals(info.getApkPath())) {
					result.add(info);
					break;
				}
			}
		}
		return result;
	}

	private void updateRecorded(List<PluginInfo> newRecord) {
		Log.d(TAG, "updateRecorded");
		Map<PluginInfo, Condition> updates = new HashMap<PluginInfo, Condition>();
		for (PluginInfo info : newRecord) {
			Condition condition = mPluginDB.buildCondition()
					.where(PluginInfo2Proxy.apkPath).equal(info.getApkPath())
					.buildDone();
			updates.put(info, condition);
			Log.d(TAG, "updateRecorded, condition = " + condition);
		}
		mPluginDB.update(updates);
	}

	private void deleteFromRecord(List<PluginInfo> recordedButNotExist) {
		Log.d(TAG, "deleteFromRecord");
		List<Condition> conditions = new ArrayList<Condition>();
		for (PluginInfo info : recordedButNotExist) {
			Condition c = mPluginDB.buildCondition().where(PluginInfo2Proxy.apkPath)
					.equal(info.getApkPath()).buildDone();
			conditions.add(c);
			Log.d(TAG, "deleteFromRecord, condition = " + c);
		}
		mPluginDB.delete(conditions);
	}

	private void insertIntoRecord(List<PluginInfo> existButNotRecorded) {
		Log.d(TAG, "insertIntoRecord");
		for (PluginInfo info : existButNotRecorded) {
			Log.d(TAG, "insertIntoRecord, plugin = " + info.getApkPath());
		}
		mPluginDB.insert(existButNotRecorded);
	}

	private long getRecordedLastModify(Context context) {
		SharedPreferences sp = context.getSharedPreferences("modifyrecorder",
				Context.MODE_PRIVATE);
		return sp.getLong("lastModify", 0);
	}

	private void updateRecordedLastModify(Context context, long lastModified) {
		SharedPreferences sp = context.getSharedPreferences("modifyrecorder",
				Context.MODE_PRIVATE);
		sp.edit().putLong("lastModify", lastModified).apply();
	}

	/**
	 * 获取目前被记录的所有插件，不论是否安装、是否启用。
	 * 
	 * @return 所有已被记录的插件APK信息。无则返回空列表。
	 */
	public List<PluginInfo> getAllRecordedPlugins() {
		return mPluginDB.queryAll();
	}

	/**
	 * 安装指定插件
	 * 
	 * @param context
	 *            {@code getApplicationContext()}即可
	 * @param pluginInfo
	 *            指定插件的插件信息
	 */
	public void installPlugin(Context context, PluginInfo pluginInfo) {
		if (pluginInfo.isInstalled()) {
			Log.d(TAG, "already installed!");
			return;
		}
		final String apkPath = pluginInfo.getApkPath();
		final String dexPath = pluginInfo.getDexPath();
		// query database
		Condition whereApkPath = mPluginDB.buildCondition()
				.where(PluginInfo2Proxy.apkPath).equal(apkPath).buildDone();
		List<PluginInfo> info = mPluginDB.query(whereApkPath);
		if (!info.isEmpty() && info.get(0).isInstalled()) {
			// check for update
			PluginInfo recorded = info.get(0);
			final int recordAppVersion = recorded.getVersion();
			int currentAppVersion = pluginInfo.getVersion();
			if (currentAppVersion > recordAppVersion) {
				new File(pluginInfo.getDexPath()).delete();
				pluginInfo.setEnabled(recorded.isEnabled());
				pluginInfo.setEnableIndex(recorded.getEnableIndex());
				mPluginDB.update(pluginInfo, whereApkPath);
				installPlugin(context, pluginInfo);
			}
		} else {
			// install the apk, output dex
			mResController.installClassLoader(apkPath, dexPath);
			PluginInfo plugin = null;
			if (info.isEmpty()) {
				// new apk, preInstall it
				plugin = parsePluginInfo(context, new File(apkPath));
				plugin.setInstalled(true);
				mPluginDB.insert(plugin);
			} else {
				plugin = info.get(0);
				plugin.setInstalled(true);
				mPluginDB.update(plugin, whereApkPath);
			}
		}
	}

	/**
	 * 卸载指定插件
	 * 
	 * @param context
	 *            {@code getApplicationContext()}即可
	 * @param pluginInfo
	 *            指定插件的插件信息
	 */
	public void uninstallPlugin(Context context, PluginInfo pluginInfo) {
		if (!pluginInfo.isInstalled()) {
			Log.d(TAG, "already uninstall !");
			return;
		}
		final String apkPath = pluginInfo.getApkPath();
		final String dexPath = pluginInfo.getDexPath();
		// query database
		Condition whereApkPath = mPluginDB.buildCondition()
				.where(PluginInfo2Proxy.apkPath).equal(apkPath).buildDone();
		List<PluginInfo> info = mPluginDB.query(whereApkPath);
		if (!info.isEmpty() && info.get(0).isInstalled()) {
			// uninstall
			new File(dexPath).delete();
			// reset info
			PluginInfo plugin = info.get(0);
			plugin.setInstalled(false);
			plugin.setEnabled(false);
			plugin.setEnableIndex(-1);
			plugin.setVersion(1);
			mPluginDB.update(plugin, whereApkPath);
		}
	}

	/**
	 * 获取所有已经被宿主安装的插件
	 * 
	 * @return 所有已安装的插件。无则返回空列表。
	 */
	public List<PluginInfo> getInstalledPlugins() {
		return mPluginDB.query(mPluginDB.buildCondition()
				.where(PluginInfo2Proxy.installed).equal(true).buildDone());
	}

	/**
	 * 获取所有已经启用，可供宿主获取视图显示的插件
	 * <p>
	 * 插件根据用户启用的顺序在返回列表中正序排列。
	 * 
	 * @return 所有已启用的插件。无则返回空列表。
	 */
	public List<PluginInfo> getEnablePlugins() {
		List<PluginInfo> enablePlugins = mPluginDB.query(mPluginDB.buildCondition()
				.where(PluginInfo2Proxy.enabled).equal(true)
				.orderby(PluginInfo2Proxy.enableIndex).buildDone());
		loadPluginsResource(enablePlugins);
		return enablePlugins;
	}

	/**
	 * 启用指定插件
	 * 
	 * @param plugin
	 *            指定插件的信息
	 */
	public void enablePlugin(PluginInfo plugin) {
		Condition whereApkPath = mPluginDB.buildCondition()
				.where(PluginInfo2Proxy.apkPath).equal(plugin.getApkPath())
				.buildDone();
		List<PluginInfo> infos = mPluginDB.query(whereApkPath);
		if (!infos.isEmpty()) {
			PluginInfo info = infos.get(0);
			if (!info.isEnabled()) {
				info.setEnabled(true);
				// check for already enable
				List<PluginInfo> enabledInfos = getEnablePlugins();
				if (enabledInfos.isEmpty()) {
					info.setEnableIndex(0);
				} else {
					final int lastEnableIndex = enabledInfos.get(
							enabledInfos.size() - 1).getEnableIndex();
					info.setEnableIndex(lastEnableIndex + 1);
				}
				mPluginDB.update(info, whereApkPath);
				mResController.loadPluginResource(info);
			}
		}
	}

	/**
	 * 禁用指定插件
	 * 
	 * @param plugin
	 *            指定插件的信息
	 */
	public void disablePlugin(PluginInfo plugin) {
		Condition whereApkPath = mPluginDB.buildCondition()
				.where(PluginInfo2Proxy.apkPath).equal(plugin.getApkPath())
				.buildDone();
		List<PluginInfo> infos = mPluginDB.query(whereApkPath);
		if (!infos.isEmpty()) {
			PluginInfo info = infos.get(0);
			info.setEnabled(false);
			info.setEnableIndex(-1);
			mPluginDB.update(info, whereApkPath);
			mResController.unloadPluginResource(info);
		}
	}

	private void loadPluginsResource(List<PluginInfo> pluginInfos) {
		for (PluginInfo info : pluginInfos) {
			mResController.loadPluginResource(info);
		}
	}

	/**
	 * 获取指定插件提供的视图
	 * 
	 * @param context
	 *            {@code getHostContext()}
	 * @param pluginInfo
	 *            指定插件的信息
	 * @return {@code View} 指定插件提供的视图。如果插件APK未根据要求实现，则返回{@code null}
	 */
	public View getPluginView(Context context, PluginInfo pluginInfo) {
		mResController.holdPluginResource(pluginInfo);
		IPlugin plugin = launchPlugin(context, pluginInfo);
		View view = plugin.getPluginView();
		mResController.releasePluginResource(pluginInfo);
		return view;
	}

	private IPlugin launchPlugin(Context context, PluginInfo pluginInfo) {
		IPlugin plugin = null;
		try {
			Class<?> localClass = mResController.getClassLoader().loadClass(
					pluginInfo.getEntryClass());
			Constructor<?> localConstructor = localClass
					.getConstructor(new Class[] {});
			Object instance = localConstructor.newInstance(new Object[] {});
			plugin = (IPlugin) instance;
			plugin.setContext(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return plugin;
	}

	/**
	 * 获取当前{@code AssetManager}
	 * 
	 * @return 未找到则返回{@code null}
	 */
	public AssetManager getAssets() {
		return (null != mResController) ? mResController.getAssets() : null;
	}

	/**
	 * 获取当前{@code Resources}
	 * 
	 * @return 未找到则返回{@code null}
	 */
	public Resources getResources() {
		return (null != mResController) ? mResController.getResources() : null;
	}

	/**
	 * 获取当前{@code Theme}
	 * 
	 * @return 未找到则返回{@code null}
	 */
	public Theme getTheme() {
		return (null != mResController) ? mResController.getTheme() : null;
	}

	/**
	 * 获取当前{@code ClassLoader}
	 * 
	 * @return 未找到则返回{@code null}
	 */
	public ClassLoader getClassLoader() {
		return (null != mResController) ? mResController.getClassLoader() : null;
	}

}
