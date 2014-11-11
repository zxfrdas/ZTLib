package com.konka.dynamicplugin.host.app;

import android.app.Dialog;
import android.content.Context;

import com.konka.dynamicplugin.core.PluginManager;
import com.konka.dynamicplugin.host.IHost;

/**
 * 提供一个可使用插件，依赖于宿主Activity/Service的Dialog控件
 * 
 * @see HostActivity
 * @see HostService
 */
public abstract class HostDialog extends Dialog implements IHost {
	private IHost mHost;

	public HostDialog(Context context) {
		this(context, 0);
	}

	public HostDialog(Context context, int theme) {
		super(context, theme);
		if (!(context instanceof IHost)) {
			throw new IllegalArgumentException("HostDialog的宿主需实现IHost");
		}
		mHost = (IHost) context;
	}

	@Override
	public final PluginManager getPluginManager() {
		return mHost.getPluginManager();
	}

	@Override
	public final Context getHostContext() {
		return mHost.getHostContext();
	}

}
