package com.zt.lib.config.ReaderWriterImpl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import android.content.Context;

import com.zt.lib.collect.SetValueProperties;
import com.zt.lib.config.EnumConfigType;
import com.zt.lib.config.ReaderWriter;

public class PropReaderWriterImpl implements ReaderWriter {

	private WeakReference<Context> mContextRef;
	private SetValueProperties mProper;
	private String mFileName;

	@Override
	public void loadFile(String name, Context context) throws IOException {
		mContextRef = new WeakReference<Context>(context);
		mFileName = name + EnumConfigType.PROP.value();
		mProper = new SetValueProperties();
		mProper.load(new InputStreamReader(mContextRef.get()
				.openFileInput(mFileName)));
	}

	@Override
	public Object get(String name) {
		Object o = null;
		if (null != mProper) {
			o = mProper.getByArray(name);
		}
		return o;
	}

	@Override
	public int getInt(String name) {
		int i = 0;
		if (null != mProper && null != mProper.getProperty(name)) {
			i = Integer.valueOf(mProper.getProperty(name));
		}
		return i;
	}

	@Override
	public boolean getBoolean(String name) {
		boolean b = false;
		if (null != mProper && null != mProper.getProperty(name)) {
			b = Boolean.valueOf(mProper.getProperty(name));
		}
		return b;
	}

	@Override
	public String getString(String name) {
		StringBuilder sb = new StringBuilder();
		if (null != mProper && null != mProper.getProperty(name)) {
			sb.append(mProper.getProperty(name));
		}
		return sb.toString();
	}

	@Override
	public String[] getStringArray(String name) {
		String[] sArray = null;
		if (null != mProper) {
			sArray = mProper.getPropertyAll(name);
		}
		return sArray;
	}

	@Override
	public Map<String, ?> getAll() {
		Map<String, Object> m = new Hashtable<String, Object>();
		Object o = null;
		if (null != mProper) {
			for (Map.Entry<String, Set<String>> entry : mProper.entrySet()) {
				String[] array = mProper.setToArray(entry.getValue());
				o = null;
				if (1 == array.length) {
					o = array[0];
				} else {
					o = array;
				}
				m.put(entry.getKey(), o);
			}
		}
		return m;
	}

	@Override
	public ReaderWriter set(String name, Object value) {
		if (null != mProper) {
			mProper.put(name, value);
		}
		return this;
	}

	@Override
	public ReaderWriter setInt(String name, int value) {
		if (null != mProper) {
			mProper.setProperty(name, String.valueOf(value));
		}
		return this;
	}

	@Override
	public ReaderWriter setBoolean(String name, boolean value) {
		if (null != mProper) {
			mProper.setProperty(name, String.valueOf(value));
		}
		return this;
	}

	@Override
	public ReaderWriter setString(String name, String value) {
		if (null != mProper) {
			mProper.setProperty(name, value);
		}
		return this;
	}

	@Override
	public ReaderWriter setStringArray(String name, String[] value) {
		if (null != mProper) {
			mProper.put(name, value);
		}
		return this;
	}

	@Override
	public ReaderWriter setAll(Map<String, ?> value) {
		if (null != mProper) {
			for (Map.Entry<String, ?> entry : value.entrySet()) {
				mProper.put(entry.getKey(), entry.getValue());
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
