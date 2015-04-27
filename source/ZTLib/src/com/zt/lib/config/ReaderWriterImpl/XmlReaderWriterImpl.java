package com.zt.lib.config.ReaderWriterImpl;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.zt.lib.config.StringListReaderWriter;

public class XmlReaderWriterImpl implements StringListReaderWriter {

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

	@Override
	public List<Integer> getInt(String name) {
		List<Integer> results = new ArrayList<Integer>();
		if (null != mSharedPref) {
			Set<String> v = mSharedPref.getStringSet(name, null);
			if (null != v) {
				for (String s : v) {
					results.add(Integer.valueOf(s));
				}
			} else {
				final int result = mSharedPref.getInt(name, Integer.MIN_VALUE);
				if (Integer.MIN_VALUE != result) {
					results.add(result);
				}
			}
		}
		return results;
	}

	@Override
	public List<Boolean> getBoolean(String name) {
		List<Boolean> results = new ArrayList<Boolean>();
		if (null != mSharedPref) {
			Set<String> v = mSharedPref.getStringSet(name, null);
			if (null != v) {
				for (String s : v) {
					results.add(Boolean.valueOf(s));
				}
			} else {
				results.add(mSharedPref.getBoolean(name, false));
			}
		}
		return results;
	}

	@Override
	public List<String> getString(String name) {
		List<String> results = new ArrayList<String>();
		if (null != mSharedPref) {
			Set<String> v = mSharedPref.getStringSet(name, null);
			if (null != v) {
				for (String s : v) {
					results.add(s);
				}
			} else {
				final String result = mSharedPref.getString(name, "");
				if (!result.isEmpty()) {
					results.add(result);
				}
			}
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, ?> getAll() {
		Map<String, Object> m = new Hashtable<String, Object>();
		Object newV = null;
		if (null != mSharedPref) {
			for (Map.Entry<String, ?> entry : mSharedPref.getAll().entrySet()) {
				final Object value = entry.getValue();
				if (value instanceof Set<?>) {
					Set<?> temp = (Set<?>) value;
					newV = new ArrayList<String>(temp.size());
					for (Object o : temp) {
						((List<String>) newV).add(o.toString());
					}
				} else {
					newV = value.toString();
				}
				m.put(entry.getKey(), newV);
			}
		}
		return m;
	}

	@Override
	public StringListReaderWriter set(String name, Object value) {
		if (null != mSharedPref) {
			setByType(name, value);
		}
		return this;
	}

	private void setByType(String name, Object value) {
		Set<String> v = new HashSet<String>();
		if (value instanceof String[]) {
			final int length = ((String[]) value).length;
			for (int i = 0; i < length; i ++) {
				v.add(((String[]) value)[i]);
			}
		} else if (value instanceof Set<?>) {
			Set<?> temp = (Set<?>) value;
			for (Object o : temp) {
				v.add(o.toString());
			}
		} else if (value instanceof List<?>) {
			List<?> temp = (List<?>) value;
			for (Object o : temp) {
				v.add(o.toString());
			}
		} else {
			v.add(value.toString());
		}
		mSpEditor.putStringSet(name, v);
	}

	@Override
	public StringListReaderWriter setInt(String name, int value) {
		set(name, value);
		return this;
	}

	@Override
	public StringListReaderWriter setBoolean(String name, boolean value) {
		set(name, value);
		return this;
	}

	@Override
	public StringListReaderWriter setString(String name, String value) {
		set(name, value);
		return this;
	}

	@Override
	public StringListReaderWriter setAll(Map<String, ?> value) {
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
