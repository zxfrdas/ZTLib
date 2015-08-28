package com.example.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SourceController {
	private List<String> mOrderSources;
	private SourceMap mSpecificVideoSources;
	
	private static final class SourceMap extends HashMap<String, Collection<String>> {
		
		private static final long serialVersionUID = -962874052035416446L;

		public Collection<String> put(String key, String value) {
			Collection<String> realValue = new HashSet<String>();
			realValue.add(value);
			return put(key, realValue);
		}

		@Override
		public Collection<String> put(String key, Collection<String> value) {
			if (containsKey(key)) {
				value.addAll(get(key));
			}
			return super.put(key, value);
		}
		
	}

	private static final class InstanceHolder {
		private static SourceController sInstance = new SourceController();
	}
	
	public static SourceController getInstance() {
		return InstanceHolder.sInstance;
	}
	
	private SourceController() {
		mSpecificVideoSources = new SourceMap();
		mOrderSources = new ArrayList<String>();
		mOrderSources.add(2 + "");
		mOrderSources.add(0 + "");
		mOrderSources.add(1 + "");
	}
	
	public void put(String name, String value) {
		mSpecificVideoSources.put(name, value);
	}
	
	public void remove(String name) {
		mSpecificVideoSources.remove(name);
	}
	
	public String getSuitableSource(String name) {
		Collection<String> sources = mSpecificVideoSources.get(name);
		for (String s : mOrderSources) {
			for (String source : sources) {
				if (s.equals(source)) {
					return s;
				}
			}
		}
		return null;
	}
	
}
