package com.zt.host;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.konka.dynamicplugin.host.PluginInfo;
import com.konka.dynamicplugin.host.app.HostDialog;

public class TestDialog extends HostDialog {
	private LinearLayout mContainer;

	public TestDialog(Context context) {
		super(context, R.style.dialog);
		setContentView(R.layout.dialog);
		getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContainer = (LinearLayout) findViewById(R.id.container);
		List<PluginInfo> plugins = getPluginManager().getPluginInfos();
		for (PluginInfo info : plugins) {
			mContainer.addView(getPluginManager().getPluginView(getHostContext(), info));
		}
	}

}
