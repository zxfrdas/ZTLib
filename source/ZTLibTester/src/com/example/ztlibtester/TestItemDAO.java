package com.example.ztlibtester;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zt.lib.database.impl.SQLite3DAO;

public class TestItemDAO extends SQLite3DAO<TestItem> {

	public TestItemDAO(Context context, Class<?> item)
	{
		super(context, "test.db", 1, item);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(mItemProxy.getTableCreator());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		// TODO Auto-generated method stub
	}

}
