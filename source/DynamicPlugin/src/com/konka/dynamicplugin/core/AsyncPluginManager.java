package com.konka.dynamicplugin.core;

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

public class AsyncPluginManager implements IPluginManager {
	private static final String TAG = PluginManager.class.getSimpleName();
	private PluginManager mPluginManager;
	private IActionListener mListener;
	private ExecutorService mThread;
	private Handler mUIHandler;

	private static class InstanceHolder {
		private static AsyncPluginManager sInstance = new AsyncPluginManager();
	}

	public static AsyncPluginManager getInstance() {
		return InstanceHolder.sInstance;
	}

	private AsyncPluginManager() {
		mPluginManager = PluginManager.getInstance();
		mThread = Executors.newFixedThreadPool(1);
		mUIHandler = new Handler(Looper.getMainLooper());
	}

	@Override
	public void setActionListener(IActionListener listener) {
		mListener = listener;
	}

	@Override
	public void setResourceController(ResourceController controller) {
		mPluginManager.setResourceController(controller);
	}

	@Override
	public void initPlugins(final Context context) {
		mThread.execute(new Runnable() {

			@Override
			public void run() {
				synchronized (mPluginManager) {
					mPluginManager.initPlugins(context);
					mUIHandler.post(new Runnable() {
						
						@Override
						public void run() {
							mListener.success(PluginAsyncEvent.INIT);
						}
					});
				}
			}
		});
	}

	@Override
	public List<PluginInfo> getAllRecordedPlugins() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void installPlugin(Context context, PluginInfo pluginInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void uninstallPlugin(Context context, PluginInfo pluginInfo) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<PluginInfo> getInstalledPlugins() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PluginInfo> getEnablePlugins() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void enablePlugin(PluginInfo plugin) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disablePlugin(PluginInfo plugin) {
		// TODO Auto-generated method stub

	}

	@Override
	public View getPluginView(Context context, PluginInfo pluginInfo) {
		// TODO Auto-generated method stub
		return null;
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
