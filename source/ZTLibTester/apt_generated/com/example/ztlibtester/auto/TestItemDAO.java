package com.example.ztlibtester.auto;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.zt.lib.database.bean.IBeanProxy;
import com.zt.lib.database.dao.sqlite.SQLite3DAO;
import com.example.ztlibtester.TestItem;
import com.example.ztlibtester.auto.TestItemProxy;

public class TestItemDAO extends SQLite3DAO<TestItem> {
	// com.example.ztlibtester.TestItem
	private static TestItemDAO sInstance;

	public synchronized static TestItemDAO getInstance(Context context) {
		if (null == sInstance) {
			sInstance = new TestItemDAO(context, new TestItemProxy());
		}
		return sInstance;
	}

	private TestItemDAO(Context context, IBeanProxy proxy) {
		super(context, proxy);
	}

	@Override
	protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion, IBeanProxy proxy) {
		db.execSQL("DROP TABLE IF EXISTS " + proxy.getTableName());
		db.execSQL(proxy.getTableCreator());
	}

}