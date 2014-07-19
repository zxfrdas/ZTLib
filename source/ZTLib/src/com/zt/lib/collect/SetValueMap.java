package com.zt.lib.collect;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;


/**
 * 通过修改Hashtable类，实现了一个key为String，value为Set<String>的Map类。
 * 如果两个value对应的key相同，则将两个value并入一个Set，然后为key赋值。
 * @author zhaotong
 */
public class SetValueMap extends Hashtable<String, Set<String>> {

	private static final long serialVersionUID = 7493345639730876291L;

	@Override
	public synchronized Set<String> put(String key, Set<String> value)
	{
		for (Map.Entry<String, Set<String>> entry : entrySet()) {
			if (entry.getKey().equals(key)) {
				for (String s : value) {
					entry.getValue().add(s);
				}
				value = entry.getValue();
			}
		}
		return super.put(key, value);
	}
	
	/**
	 * 接受类型为String,Set<String>,String[]的value参数。内部将统一转换为Set<String>后put进Map。
	 * @param key
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized <T> Set<String> put(String key, T value)
	{
		Set<String> setValue = new HashSet<String>();
		if (value instanceof String) {
			setValue.add(value.toString());
		} else if (value instanceof Set<?>) {
			for (String s : ((Set<String>) value)) {
				setValue.add(s);
			}
		} else if (value instanceof String[]) {
			for (String s : ((String[]) value)) {
				setValue.add(s);
			}
		}
		return put(key, setValue);
	}
	
	public synchronized String[] getByArray(Object key)
	{
		Set<String> set = get(key);
		String[] values = new String[set.size()];
		return set.toArray(values);
	}
	
	public synchronized int getCount(String key)
	{
		return getByArray(key).length;
	}
	
}
