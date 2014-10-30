package com.zt.host;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zt.lib.DynamicPlugin.DLClassLoader;
import com.zt.lib.DynamicPlugin.IPlugin;
import com.zt.lib.util.Print;

public class ShowPluginActivity extends Activity {
	private String mPlugDexPath;
	private AssetManager mAssetManager;
	private Resources mResources;
	private Theme mTheme;
	private IPlugin mPlugin;
	private LinearLayout mContainer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.host);
		mContainer = (LinearLayout) findViewById(R.id.container);
		if (null != getIntent() && null != getIntent().getStringExtra("path")) {
			mPlugDexPath = getIntent().getStringExtra("path");
			Print.d("mPlugDexPath = " + mPlugDexPath);
		}
		loadResources();
		handleActivityInfo();
		launchPlugin();
		getPluginView();
	}

	private void loadResources() {
		try {
			AssetManager assetManager = AssetManager.class.newInstance();
			Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
			addAssetPath.invoke(assetManager, mPlugDexPath);
			mAssetManager = assetManager;
		} catch (Exception e) {
			e.printStackTrace();
		}
		Resources superRes = super.getResources();
		mResources = new Resources(mAssetManager, superRes.getDisplayMetrics(),
				superRes.getConfiguration());
	}

	private void handleActivityInfo() {
		PackageInfo packageInfo = getPackageManager().getPackageArchiveInfo(mPlugDexPath,
				PackageManager.GET_ACTIVITIES);
		Print.d("handleActivityInfo, theme=" + packageInfo.applicationInfo.theme);
		if (packageInfo.applicationInfo.theme > 0) {
			setTheme(packageInfo.applicationInfo.theme);
		}
		mTheme = mResources.newTheme();
		mTheme.setTo(super.getTheme());
	}

	private void launchPlugin() {
		try {
			Class<?> localClass = getClassLoader().loadClass("com.zt.plugin.PluginImpl");
			Constructor<?> localConstructor = localClass.getConstructor(new Class[] {});
			Object instance = localConstructor.newInstance(new Object[] {});
			mPlugin = (IPlugin) instance;
			Print.d("instance = " + instance);
			mPlugin.setContext(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getPluginView() {
		TextView plugin = mPlugin.getPluginView();
		Print.d("plugin in host = " + plugin.hashCode());
		LayoutParams params = plugin.getLayoutParams();
		mContainer.addView(plugin);
	}

	@Override
	public AssetManager getAssets() {
		return mAssetManager == null ? super.getAssets() : mAssetManager;
	}

	@Override
	public Resources getResources() {
		return mResources == null ? super.getResources() : mResources;
	}

	@Override
	public Theme getTheme() {
		return mTheme == null ? super.getTheme() : mTheme;
	}

	@Override
	public ClassLoader getClassLoader() {
		return DLClassLoader.getClassLoader(mPlugDexPath, getApplicationContext(),
				super.getClassLoader());
	}

}
