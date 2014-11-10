package com.konka.dynamicplugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.zt.lib.database.Column;
import com.zt.lib.database.Database;
import com.zt.lib.database.SQLDataType;
import com.zt.lib.database.Table;

@Database(name = "plugin", version = 1)
@Table(name="plugins")
public class PluginInfo2 {
	@Column(index=1, name="Title", type=SQLDataType.TEXT)
	private String title;
	@Column(index=2, name="ApkPath", type=SQLDataType.TEXT)
	private String apkPath;
	@Column(index=3, name="DexPath", type=SQLDataType.TEXT)
	private String dexPath;
	@Column(index=4, name="EntryClass", type=SQLDataType.TEXT)
	private String entryClass;
	@Column(index=5, name="Icon", type=SQLDataType.BLOB)
	private byte[] icon;
	@Column(index=6, name="Install", type=SQLDataType.INTEGER)
	private boolean installed;
	@Column(index=7, name="Enable", type=SQLDataType.INTEGER)
	private boolean enabled;
	@Column(index=8, name="Index", type=SQLDataType.INTEGER)
	private int enableIndex;
	
	public PluginInfo2() {
		title = "";
		apkPath = "";
		dexPath = "";
		entryClass = "";
		icon = new byte[0];
		installed = false;
		enabled = false;
		enableIndex = -1;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getApkPath() {
		return apkPath;
	}

	public void setApkPath(String apkPath) {
		this.apkPath = apkPath;
	}

	public String getDexPath() {
		return dexPath;
	}

	public void setDexPath(String dexPath) {
		this.dexPath = dexPath;
	}

	public String getEntryClass() {
		return entryClass;
	}

	public void setEntryClass(String entryClass) {
		this.entryClass = entryClass;
	}
	
	public Drawable getIcon() {
		ByteArrayInputStream is = new ByteArrayInputStream(this.icon);
		return Drawable.createFromStream(is, this.title + ".png");
	}
	
	public void setIcon(Drawable icon) {
		final BitmapDrawable bDrawable = (BitmapDrawable) icon;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		bDrawable.getBitmap().compress(CompressFormat.PNG, 100, os);
		this.icon = os.toByteArray();
		try {
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isInstalled() {
		return installed;
	}

	public void setInstalled(boolean installed) {
		this.installed = installed;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getEnableIndex() {
		return enableIndex;
	}

	public void setEnableIndex(int enableIndex) {
		this.enableIndex = enableIndex;
	}
	
}
