package com.example.ztlibtester;

import android.content.Context;

import com.zt.lib.database.dao.sqlite.SQLite3DAO;

public class TestDAO extends SQLite3DAO<TestItem> {

	public TestDAO(Context context, Class<?> item) {
		super(context, "test.db", 1, item);
	}
	

}
