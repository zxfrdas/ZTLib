package com.zt.lib.config.ReaderWriterImpl;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.zt.lib.config.ReaderWriter;

public class XmlReaderWriterImpl implements ReaderWriter {

	private WeakReference<Context> mContextRef;
	private SharedPreferences mSharedPref;
	private Editor mSpEditor;
	private String mFileName;

	@Override
	public void loadFile(String name, Context context) throws IOException {
		mContextRef = new WeakReference<Context>(context);
		mFileName = name;
		mSharedPref = mContextRef.get().getSharedPreferences(mFileName,
				Context.MODE_MULTI_PROCESS);
		mSpEditor = mSharedPref.edit();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object get(String name) {
		Object o = null;
		if (null != mSharedPref) {
			if (mSharedPref.getAll().get(name) instanceof Set<?>) {
				Set<String> set = (Set<String>) mSharedPref.getAll().get(name);
				String[] strings = new String[set.size()];
				o = set.toArray(strings);
			}
		}
		return o;
	}

	@Override
	public int getInt(String name) {
		int i = 0;
		if (null != mSharedPref) {
			i = mSharedPref.getInt(name, 0);
		}
		return i;
	}

	@Override
	public boolean getBoolean(String name) {
		boolean b = false;
		if (null != mSharedPref) {
			b = mSharedPref.getBoolean(name, false);
		}
		return b;
	}

	@Override
	public String getString(String name) {
		String s = "";
		if (null != mSharedPref) {
			s = mSharedPref.getString(name, "");
		}
		return s;
	}

	@Override
	public String[] getStringArray(String name) {
		String[] sArray = null;
		if (null != mSharedPref) {
			Set<String> set = null;
			set = mSharedPref.getStringSet(name, new HashSet<String>());
			String[] strings = new String[set.size()];
			sArray = set.toArray(strings);
		}
		return sArray;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, ?> getAll() {
		Map<String, Object> m = new Hashtable<String, Object>();
		Object o = null;
		if (null != mSharedPref) {
			for (Map.Entry<String, ?> entry : mSharedPref.getAll().entrySet()) {
				o = entry.getValue();
				if (o instanceof Set<?>) {
					Set<String> set = (Set<String>) entry.getValue();
					String[] strings = new String[set.size()];
					o = set.toArray(strings);
				}
				m.put(entry.getKey(), o);
			}
		}
		return m;
	}

	@Override
	public ReaderWriter set(String name, Object value) {
		if (null != mSharedPref) {
			setByType(name, value);
		}
		return this;
	}

	private void setByType(String name, Object value) {
		Class<?> c = value.getClass();
		if (int.class.equals(c) || Integer.class.equals(c)) {
			mSpEditor.putInt(name, Integer.valueOf(value.toString()));
		} else if (float.class.equals(c) || Float.class.equals(c)) {
			mSpEditor.putFloat(name, Float.valueOf(value.toString()));
		} else if (long.class.equals(c) || Long.class.equals(c)) {
			mSpEditor.putLong(name, Long.valueOf(value.toString()));
		} else if (boolean.class.equals(c) || Boolean.class.equals(c)) {
			mSpEditor.putBoolean(name, Boolean.valueOf(value.toString()));
		} else if (String.class.equals(c)) {
			mSpEditor.putString(name, value.toString());
		} else if (String[].class.equals(c)) {
			Set<String> setValue = new HashSet<String>();
			for (String s : (String[]) value) {
				setValue.add(s);
			}
			mSpEditor.putStringSet(name, setValue);
		}
	}

	@Override
	public ReaderWriter setInt(String name, int value) {
		if (null != mSharedPref) {
			mSpEditor.putInt(name, value);
		}
		return this;
	}

	@Override
	public ReaderWriter setBoolean(String name, boolean value) {
		if (null != mSharedPref) {
			mSpEditor.putBoolean(name, value);
		}
		return this;
	}

	@Override
	public ReaderWriter setString(String name, String value) {
		if (null != mSharedPref) {
			mSpEditor.putString(name, value);
		}
		return this;
	}

	@Override
	public ReaderWriter setStringArray(String name, String[] value) {
		if (null != mSharedPref) {
			Set<String> set = new HashSet<String>();
			for (String s : value) {
				set.add(s);
			}
			mSpEditor.putStringSet(name, set);
		}
		return this;
	}

	@Override
	public ReaderWriter setAll(Map<String, ?> value) {
		if (null != mSharedPref) {
			for (Map.Entry<String, ?> entry : value.entrySet()) {
				setByType(entry.getKey(), entry.getValue());
			}
		}
		return this;
	}

	@Override
	public void commit() throws IOException {
		if (null != mSharedPref) {
			mSpEditor.commit();
		}
	}

}
