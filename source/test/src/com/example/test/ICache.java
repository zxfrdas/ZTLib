package com.example.test;

import java.util.Collection;

public interface ICache<T> {
	Collection<T> getAll(String key);
	void putAll(String key, Collection<T> value);
	boolean containsKey(String key);
}
