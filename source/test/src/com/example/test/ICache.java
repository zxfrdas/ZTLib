package com.example.test;

public interface ICache<K, V> {
	public interface ICacheObserver<K, V> {
		V prePutValue(V olds, V nows);
	}
	void setObserver(ICacheObserver<K, V> observer);
	V get(K key);
	V put(K key, V value);
	boolean containsKey(K key);
	boolean containsValue(V value);
}
