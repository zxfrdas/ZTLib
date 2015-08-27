package com.example.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.example.test.ICache.ICacheObserver;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class MutipleSourcePublisher extends Handler implements
		IPublisher<TestItem>, ICacheObserver<String, Map<TestItem, Collection<String>>> {
	private static final int MSG_PUBLISH = 0x01;
	private ICache<String, Map<TestItem, Collection<String>>> mCacher;
	private ConcurrentMap<String, Boolean> mCompleted;
	private ExecutorService mThreads;
	private AtomicInteger mSubscribeID;
	private SourceController mSourceController;
	
	private static final class RequestItem {
		int id;
		String key;
		Map<TestItem, Collection<String>> results;
		INotifier<TestItem> notifier;
	}
	
	public MutipleSourcePublisher() {
		super(Looper.getMainLooper());
		mSourceController = SourceController.getInstance();
		mCacher = new FIFOCache<String, Map<TestItem, Collection<String>>>(3);
		mCacher.setObserver(this);
		mCompleted = new ConcurrentHashMap<String, Boolean>();
		mThreads = Executors.newSingleThreadScheduledExecutor();
		mSubscribeID = new AtomicInteger(0);
	}
	
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_PUBLISH:
			RequestItem item = (RequestItem) msg.obj;
			// 加入缓存
			mCacher.put(item.key, item.results);
			// 发布
			publish(item.id, item.key, item.notifier);
			break;

		default:
			break;
		}
	}
	
	@Override
	public Map<TestItem, Collection<String>> prePutValue(Map<TestItem, Collection<String>> olds,
			Map<TestItem, Collection<String>> nows) {
		if (null == olds || olds.isEmpty()) {
			return nows;
		}
		if (null == nows || nows.isEmpty()) {
			return olds;
		}
		return mergeSameName(olds, nows);
	}
	
	private Map<TestItem, Collection<String>> mergeSameName(
			Map<TestItem, Collection<String>> olds,
			Map<TestItem, Collection<String>> nows) {
		Map<TestItem, Collection<String>> results = new HashMap<TestItem, Collection<String>>();
		Map<TestItem, Boolean> flags = new HashMap<TestItem, Boolean>();
		for (Entry<TestItem, Collection<String>> old : olds.entrySet()) {
			results.put(old.getKey(), old.getValue());
			for (Entry<TestItem, Collection<String>> now : nows.entrySet()) {
				if (flags.get(now.getKey())) {
					continue;
				}
				if (old.getKey().getVideoName().equals(now.getKey().getVideoName())) {
					now.getValue().addAll(old.getValue());
					flags.put(now.getKey(), true);
				}
				results.put(now.getKey(), now.getValue());
			}
		}
		return results;
	}
	
	@Override
	public void request(String key, INotifier<TestItem> notifier) {
		// 更新请求ID
		final int id = mSubscribeID.getAndIncrement();
		// 检查是否缓存过，检查是否完整
		boolean isCached = mCacher.containsKey(key);
		boolean isCompleted = mCompleted.get(key);
		if (isCached && isCompleted) {
			// 发布
			publish(id, key, notifier);
		} else {
			// 不完整或未缓存过，进行请求，获取结果
			asyncGetResults(id, key, notifier);
		}
	}
	
	private void publish(int id, String name, INotifier<TestItem> notifier) {
		if (!checkIfThisRequestAlive(id)) {
			return;
		}
		Map<TestItem, Collection<String>> results = mCacher.get(name);
		mSourceController.setCurrents(results);
		notifier.onItemPublish(results.keySet());
	}
	
	private void asyncGetResults(final int id, final String key,
			final INotifier<TestItem> notifier) {
		if (!checkIfThisRequestAlive(id)) {
			return;
		}
		// 模拟网络请求多个供应商来源
		mThreads.submit(new Runnable() {
			
			@Override
			public void run() {
				boolean isCompleted = true;
				for (int i = 0; i < 3; i ++) {
					// 判断请求是否过期，即有新请求
					if (checkIfThisRequestAlive(id)) {
						// 获取请求结果数据
						List<TestItem> results = mockCreateTestItem(key, i);
						Map<TestItem, Collection<String>> finalResults = convertTestItems(results, i + "");
						// 推送至UI Thread
						sendMessage(createMessage(id, key, finalResults, notifier));
					} else {
						// 有新请求则丢弃本次请求
						isCompleted = false;
						break;
					}
				}
				mCompleted.put(key, isCompleted);
			}
		});
	}
	
	private Message createMessage(int id, String key, Map<TestItem, Collection<String>> results, INotifier<TestItem> notifier) {
		RequestItem item = new RequestItem();
		item.id = id;
		item.key = key;
		item.results = results;
		item.notifier = notifier;
		return obtainMessage(MSG_PUBLISH, results);
	}
	
	private boolean checkIfThisRequestAlive(int id) {
		return mSubscribeID.intValue() != id;
	}
	
	private List<TestItem> mockCreateTestItem(String name, int source) {
		try {
			TimeUnit.MILLISECONDS.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<TestItem> results = new ArrayList<TestItem>();
		results.add(new TestItem(name + "0"));
		results.add(new TestItem(name + "1"));
		results.add(new TestItem(name + "2"));
		return results;
	}
	
	private Map<TestItem, Collection<String>> convertTestItems(List<TestItem> items, String source) {
		Map<TestItem, Collection<String>> results = new HashMap<TestItem, Collection<String>>();
		Collection<String> sources = new HashSet<String>(1);
		sources.add(source);
		for (TestItem item : items) {
			results.put(item, sources);
		}
		return results;
	}
	
}
