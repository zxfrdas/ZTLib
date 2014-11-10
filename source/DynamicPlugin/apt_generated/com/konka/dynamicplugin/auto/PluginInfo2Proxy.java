package com.konka.dynamicplugin.auto;

import com.konka.dynamicplugin.PluginInfo2;

import com.zt.lib.database.bean.IBeanProxy;

public class PluginInfo2Proxy implements IBeanProxy {
	// com.konka.dynamicplugin.PluginInfo2
	public static final String apkPath = "ApkPath";
	public static final String dexPath = "DexPath";
	public static final String enableIndex = "Index";
	public static final String enabled = "Enable";
	public static final String entryClass = "EntryClass";
	public static final String icon = "Icon";
	public static final String installed = "Install";
	public static final String title = "Title";
	private static final String DATABASE_NAME = "plugin";
	private static final int VERSION = 1;
	private static final String TABLE = "plugins";
	private static final String TABLE_CREATOR = "create table plugins(_id integer primary key autoincrement, Title TEXT, ApkPath TEXT, DexPath TEXT, EntryClass TEXT, Icon BLOB, Install INTEGER, Enable INTEGER, Index INTEGER);";

	@Override
	public String getDataBaseName() {
		return DATABASE_NAME;
	}

	@Override
	public int getDataBaseVersion() {
		return VERSION;
	}

	@Override
	public String getTableName() {
		return TABLE;
	}

	@Override
	public String getTableCreator() {
		return TABLE_CREATOR;
	}

	@Override
	public Class<?> getBeanClass() {
		return PluginInfo2.class;
	}

}