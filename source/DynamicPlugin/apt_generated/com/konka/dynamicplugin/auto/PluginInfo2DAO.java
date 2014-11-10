package com.konka.dynamicplugin.auto;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.zt.lib.database.bean.IBeanProxy;
import com.zt.lib.database.dao.sqlite.SQLite3DAO;
import com.konka.dynamicplugin.PluginInfo2;
import com.konka.dynamicplugin.auto.PluginInfo2Proxy;

public class PluginInfo2DAO extends SQLite3DAO<PluginInfo2> {
	// com.konka.dynamicplugin.PluginInfo2
	private static PluginInfo2DAO sInstance;

	public synchronized static PluginInfo2DAO getInstance(Context context) {
		if (null == sInstance) {
			sInstance = new PluginInfo2DAO(context, new PluginInfo2Proxy());
		}
		return sInstance;
	}

	private PluginInfo2DAO(Context context, IBeanProxy proxy) {
		super(context, proxy);
	}

	@Override
	protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion, IBeanProxy proxy) {
		db.execSQL("DROP TABLE IF EXISTS " + proxy.getTableName());
		db.execSQL(proxy.getTableCreator());
	}

}