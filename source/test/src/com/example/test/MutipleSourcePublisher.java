package com.example.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.example.test.ICache.ICacheObserver;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class MutipleSourcePublisher extends Handler implements
		IPublisher<TestItem>, ICacheObserver<String, Collection<ComparableTestItem>> {
	private static final int MSG_PUBLISH = 0x01;
	private static final int CACHE_SIZE_DEFAULT = 5;
	private static final int THREAD_NUMBER_DEFAULT = 3;
	private ICache<String, Collection<ComparableTestItem>> mCacher;
	private Map<String, Boolean> mCompleted;
	private ExecutorService mThreads;
	private AtomicInteger mSubscribeID;
	private SourceController mSourceController;
	
	private static final class RequestItem {
		int id;
		String key;
		Collection<ComparableTestItem> results;
		INotifier<TestItem> notifier;
	}
	
	public MutipleSourcePublisher() {
		this(CACHE_SIZE_DEFAULT, THREAD_NUMBER_DEFAULT);
	}
	
	public MutipleSourcePublisher(int cacheSize) {
		this(cacheSize, THREAD_NUMBER_DEFAULT);
	}
	
	public MutipleSourcePublisher(int cacheSize, int threadNumber) {
		super(Looper.getMainLooper());
		mSourceController = SourceController.getInstance();
		mCompleted = Collections.synchronizedMap(new HashMap<String, Boolean>());
		mSubscribeID = new AtomicInteger(0);
		mCacher = new FIFOCache<String, Collection<ComparableTestItem>>(cacheSize);
		mCacher.setObserver(this);
		mThreads = Executors.newFixedThreadPool(threadNumber);
	}
	
	@Override
	public void book(String key, INotifier<TestItem> notifier) {
		// 更新请求ID
		final int id = mSubscribeID.incrementAndGet();
		// 检查是否缓存过，检查是否完整
		boolean isCached = mCacher.containsKey(key);
		boolean isCompleted = mCompleted.containsKey(key) && mCompleted.get(key);
		if (isCached && isCompleted) {
			publish(id, key, notifier);
		} else {
			asyncGetResults(id, key, notifier);
		}
	}
	
	private void publish(int id, String name, INotifier<TestItem> notifier) {
		if (!checkIfThisRequestAlive(id)) {
			return;
		}
		Collection<ComparableTestItem> results = mCacher.get(name);
		Collection<TestItem> publish = new ArrayList<TestItem>();
		for (ComparableTestItem item : results) {
			publish.add(item.getOrigin());
		}
		
		notifier.onPublish(publish);
	}
	
	private boolean checkIfThisRequestAlive(int id) {
		return mSubscribeID.intValue() == id;
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
						for (TestItem item : results) {
							mSourceController.put(item.getVideoName(), i + "");
						}
						// 推送至UI Thread
						sendMessage(createMessage(id, key, wrapTestItemComparable(results), notifier));
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
		results.add(new TestItem(name + Thread.currentThread().getId()));
		return results;
	}
	
	private Collection<ComparableTestItem> wrapTestItemComparable(List<TestItem> items) {
		Collection<ComparableTestItem> results = new TreeSet<ComparableTestItem>();
		int index = 0;
		for (TestItem item : items) {
			ComparableTestItem warped = new ComparableTestItem(item, index);
			results.add(warped);
			index ++;
		}
		return results;
	}
	
	private Message createMessage(int id, String key, Collection<ComparableTestItem> results, INotifier<TestItem> notifier) {
		RequestItem item = new RequestItem();
		item.id = id;
		item.key = key;
		item.results = results;
		item.notifier = notifier;
		return obtainMessage(MSG_PUBLISH, item);
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
	public Collection<ComparableTestItem> interceptValueBeforePut(String key,
			Collection<ComparableTestItem> value) {
		if (!mCacher.containsKey(key)) {
			return value;
		}
		if (null == value) {
			return new TreeSet<ComparableTestItem>();
		}
		value.addAll(mCacher.get(key));
		return value;
	}
	

	@Override
	public void onKeyRemove(String key) {
		mSourceController.remove(key);
	}

	@Override
	public void displayDetails(TestItem publication) {
		Log.d("ZT", "let source show detail = " + mSourceController.getSuitableSource(publication.getVideoName()));
	}
	
}
