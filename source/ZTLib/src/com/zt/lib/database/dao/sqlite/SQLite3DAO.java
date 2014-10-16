package com.zt.lib.database.dao.sqlite;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.zt.lib.database.SQLDataType;
import com.zt.lib.database.bean.SQLBeanParser;
import com.zt.lib.database.bean.SQLBeanParser.ColumnItem;
import com.zt.lib.database.condition.Condition;
import com.zt.lib.database.condition.IConditionBuilder;
import com.zt.lib.database.condition.sqlite.SQLiteConditionBuilder;
import com.zt.lib.database.dao.IDAO;

/**
 * 本工程所有数据库操作类均需继承此类。
 * <p>
 * 提供读/写锁，数据库{@code SQLiteDatabase}实例
 * 
 * @see SQLiteDatabase
 */
public abstract class SQLite3DAO<T> extends SQLiteOpenHelper implements IDAO<T> {

	private final ReadLock mReadLock;
	private final WriteLock mWriteLock;
	private SQLiteDatabase mDatabase;
	private SQLBeanParser mParser;
	private String tableName;
	private Class<?> clazz;

	public SQLite3DAO(Context context, String name, int version, Class<?> bean) {
		super(context, name, null, version);
		clazz = bean;
		mParser = SQLBeanParser.getInstace();
		mParser.analyze(clazz);
		tableName = mParser.getTableName();
		final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		mReadLock = lock.readLock();
		mWriteLock = lock.writeLock();
		mDatabase = getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(mParser.getTableCreator());
	}

	@Override
	public boolean insert(T item) {
		long ret = -1;
		ContentValues values = setColumnToContentValue(mParser.getAllColumnItem(),
				item);
		mWriteLock.lock();
		try {
			ret = mDatabase.insert(tableName, null, values);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			mWriteLock.unlock();
		}
		if (-1 != ret) {
			return true;
		}
		return false;
	}

	@Override
	public boolean insert(Collection<T> items) {
		long ret = -1;
		ArrayList<ContentValues> values = new ArrayList<ContentValues>();
		for (T item : items) {
			ContentValues value = setColumnToContentValue(
					mParser.getAllColumnItem(), item);
			values.add(value);
		}
		mWriteLock.lock();
		mDatabase.beginTransaction();
		try {
			for (ContentValues v : values) {
				ret = mDatabase.insert(tableName, null, v);
			}
			mDatabase.setTransactionSuccessful();
		} catch (SQLiteException e) {
			e.printStackTrace();
			ret = -1;
		} finally {
			mDatabase.endTransaction();
			mWriteLock.unlock();
		}
		if (-1 != ret) {
			return true;
		}
		return false;
	}

