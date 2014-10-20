package com.zt.lib.database.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.zt.lib.database.SQLDataType;


public class SQLBeanParser {
	private String table;
	private String tableCreator;
	private ColumnItem primary;
	private Map<String, ColumnItem> field_Item;
	
	public static class ColumnItem {
		public int index;
		public String name;
		public SQLDataType type;
		public Field field;
	}

	public SQLBeanParser() {
		field_Item = new HashMap<String, ColumnItem>();
	}

	public void analyze(Class<?> clazz) {
		Annotation t = clazz.getAnnotation(Table.class);
		if (null == t)
			throw new NullPointerException("Must Have Table Name");
		table = ((Table) t).name();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			Annotation c = field.getAnnotation(Column.class);
			if (null != c) {
				ColumnItem column = new ColumnItem();
				column.index = ((Column) c).index();
				column.name = ((Column) c).name();
				column.type = ((Column) c).type();
				column.field = field;
				if (0 == column.index) {
					// primary id
					primary = column;
				}
				field_Item.put(field.getName(), column);
			}
		}
		tableCreator = crateTable();
	}

	private String crateTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("create table ").append(table).append("(");
		int startIndex = 0;
		if (null == primary) {
			// 数据类未声明index=0的键，由我们创建一个。
			primary = new ColumnItem();
			primary.index = 0;
			primary.name = "_id";
			sb.append(primary.name).append(" integer primary key autoincrement, ");
			startIndex = 1;
		}
		final int total = field_Item.size() + startIndex;
		// 转换为了按Column中声明的index顺序构造sql语句。
		Map<Integer, ColumnItem> indexMap = new HashMap<Integer, ColumnItem>();
		for (ColumnItem item : field_Item.values()) {
			indexMap.put(item.index, item);
		}
		for (int i = startIndex; i < total; i ++) {
			ColumnItem item = indexMap.get(i);
			sb.append(item.name).append(" ").append(item.type.toString());
			if (item.index == (total - 1)) {
				sb.append(");");
			} else {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	public String getTableCreator() {
		return tableCreator;
	}

	public String getTableName() {
		return table;
	}

	public String getColumnName(String fieldName) {
		return field_Item.get(fieldName).name;
	}
	
	public Collection<ColumnItem> getAllColumnItem() {
		return field_Item.values();
	}

}
