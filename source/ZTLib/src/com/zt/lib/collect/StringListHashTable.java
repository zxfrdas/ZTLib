package com.zt.lib.collect;

import java.util.Hashtable;
import java.util.List;

public class StringListHashTable extends Hashtable<String, List<String>> {

	private static final long serialVersionUID = -7945834751742859034L;

	@Override
	public synchronized List<String> put(String key, List<String> value) {
		List<String> orgV = get(key);
		if (null == orgV) {
			orgV = value;
		} else {
			for (String newV : value) {
				if (!orgV.contains(newV)) {
					orgV.add(newV);
				}
			}
		}
		return super.put(key, orgV);
	}

}
