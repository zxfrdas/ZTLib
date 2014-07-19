package com.zt.lib.database.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ContentValues;
import android.database.Cursor;

public class ItemProxy<T> {
	
	private static class ColumnItem {
		int index;
		String name;
		SQLite3DataType type;
		Field field;
	}
	
	private static class RowItem {
		String table;
		ColumnItem primary;
		Map<Integer, ColumnItem> index_Item = new HashMap<Integer, ItemProxy.ColumnItem>();
		Map<String, ColumnItem> name_Item = new HashMap<String, ItemProxy.ColumnItem>();
		Map<String, ColumnItem> field_Item = new HashMap<String, ItemProxy.ColumnItem>();
	}
	
	private Class<?> clazz;
	private RowItem row;
	
	public ItemProxy(Class<?> item) {
		this.clazz = item;
		this.row = new RowItem();
		analyzeItem();
	}
	
	private void analyzeItem() {
		Annotation t = clazz.getAnnotation(Table.class);
		if (null == t) throw new NullPointerException("Must Have Table Name");
		row.table = ((Table) t).name();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			Annotation c = field.getAnnotation(Column.class);
			if (null != c) {
				ColumnItem column = new ColumnItem();
				if (0 == ((Column) c).index()) {
					// primary id, ignore
					row.primary = new ColumnItem();
					row.primary.index = 0;
					row.primary.name = ((Column) c).name();
					continue;
				}
				column.index = ((Column) c).index();
				column.name = ((Column) c).name();
				column.type = ((Column) c).type();
				column.field = field;
				row.index_Item.put(column.index, column);
				row.name_Item.put(column.name, column);
				row.field_Item.put(field.getName(), column);
			}
		}
	}
	
	public String getTableName() {
		return row.table;
	}
	
	public String getTableCreator() {
		StringBuilder sb = new StringBuilder();
		sb.append("create table ").append(row.table);
		if (null == row.primary) {
			row.primary = new ColumnItem();
			row.primary.index = 0;
			row.primary.name = "_id";
		}
		sb.append("(").append(row.primary.name).append(" integer primary key autoincrement, ");
		final int total = row.name_Item.size();
		int index = 0;
		for (ColumnItem item : row.name_Item.values()) {
			sb.append(item.name).append(" ").append(item.type.toString());
			index ++;
			if (index < total) {
				sb.append(", ");
			} else {
				sb.append(");");
			}
		}
		return sb.toString();
	}
	
	public List<T> getItemFromDB(Cursor c) throws IllegalAccessException,
			IllegalArgumentException {
		List<T> items = new ArrayList<T>();
		while (null != c && c.moveToNext()) {
			T item = null;
			try {
				item = (T) clazz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
			for (Entry<Integer, ColumnItem> map : row.index_Item.entrySet()) {
				int index = map.getKey();
				SQLite3DataType sqlite3type = map.getValue().type;
				Field field = map.getValue().field;
				Class<?> fieldType = field.getType();
				switch (sqlite3type)
				{
				case BLOB:
					field.set(item, c.getBlob(index));
					break;
					
				case INTEGER:
					if (boolean.class.equals(fieldType) || Boolean.class.equals(fieldType)) {
						field.set(item, 1 == c.getInt(index) ? true : false);
					} else if (int.class.equals(fieldType) || Integer.class.equals(fieldType)) {
						field.set(item, c.getInt(index));
					} else if (long.class.equals(fieldType) || Long.class.equals(fieldType)) {
						field.set(item, c.getLong(index));
					} else if (short.class.equals(fieldType) || Short.class.equals(fieldType)) {
						field.set(item, c.getShort(index));
					}
					break;
					
				case REAL:
					if (float.class.equals(fieldType) || Float.class.equals(fieldType)) {
						field.set(item, c.getFloat(index));
					} else if (double.class.equals(fieldType) || Double.class.equals(fieldType)) {
						field.set(item, c.getDouble(index));
					}
					break;
					
				case TEXT:
					field.set(item, c.getString(index));
					break;
					
				case NULL:
				default:
					break;
				}
			}
			items.add(item);
		}
		if (null != c) {
			c.close();
		}
		return items;
	}
	
	public ContentValues getContentValues(T item) throws IllegalAccessException,
			IllegalArgumentException {
		return getContentValues(item,
				row.field_Item.keySet().toArray(new String[row.field_Item.keySet().size()]));
	}
	
	public ContentValues getContentValues(T item, String... fields)
			throws IllegalAccessException, IllegalArgumentException {
		ContentValues values = new ContentValues();
		for (String column : fields) {
			if (!row.field_Item.containsKey(column)) {
				return values;
			}
			ColumnItem need = row.field_Item.get(column);
			String name = need.name;
			SQLite3DataType sqlite3type = need.type;
			Field field = need.field;
			Class<?> fieldType = field.getType();
			switch (sqlite3type)
			{
			case BLOB:
				values.put(name, (byte[])field.get(item));
				break;
				
			case INTEGER:
				if (boolean.class.equals(fieldType) || Boolean.class.equals(fieldType)) {
					values.put(name, field.getBoolean(item) ? 1 : 0);
				} else if (int.class.equals(fieldType) || Integer.class.equals(fieldType)) {
					values.put(name, field.getInt(item));
				} else if (long.class.equals(fieldType) || Long.class.equals(fieldType)) {
					values.put(name, field.getLong(item));
				} else if (short.class.equals(fieldType) || Short.class.equals(fieldType)) {
					values.put(name, field.getShort(item));
				}
				break;
				
			case REAL:
				if (float.class.equals(fieldType) || Float.class.equals(fieldType)) {
					values.put(name, field.getFloat(item));
				} else if (double.class.equals(fieldType) || Double.class.equals(fieldType)) {
					values.put(name, field.getDouble(item));
				}
				break;
				
			case TEXT:
				values.put(name, field.get(item) + "");
				break;
				
			case NULL:
			default:
				break;
			}
		}
		return values;
	}
	
	public String[] getColumnName(String... fieldName) {
		String[] result = new String[fieldName.length];
		int index = 0;
		for (String s : fieldName) {
			result[index] = row.field_Item.get(s).name;
			index ++;
		}
		return result;
	}
	
}
