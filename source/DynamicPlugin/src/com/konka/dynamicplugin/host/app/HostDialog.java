package com.konka.dynamicplugin.host.app;

import com.konka.dynamicplugin.host.IHost;
import com.konka.dynamicplugin.host.PluginManager;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

/**
 * 提供一个可使用插件，依赖于宿主Activity/Service的Dialog控件
 * 
 * @see HostActivity
 * @see HostService
 */
public abstract class HostDialog extends Dialog implements IHost {
	private PluginManager mPluginManager;
	private Context mHostContext;

	public HostDialog(Context context) {
		this(context, 0);
	}

	public HostDialog(Context context, int theme) {
		super(context, theme);
		mHostContext = context;
		if (!(mHostContext instanceof HostActivity) && !(mHostContext instanceof HostService)) {
			throw new IllegalArgumentException("HostDialog的宿主需继承自HostActivity或HostService");
		}
		mPluginManager = PluginManager.getInstance();
	}

	@Override
	public final void setContentView(int layoutResID) {
		mPluginManager.setUsePluginResourcesEnable(false);
		super.setContentView(layoutResID);
		mPluginManager.setUsePluginResourcesEnable(true);
	}

	@Override
	public final View findViewById(int id) {
		mPluginManager.setUsePluginResourcesEnable(false);
		View v = super.findViewById(id);
		mPluginManager.setUsePluginResourcesEnable(true);
		return v;
	}

	@Override
	public final PluginManager getPluginManager() {
		return mPluginManager;
	}

	@Override
	public final Context getHostContext() {
		return mHostContext;
	}

}
