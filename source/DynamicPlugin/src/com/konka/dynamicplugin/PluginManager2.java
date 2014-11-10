package com.konka.dynamicplugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.konka.dynamicplugin.auto.PluginInfo2DAO;
import com.konka.dynamicplugin.auto.PluginInfo2Proxy;
import com.konka.dynamicplugin.core.DLUtils;
import com.zt.lib.database.condition.Condition;
import com.zt.lib.database.dao.IDAO;

public class PluginManager2 {
	private static final String SYSTEM_PLUGIN_PATH = "/data/misc/konka/plugins/plugin";
	private static final String SYSTEM_DEX_PATH = "/data/misc/konka/plugins/dex";
	private IDAO<PluginInfo2> mPluginDB;
	private ResourceController mResController;

	private static class InstanceHolder {
		private static PluginManager2 sInstance = new PluginManager2();
	}

	public static PluginManager2 getInstance() {
		return InstanceHolder.sInstance;
	}

	private PluginManager2() {
	}

	public void setResourceController(ResourceController controller) {
		mResController = controller;
	}

	private boolean checkDatabaseExist(Context context) {
		mPluginDB = PluginInfo2DAO.getInstance(context);
		if (0 == mPluginDB.getCount()) {
			return true;
		}
		return false;
	}

	private File[] checkStoragePathExist(Context context) {
		File systemPluginPath = new File(SYSTEM_PLUGIN_PATH);
		if (systemPluginPath.exists()) {
			return systemPluginPath.listFiles();
		} else {
			File localPluginPath = context.getDir("plugins", Context.MODE_PRIVATE);
			return localPluginPath.listFiles();
		}
	}

	private void savePluginsInfo(Context context, File[] apks) {
		List<PluginInfo2> pluginInfos = new ArrayList<PluginInfo2>(apks.length);
		for (File apk : apks) {
			PluginInfo2 info = parsePluginInfo(context, apk);
			// add list
			pluginInfos.add(info);
		}
		mPluginDB.insert(pluginInfos);
	}

	private PluginInfo2 parsePluginInfo(Context context, File apk) {
		PluginInfo2 info = new PluginInfo2();
		// apk path
		String apkPath = apk.getAbsolutePath();
		info.setApkPath(apkPath);
		// apk title
		String title = DLUtils.getAppLabel(context, apkPath).toString();
		info.setTitle(title);
		// apk entry class
		String pluginClassName = DLUtils.getAppDescription(context, apkPath)
				.toString();
		info.setEntryClass(pluginClassName);
		// apk dex path
		String apkName = apkPath.substring(apkPath.lastIndexOf(File.separator) + 1,
				apkPath.lastIndexOf("."));
		info.setDexPath(SYSTEM_DEX_PATH + File.separator + apkName + ".dex");
		// apk icon
		Drawable icon = DLUtils.getAppIcon(context, apkPath);
		info.setIcon(icon);
		return info;
	}

	public List<PluginInfo2> getPlugins() {
		return mPluginDB.queryAll();
	}

	private void installPlugin(Context context, String apkPath) {
		// query database
		Condition whereApkPath = mPluginDB.buildCondition()
				.where(PluginInfo2Proxy.apkPath).equal(apkPath).buildDone();
		List<PluginInfo2> info = mPluginDB.query(whereApkPath);
		if (!info.isEmpty() && info.get(0).isInstalled()) {
			// already installed, do nothing
			// TODO-check is need update?
		} else {
			// install the apk, output dex
			mResController.installClassLoader(context, apkPath);
			PluginInfo2 plugin = null;
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
	
	public List<PluginInfo2> getInstalledPlugins() {
		return mPluginDB.query(mPluginDB.buildCondition()
				.where(PluginInfo2Proxy.installed).equal(true).buildDone());
	}

	public List<PluginInfo2> getEnablePlugins() {
		return mPluginDB.query(mPluginDB.buildCondition()
				.where(PluginInfo2Proxy.enabled).equal(true)
				.orderby(PluginInfo2Proxy.enableIndex).buildDone());
	}

	public void enablePlugin(PluginInfo2 plugin) {
		Condition whereApkPath = mPluginDB.buildCondition()
				.where(PluginInfo2Proxy.apkPath).equal(plugin.getApkPath())
				.buildDone();
		List<PluginInfo2> infos = mPluginDB.query(whereApkPath);
		if (!infos.isEmpty()) {
			PluginInfo2 info = infos.get(0);
			info.setEnabled(true);
			// check for already enable
			List<PluginInfo2> enabledInfos = getEnablePlugins();
			if (enabledInfos.isEmpty()) {
				info.setEnableIndex(0);
			} else {
				int lastEnableIndex = enabledInfos.get(enabledInfos.size() - 1)
						.getEnableIndex();
				info.setEnableIndex(lastEnableIndex + 1);
			}
			mPluginDB.update(info, whereApkPath);
		}
	}

	public void disablePlugin(PluginInfo2 plugin) {
		Condition whereApkPath = mPluginDB.buildCondition()
				.where(PluginInfo2Proxy.apkPath).equal(plugin.getApkPath())
				.buildDone();
		List<PluginInfo2> infos = mPluginDB.query(whereApkPath);
		if (!infos.isEmpty()) {
			PluginInfo2 info = infos.get(0);
			info.setEnabled(false);
			info.setEnableIndex(-1);
			mPluginDB.update(info, whereApkPath);
		}
	}
	
	private void loadPluginsResource(List<PluginInfo2> pluginInfos) {
		for (PluginInfo2 info : pluginInfos) {
			mResController.loadPluginResource(info);
		}
	}
	
	private void loadPluginResource(PluginInfo2 pluginInfo) {
		mResController.loadPluginResource(pluginInfo);
	}
	
	

}
