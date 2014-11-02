package com.zt.host;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.konka.dynamicplugin.host.PluginInfo;
import com.konka.dynamicplugin.host.app.HostActivity;

public class MainActivity extends HostActivity implements OnClickListener {
	private TextView mPluginInfo;
	private Button mOpenPlugin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		findPlugin();
	}

	private void initView() {
		mPluginInfo = (TextView) findViewById(R.id.plugin_info);
		mOpenPlugin = (Button) findViewById(R.id.open_plugin);
		mOpenPlugin.setOnClickListener(this);
		Drawable d = getResources().getDrawable(R.drawable.ic_launcher);
		((ImageView) findViewById(R.id.image)).setImageDrawable(d);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("ZT", "onDestroy");
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		if (R.id.open_plugin == id) {
			new TestDialog(getHostContext()).show();
			// startService(new Intent(this, TestService.class));
			Drawable d = getResources().getDrawable(R.drawable.ic_launcher);
			((ImageView) findViewById(R.id.image)).setImageDrawable(d);
		}
	}

	private void findPlugin() {
		StringBuilder sb = new StringBuilder();
		for (PluginInfo plugin : getPluginManager().getPluginInfos()) {
			sb.append(plugin.toString());
		}
		mPluginInfo.setText(sb.toString());
	}
}
