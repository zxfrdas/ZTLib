package com.zt.daotest;

import com.zt.lib.database.util.Column;
import com.zt.lib.database.util.SQLite3DataType;
import com.zt.lib.database.util.Table;

@Table(name="tbl_Test")
public class TestItem {
	
	@Column(index=1, name="column_1", type=SQLite3DataType.INTEGER)
	public int testInt;
	
	@Column(index=2, name="column_2", type=SQLite3DataType.REAL)
	public double testFloat;
	
	@Column(index=3, name="column_3", type=SQLite3DataType.INTEGER)
	public boolean testBoolean;
	
	@Column(index=4, name="column_4", type=SQLite3DataType.TEXT)
	public String testString;
	
}
