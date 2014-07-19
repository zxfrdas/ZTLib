package com.zt.lib.collect;

import java.util.Hashtable;
import java.util.Map;

/**
 * 继承于Hashtable，实现了一个value无法重复的map，增加了一个根据value获取key的方法。
 * 如果输入的value已经存在于键值对中，并且键与已经存在的不同，那么会用此值覆盖掉已经存在的键值对中的值。
 * 
 * @author zhaotong
 * 
 * @param <K>
 *            键
 * @param <V>
 *            值
 */
public class SingletonValueMap<K, V> extends Hashtable<K, V> {

	private static final long serialVersionUID = -6033585881328750826L;

	@Override
	public synchronized V put(K key, V value)
	{
		for (Map.Entry<K, V> entry : entrySet()) {
			if (entry.getValue().equals(value) && !entry.getKey().equals(key)) {
				key = entry.getKey();
				break;
			}
		}
		return super.put(key, value);
	}

	/**
	 * 根据值获取键。因为此Map键值是唯一对应关系故可以实现此功能。
	 * 
	 * @param value
	 * @return the key linked to the value
	 */
	public synchronized K getKeyByValue(Object value)
	{
		for (Map.Entry<K, V> entry : entrySet()) {
			if (entry.getValue().equals(value)) {
				return entry.getKey();
			}
		}
		return null;
	}

}
