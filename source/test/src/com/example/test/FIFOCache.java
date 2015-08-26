package com.example.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FIFOCache<T> implements ICache<T> {
	private List<String> mKeys;
	private Map<String, Collection<T>> mCache;
	
	public FIFOCache(int cap) {
		mKeys = new ArrayList<String>(cap);
		mCache = new HashMap<String, Collection<T>>();
	}
	
	public boolean containsKey(String key) {
		return containsIndex(getKeyIndex(key));
	}
	
	private boolean containsIndex(int index) {
		return 0 <= index;
	}
	
	private int getKeyIndex(String key) {
		final int size = mKeys.size();
		for (int i = 0; i < size; i ++) {
			if (mKeys.get(i).equals(key)) {
				return i;
			}
		}
		return -1;
	}
	
	public void putAll(String key, Collection<T> value) {
		final int index = getKeyIndex(key);
		String removed = null;
		if (containsIndex(index)) {
			// remove current
			removed = mKeys.remove(index);
		} else {
			// remove first
			removed = mKeys.remove(0);
		}
		// add last
		mCache.remove(removed);
		mKeys.add(key);
	}

	@Override
	public Collection<T> getAll(String key) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
