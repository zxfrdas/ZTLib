package com.example.ztlibtester.auto;

import com.example.ztlibtester.TestItem;

import com.zt.lib.database.apt.IBeanProxy;

public class TestItemProxy implements IBeanProxy {
	// com.example.ztlibtester.TestItem
	public static final String testBoolean = "column_3";
	public static final String testFloat = "column_2";
	public static final String testInt = "column_1";
	public static final String testString = "column_4";
	private final String DATABASE_NAME = "test.db";
	private final int VERSION = 1;
	private final String TABLE = "tbl_Test";
	private final String TABLE_CREATOR = "create table tbl_Test(_id integer primary key autoincrement, column_1 INTEGER, column_2 REAL, column_3 INTEGER, column_4 TEXT);";

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
		return TestItem.class;
	}

}