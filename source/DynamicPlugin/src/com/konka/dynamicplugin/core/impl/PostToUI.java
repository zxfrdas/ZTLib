package com.konka.dynamicplugin.core.impl;

import android.os.Handler;
import android.os.Looper;

import com.konka.dynamicplugin.core.IAsyncListener;

public class PostToUI implements Runnable {
	private Handler uiHandler;
	private IAsyncListener listener;
	private Task task;

	public static class Task {
		public boolean success;
		public String reason;

		private Task(boolean success, String reason) {
			this.success = success;
			this.reason = reason;
		}

		public static Task success() {
			return new Task(true, "");
		}

		public static Task fail(String reason) {
			return new Task(false, reason);
		}

	}

	public PostToUI() {
		uiHandler = new Handler(Looper.getMainLooper());
	}

	public void post(IAsyncListener listener, Task task) {
		this.listener = listener;
		this.task = task;
		uiHandler.post(this);
	}

	@Override
	public void run() {
		if (null != listener) {
			if (task.success) {
				listener.success();
			} else {
				listener.fail(task.reason);
			}
		}
	}
}
