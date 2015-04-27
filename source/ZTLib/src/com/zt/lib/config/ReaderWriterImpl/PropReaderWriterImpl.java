package com.zt.lib.config.ReaderWriterImpl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;

import com.zt.lib.collect.StringListProperties;
import com.zt.lib.config.EnumConfigType;
import com.zt.lib.config.StringListReaderWriter;

public class PropReaderWriterImpl implements StringListReaderWriter {

	private WeakReference<Context> mContextRef;
	private StringListProperties mProper;
	private String mFileName;

	@Override
	public void loadFile(String name, Context context) throws IOException {
		mContextRef = new WeakReference<Context>(context);
		mFileName = name + EnumConfigType.PROP.value();
		mProper = new StringListProperties();
		mProper.load(new InputStreamReader(mContextRef.get()
				.openFileInput(mFileName)));
	}
	
	@Override
	public List<Integer> getInt(String name) {
		List<Integer> results = new ArrayList<Integer>();
		if (null != mProper && null != mProper.getProperty(name)) {
			List<String> org = mProper.getProperty(name);
			for (String s : org) {
				results.add(Integer.valueOf(s));
			}
		}
		return results;
	}

	@Override
	public List<Boolean> getBoolean(String name) {
		List<Boolean> results = new ArrayList<Boolean>();
		if (null != mProper && null != mProper.getProperty(name)) {
			List<String> org = mProper.getProperty(name);
			for (String s : org) {
				results.add(Boolean.valueOf(s));
			}
		}
		return results;
	}

	@Override
	public List<String> getString(String name) {
		List<String> results = new ArrayList<String>();
		if (null != mProper && null != mProper.getProperty(name)) {
			List<String> org = mProper.getProperty(name);
			for (String s : org) {
				results.add(s);
			}
		}
		return results;
	}

	@Override
	public Map<String, ?> getAll() {
		Map<String, Object> m = new Hashtable<String, Object>();
		Object o = null;
		if (null != mProper) {
			for (Map.Entry<String, List<String>> entry : mProper.entrySet()) {
				List<String> value = entry.getValue();
				o = null;
				if (1 == value.size()) {
					o = value.get(0);
				} else {
					o = value;
				}
				m.put(entry.getKey(), o);
			}
		}
		return m;
	}

	@Override
	public StringListReaderWriter set(String name, Object value) {
		if (null != mProper) {
			mProper.put(name, new ArrayList<String>(Arrays.asList(value.toString())));
		}
		return this;
	}

	@Override
	public StringListReaderWriter setInt(String name, int value) {
		if (null != mProper) {
			mProper.setProperty(name, String.valueOf(value));
		}
		return this;
	}

	@Override
	public StringListReaderWriter setBoolean(String name, boolean value) {
		if (null != mProper) {
			mProper.setProperty(name, String.valueOf(value));
		}
		return this;
	}

	@Override
	public StringListReaderWriter setString(String name, String value) {
		if (null != mProper) {
			mProper.setProperty(name, value);
		}
		return this;
	}

	@Override
	public StringListReaderWriter setAll(Map<String, ?> value) {
		if (null != mProper) {
			for (Map.Entry<String, ?> entry : value.entrySet()) {
				final String k = entry.getKey();
				final Object v = entry.getValue();
				List<String> newV = new ArrayList<String>();
				if (v instanceof String) {
					newV.add((String) v);
				} else if (v instanceof String[]) {
					String[] temp = (String[]) v;
					newV = new ArrayList<String>(Arrays.asList(temp));
				} else if (v instanceof Set<?>) {
					Set<?> temp = (Set<?>) v;
					for (Object o : temp) {
						newV.add(o.toString());
					}
				} else if (v instanceof List<?>) {
					List<?> temp = (List<?>) v;
					for (Object o : temp) {
						newV.add(o.toString());
					}
				}
				mProper.put(k, newV);
			}
		}
		return this;
	}

	@Override
	public void commit() throws IOException {
		if (null != mProper) {
			OutputStreamWriter osw = new OutputStreamWriter(mContextRef.get()
					.openFileOutput(mFileName, Context.MODE_PRIVATE));
			mProper.store(osw, "");
			osw.close();
		}
	}

}
