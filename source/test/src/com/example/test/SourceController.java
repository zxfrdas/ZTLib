package com.example.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SourceController {
	private List<String> mOrderSources;
	private Map<String, Collection<String>> mCurrents;

	private static final class InstanceHolder {
		private static SourceController sInstance = new SourceController();
	}
	
	public static SourceController getInstance() {
		return InstanceHolder.sInstance;
	}
	
	private SourceController() {
		mOrderSources = new ArrayList<String>();
		mOrderSources.add(2 + "");
		mOrderSources.add(0 + "");
		mOrderSources.add(1 + "");
	}
	
	public void setCurrents(Map<TestItem, Collection<String>> currents) {
		if (null == mCurrents) {
			mCurrents = new HashMap<String, Collection<String>>();
		}
		mCurrents.clear();
		for (Entry<TestItem, Collection<String>> entry : currents.entrySet()) {
			mCurrents.put(entry.getKey().getVideoName(), entry.getValue());
		}
	}
	
	public String getSuitableSource(String name) {
		Collection<String> sources = mCurrents.get(name);
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
