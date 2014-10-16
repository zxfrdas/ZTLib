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

	private static class InstanceHolder {
		private static SQLBeanParser sInstance = new SQLBeanParser();
	}

	public static SQLBeanParser getInstace() {
		return InstanceHolder.sInstance;
	}

	private SQLBeanParser() {
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
				if (0 == ((Column) c).index()) {
					// primary id, ignore
					primary = new ColumnItem();
					primary.index = 0;
					primary.name = ((Column) c).name();
					continue;
				}
				column.index = ((Column) c).index();
				column.name = ((Column) c).name();
				column.type = ((Column) c).type();
				column.field = field;
				field_Item.put(field.getName(), column);
			}
		}
		tableCreator = crateTable();
	}

	private String crateTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("create table ").append(table);
		if (null == primary) {
			primary = new ColumnItem();
			primary.index = 0;
			primary.name = "_id";
		}
		sb.append("(").append(primary.name)
				.append(" integer primary key autoincrement, ");
		final int total = field_Item.size();
		// 转换为了按Column中声明的index顺序构造sql语句。
		Map<Integer, ColumnItem> indexMap = new HashMap<Integer, ColumnItem>();
		for (ColumnItem item : field_Item.values()) {
			indexMap.put(item.index, item);
		}
		for (int i = 1; i <= total; i ++) {
			ColumnItem item = indexMap.get(i);
			sb.append(item.name).append(" ").append(item.type.toString());
			if (item.index < total) {
				sb.append(", ");
			} else {
				sb.append(");");
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
