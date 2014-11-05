package com.zt.lib.database.apt;

public interface IBeanProxy {
	String getDataBaseName();
	int getDataBaseVersion();
	String getTableName();
	String getTableCreator();
	Class<?> getBeanClass();
}
