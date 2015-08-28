package com.example.test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class FIFOCache<K, V> implements ICache<K, V> {
	private LinkedList<K> mKeys;
	private Map<K, V> mCache;
	private ICacheObserver<K, V> mObserver;
	private int capacity;
	
	public FIFOCache(int cap) {
		if (0 >= cap) throw new IllegalArgumentException("capacity need more then zero");
		mKeys = new LinkedList<K>();
		mCache = new HashMap<K, V>();
	}
	
	@Override
	public void setObserver(ICacheObserver<K, V> observer) {
		mObserver = observer;
	}
	
	@Override
	public V get(K key) {
		return mCache.get(key);
	}

	@Override
	public V put(K key, V value) {
		mKeys.addLast(key);
		
		K removed = removeOtherKeyIfNeed(key);
		if (null != removed) {
			mCache.remove(removed);
			if (null != mObserver) {
				mObserver.onKeyRemove(removed);
			}
		}
		
		return mCache.put(key, processValueBeforePut(key, value));
	}
	
	private K removeOtherKeyIfNeed(K key) {
		final int index = mKeys.indexOf(key);
		K removed = null;
		try {
			// cache中已经存在当前加入key，移除最先保存的
			mKeys.remove(index);
		} catch (IndexOutOfBoundsException e) {
			// cache中不存在当前加入的key
			if (capacity <= mKeys.size()) {
				// 如果已经超出容量，cache需要把最早缓存的清除。
				removed = mKeys.pollFirst();
			}
		}
		return removed;
	}
	
	private V processValueBeforePut(K key, V value) {
		V afterProcess = value;
		if (null != mObserver) {
			afterProcess = mObserver.interceptValueBeforePut(key, value);
		}
		return afterProcess;
	}

	@Override
	public boolean containsKey(K key) {
		return mCache.containsKey(key);
	}

	@Override
	public boolean containsValue(V value) {
		return mCache.containsValue(value);
	}
	
}
