package com.example.test;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FIFOCache<T> implements ICache<T> {
	
	private Map<String, List<T>> mCache;
	
	public FIFOCache(int cap) {
		mCache = new HashMap<String, List<T>>();
	}
	
	public boolean containsKey(String key) {
		return mCache.containsKey(key);
	}
	
	public void putAll(String key, Collection<T> value) {
		
	}

	@Override
	public Collection<T> getAll(String key) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
