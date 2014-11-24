package com.konka.dynamicplugin.core;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.konka.dynamicplugin.core.IPluginAsync.Type;
import com.konka.dynamicplugin.core.impl.PluginManager;
import com.konka.dynamicplugin.core.impl.ResourceController.Dependence;

public class AsyncPluginManager implements IPluginManager {
	private IPluginManager mPluginManager;
	private IPluginAsync.IListener mListener;
	private ExecutorService mThread;
	private PostToUI mPost;

	private static class InstanceHolder {
		private static AsyncPluginManager sInstance = new AsyncPluginManager();
	}

	public static AsyncPluginManager getInstance() {
		return InstanceHolder.sInstance;
	}

	private AsyncPluginManager() {
		mPluginManager = PluginManager.getInstance();
		mPluginManager.setActionListener(null);
		mThread = Executors.newFixedThreadPool(1);
		mPost = new PostToUI();
	}

	@Override
	public void setActionListener(IPluginAsync.IListener listener) {
		mListener = listener;
		mPost.setListener(mListener);
	}

	@Override
	public void setResourceDependence(Dependence dependence) {
		mPluginManager.setResourceDependence(dependence);
	}

	private static class Task {
		public Type type;
		public List<PluginInfo> changed;
		public boolean success;
		public String reason;

		private Task(Type type, List<PluginInfo> changed, boolean success,
				String reason) {
			this.type = type;
			this.changed = changed;
			this.success = success;
			this.reason = reason;
		}

		public static Task success(Type type, List<PluginInfo> changed) {
			return new Task(type, changed, true, "");
		}

		public static Task fail(Type type, String reason) {
			return new Task(type, null, false, reason);
		}

	}

	private static class PostToUI implements Runnable {
		private Handler uiHandler;
		private IPluginAsync.IListener listener;
		private Task task;

		public PostToUI() {
			uiHandler = new Handler(Looper.getMainLooper());
		}

		public void setListener(IPluginAsync.IListener listener) {
			this.listener = listener;
		}

		public void post(Task task) {
			this.task = task;
			uiHandler.post(this);
		}

		@Override
		public void run() {
			if (null != listener) {
				if (task.success) {
					listener.success(task.type, task.changed);
				} else {
					listener.fail(task.type, task.reason);
				}
			}
		}

	}

	@Override
	public void initPlugins(final Context context) {
		mThread.execute(new Runnable() {

			@Override
			public void run() {
				synchronized (mPluginManager) {
					try {
						mPluginManager.initPlugins(context);
						mPost.post(Task.success(Type.INIT, null));
					} catch (FileNotFoundException e) {
						mPost.post(Task.fail(Type.INIT, e.getMessage()));
					}
				}
			}
		});
	}

	@Override
	public List<PluginInfo> getAllRecordedPlugins() {
		return mPluginManager.getAllRecordedPlugins();
	}

	@Override
	public void installPlugin(final Context context, final PluginInfo pluginInfo) {
		mThread.execute(new Runnable() {

			@Override
			public void run() {
				synchronized (mPluginManager) {
					mPluginManager.installPlugin(context, pluginInfo);
					mPost.post(Task.success(Type.INSTALL, null));
				}
			}
		});
	}

	@Override
	public void uninstallPlugin(final Context context, final PluginInfo pluginInfo) {
		mThread.execute(new Runnable() {

			@Override
			public void run() {
				synchronized (mPluginManager) {
					mPluginManager.uninstallPlugin(context, pluginInfo);
					mPost.post(Task.success(Type.UNINSTALL, null));
				}
			}
		});
	}

	@Override
	public List<PluginInfo> getInstalledPlugins() {
		return mPluginManager.getInstalledPlugins();
	}

	@Override
	public List<PluginInfo> getEnablePlugins() {
		return mPluginManager.getEnablePlugins();
	}

	@Override
	public void enablePlugin(final PluginInfo plugin) {
		mThread.execute(new Runnable() {

			@Override
			public void run() {
				synchronized (mPluginManager) {
					mPluginManager.enablePlugin(plugin);
					ArrayList<PluginInfo> changed = new ArrayList<PluginInfo>();
					changed.add(plugin);
					mPost.post(Task.success(Type.ENABLE, changed));
				}
			}
		});
	}

	@Override
	public void disablePlugin(final PluginInfo plugin) {
		mThread.execute(new Runnable() {

			@Override
			public void run() {
				synchronized (mPluginManager) {
					mPluginManager.disablePlugin(plugin);
					ArrayList<PluginInfo> changed = new ArrayList<PluginInfo>();
					changed.add(plugin);
					mPost.post(Task.success(Type.DISABLE, changed));
				}
			}
		});
	}

	@Override
	public View getPluginView(Context context, PluginInfo pluginInfo) {
		return mPluginManager.getPluginView(context, pluginInfo);
	}

	@Override
	public AssetManager getAssets() {
		return mPluginManager.getAssets();
	}

	@Override
	public Resources getResources() {
		return mPluginManager.getResources();
	}

	@Override
	public Theme getTheme() {
		return mPluginManager.getTheme();
	}

	@Override
	public ClassLoader getClassLoader() {
		return mPluginManager.getClassLoader();
	}

}
