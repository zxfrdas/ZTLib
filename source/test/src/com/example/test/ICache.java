package com.example.test;

/**
 * Simple cache interface that only provide get & put to modify the cache.
 * <p> Instances should make their own rules to control cache size.
 * <p> And you will be notified by specified {@code ICacheObserver} when cache been modified if you already set.
 * @param <K> Key
 * @param <V> Value
 */
public interface ICache<K, V> {
	/**
	 * Observer of the cache. Will be notified when cache been modified.
	 * @param <K> Key
	 * @param <V> Value
	 */
	public interface ICacheObserver<K, V> {
		/**
		 * Give you a chance to process the specified K-V.
		 * @param key
		 * @param value
		 * @return modified value
		 */
		V interceptValueBeforePut(K key, V value);
		/**
		 * Notify when the key has been removed.
		 * @param key
		 */
		void onKeyRemove(K key);
	}
	/**
	 * Set the specified {@code ICacheObserver}.
	 * @param observer {@code ICacheObserver}
	 */
	void setObserver(ICacheObserver<K, V> observer);
	/**
	 * Get the specified value of the specified key.
	 * @param key
	 * @return value
	 */
	V get(K key);
	/**
	 * Store the specified key to the specified value.
	 * @param key
	 * @param value
	 * @return previous value of the specified key or null if there was no previous value.
	 */
	V put(K key, V value);
	/**
	 * Returns whether this cache contains the specified key.
	 * @param key The key to search for
	 * @return true if this cache contains the specified key, false otherwise
	 */
	boolean containsKey(K key);
	/**
	 * Returns whether this cache contains the specified value.
	 * @param value The value to search for
	 * @return true if this cache contains the specified value, false otherwise
	 */
	boolean containsValue(V value);
}
