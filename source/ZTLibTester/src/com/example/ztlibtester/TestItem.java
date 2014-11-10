package com.example.ztlibtester;

import com.zt.lib.database.Column;
import com.zt.lib.database.Database;
import com.zt.lib.database.SQLDataType;
import com.zt.lib.database.Table;

@Database(name="test.db", version=1)
@Table(name="tbl_Test")
public class TestItem {
	
	@Column(index=1, name="column_1", type=SQLDataType.INTEGER)
	public int testInt;
	
	@Column(index=2, name="column_2", type=SQLDataType.REAL)
	public double testFloat;
	
	@Column(index=3, name="column_3", type=SQLDataType.INTEGER)
	public boolean testBoolean;
	
	@Column(index=4, name="column_4", type=SQLDataType.TEXT)
	public String testString;
	
}
