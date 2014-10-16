package com.example.ztlibtester;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.zt.lib.database.dao.sqlite.SQLite3DAO;

public class TestDAO extends SQLite3DAO<TestItem> {

	public TestDAO(Context context, Class<?> item) {
		super(context, "test.db", 1, item);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
