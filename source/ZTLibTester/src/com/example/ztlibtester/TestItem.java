package com.example.ztlibtester;

import com.zt.lib.database.SQLDataType;
import com.zt.lib.database.bean.Column;
import com.zt.lib.database.bean.Table;

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

	public int getTestInt() {
		return testInt;
	}

	public void setTestInt(int testInt) {
		this.testInt = testInt;
	}

	public double getTestFloat() {
		return testFloat;
	}

	public void setTestFloat(double testFloat) {
		this.testFloat = testFloat;
	}

	public boolean isTestBoolean() {
		return testBoolean;
	}

	public void setTestBoolean(boolean testBoolean) {
		this.testBoolean = testBoolean;
	}

	public String getTestString() {
		return testString;
	}

	public void setTestString(String testString) {
		this.testString = testString;
	}
	
}
