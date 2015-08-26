package com.example.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class MutipleSourcePublisher extends Handler implements IPublisher<TestItem> {
	private static final int MSG_PUBLISH = 0x01;
	private ICache<TestItem> mCacher;
	private ConcurrentMap<String, Boolean> mCompleted;
	private ExecutorService mThreads;
	private AtomicInteger mSubscribeID;
	
	private static final class RequestItem {
		int id;
		String key;
		Collection<TestItem> results;
		INotifier<TestItem> notifier;
	}
	
	public MutipleSourcePublisher() {
		super(Looper.getMainLooper());
		mCacher = new FIFOCache<TestItem>(3);
		mCompleted = new ConcurrentHashMap<String, Boolean>();
		mThreads = Executors.newSingleThreadScheduledExecutor();
		mSubscribeID = new AtomicInteger(0);
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
		Collection<TestItem> results = mCacher.getAll(name);
		// 合并去重
		
		notifier.onItemPublish(results);
	}
	
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_PUBLISH:
			RequestItem item = (RequestItem) msg.obj;
			// 加入缓存
			mCacher.putAll(item.key, item.results);
			// 发布
			publish(item.id, item.key, item.notifier);
			break;

		default:
			break;
		}
	}
	
	private void asyncGetResults(final int id, final String key, final INotifier<TestItem> notifier) {
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
						// 推送至UI Thread
						sendMessage(createMessage(id, key, results, notifier));
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
	
	private Message createMessage(int id, String key, Collection<TestItem> results, INotifier<TestItem> notifier) {
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
		results.add(new TestItem(name + "0", source + ""));
		results.add(new TestItem(name + "1", source + ""));
		results.add(new TestItem(name + "2", source + ""));
		return results;
	}
	
}
