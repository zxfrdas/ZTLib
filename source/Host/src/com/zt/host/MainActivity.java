package com.zt.host;

import java.io.File;

import com.zt.lib.DynamicPlugin.DLUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	private TextView mPluginInfo;
	private Button mOpenPlugin;
	private String mPlugDexPath;

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
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		if (R.id.open_plugin == id) {
			Intent intent = new Intent(this, ShowPluginActivity.class);
			intent.putExtra("path", mPlugDexPath);
			startActivity(intent);
		}
	}

	private void findPlugin() {
		File pluginFolder = getDir("plugins", Context.MODE_PRIVATE);
		File[] plugins = pluginFolder.listFiles();
		for (File plugin : plugins) {
			mPlugDexPath = plugin.getAbsolutePath();
			PackageInfo packageInfo = DLUtils.getPackageInfo(getApplicationContext(), mPlugDexPath);
			String appName = DLUtils.getAppLabel(getApplicationContext(), mPlugDexPath).toString();
			String apkName = mPlugDexPath.substring(mPlugDexPath.lastIndexOf(File.separatorChar) + 1);
			String packageName = packageInfo.applicationInfo.packageName;
			StringBuilder sb = new StringBuilder();
			sb.append("plugin appName = " + appName + ", apkName = " + apkName + ", packageName = "
					+ packageName);
			mPluginInfo.setText(sb.toString());
		}
	}
}