	@Override
	public boolean delete(Condition condition) {
		long ret = 0;
		mWriteLock.lock();
		try {
			ret = mDatabase.delete(tableName, condition.getSelection(),
					condition.getSelectionArgs());
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			mWriteLock.unlock();
		}
		if (0 != ret) {
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteAll() {
		long ret = 0;
		mWriteLock.lock();
		try {
			ret = mDatabase.delete(tableName, null, null);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			mWriteLock.unlock();
		}
		if (1 == ret) {
			return true;
		}
		return false;
	}

	@Override
	public boolean update(T item, Condition condition) {
		long ret = -1;
		ContentValues value = setColumnToContentValue(mParser.getAllColumnItem(),
				item);
		mWriteLock.lock();
		try {
			ret = mDatabase.update(tableName, value, condition.getSelection(),
					condition.getSelectionArgs());
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			mWriteLock.unlock();
		}
		if (-1 != ret) {
			return true;
		}
		return false;
	}

	@Override
	public boolean update(Collection<T> items, Condition condition) {
		long ret = -1;
		ArrayList<ContentValues> values = new ArrayList<ContentValues>();
		for (T item : items) {
			ContentValues value = setColumnToContentValue(
					mParser.getAllColumnItem(), item);
			values.add(value);
		}
		mWriteLock.lock();
		mDatabase.beginTransaction();
		try {
			for (ContentValues value : values) {
				ret = mDatabase.update(tableName, value, condition.getSelection(),
						condition.getSelectionArgs());
			}
			mDatabase.endTransaction();
		} catch (SQLiteException e) {
			e.printStackTrace();
			ret = -1;
		} finally {
			mDatabase.endTransaction();
			mWriteLock.unlock();
		}
		if (-1 != ret) {
			return true;
		}
		return false;
	}

	@Override
	public boolean update(Map<T, Condition> updates) {
		long ret = -1;
		Map<ContentValues, Condition> values = new HashMap<ContentValues, Condition>();
		for (Entry<T, Condition> update : updates.entrySet()) {
			ContentValues value = setColumnToContentValue(
					mParser.getAllColumnItem(), update.getKey());
			values.put(value, update.getValue());
		}
		mWriteLock.lock();
		mDatabase.beginTransaction();
		try {
			for (Entry<ContentValues, Condition> value : values.entrySet()) {
				ret = mDatabase.update(tableName, value.getKey(), value.getValue()
						.getSelection(), value.getValue().getSelectionArgs());
			}
			mDatabase.endTransaction();
		} catch (SQLiteException e) {
			e.printStackTrace();
			ret = -1;
		} finally {
			mDatabase.endTransaction();
			mWriteLock.unlock();
		}
		if (-1 != ret) {
			return true;
		}
		return false;
	}

	@Override
	public List<T> query(Condition condition) {
		Cursor c = null;
		mReadLock.lock();
		try {
			c = mDatabase
					.query(tableName, null, condition.getSelection(),
							condition.getSelectionArgs(), null, null,
							condition.getOrderBy());
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			mReadLock.unlock();
		}
		List<T> items = new ArrayList<T>();
		try {
			items = setCursorValueToBean(c, mParser.getAllColumnItem());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return items;
	}

	@Override
	public Collection<T> queryAll() {
		Cursor c = null;
		mReadLock.lock();
		try {
			c = mDatabase.query(tableName, null, null, null, null, null, null);
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			mReadLock.unlock();
		}
		List<T> items = new ArrayList<T>();
		try {
			items = setCursorValueToBean(c, mParser.getAllColumnItem());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return items;
	}

	@Override
	public int getCount() {
		mReadLock.lock();
		int count = mDatabase.query(tableName, null, null, null, null, null, null)
				.getCount();
		mReadLock.unlock();
		return count;
	}

	@Override
	public IConditionBuilder buildCondition() {
		return new SQLiteConditionBuilder(mParser);
	}

	private ContentValues setColumnToContentValue(Collection<ColumnItem> items,
			T bean) {
		ContentValues values = new ContentValues();
		for (ColumnItem item : items) {
			final String name = item.name;
			final SQLDataType type = item.type;
			final Field field = item.field;
			try {
				if (SQLDataType.BLOB == type) {
					values.put(name, (byte[]) field.get(bean));
				} else if (SQLDataType.INTEGER == type) {
					if (boolean.class.equals(type) || Boolean.class.equals(type)) {
						values.put(name, field.getBoolean(bean) ? 1 : 0);
					} else if (int.class.equals(type) || Integer.class.equals(type)) {
						values.put(name, field.getInt(bean));
					} else if (long.class.equals(type) || Long.class.equals(type)) {
						values.put(name, field.getLong(bean));
					} else if (short.class.equals(type) || Short.class.equals(type)) {
						values.put(name, field.getShort(bean));
					}
				} else if (SQLDataType.REAL == type) {
					if (float.class.equals(type) || Float.class.equals(type)) {
						values.put(name, field.getFloat(bean));
					} else if (double.class.equals(type)
							|| Double.class.equals(type)) {
						values.put(name, field.getDouble(bean));
					}
				} else if (SQLDataType.TEXT == type) {
					values.put(name, field.get(bean).toString());
				} else if (SQLDataType.NULL == type) {
					values.putNull(name);
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		return values;
	}

	@SuppressWarnings("unchecked")
	private List<T> setCursorValueToBean(Cursor cursor, Collection<ColumnItem> items)
			throws IllegalAccessException, IllegalArgumentException {
		List<T> beans = new ArrayList<T>();
		while (null != cursor && cursor.moveToNext()) {
			T item = null;
			try {
				item = (T) clazz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
			for (ColumnItem ci : items) {
				final int index = ci.index;
				final SQLDataType type = ci.type;
				final Field field = ci.field;
				final Class<?> fieldType = field.getType();
				if (SQLDataType.BLOB == type) {
					field.set(item, cursor.getBlob(index));
				} else if (SQLDataType.INTEGER == type) {
					if (boolean.class.equals(fieldType)
							|| Boolean.class.equals(fieldType)) {
						field.set(item, 1 == cursor.getInt(index) ? true : false);
					} else if (int.class.equals(fieldType)
							|| Integer.class.equals(fieldType)) {
						field.set(item, cursor.getInt(index));
					} else if (long.class.equals(fieldType)
							|| Long.class.equals(fieldType)) {
						field.set(item, cursor.getLong(index));
					} else if (short.class.equals(fieldType)
							|| Short.class.equals(fieldType)) {
						field.set(item, cursor.getShort(index));
					}
				} else if (SQLDataType.REAL == type) {
					if (float.class.equals(fieldType)
							|| Float.class.equals(fieldType)) {
						field.set(item, cursor.getFloat(index));
					} else if (double.class.equals(fieldType)
							|| Double.class.equals(fieldType)) {
						field.set(item, cursor.getDouble(index));
					}
				} else if (SQLDataType.TEXT == type) {
					field.set(item, cursor.getString(index));
				} else if (SQLDataType.NULL == type) {
					field.set(item, null);
				}
			}
			beans.add(item);
		}
		return beans;
	}

}
