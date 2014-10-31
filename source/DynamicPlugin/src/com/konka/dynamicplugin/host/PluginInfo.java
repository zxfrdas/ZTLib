package com.konka.dynamicplugin.host;

public class PluginInfo {
	private String name;
	private String apkPath;
	private String dexPath;
	private String packageName;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("plugin name = ").append(name).append(", ");
		sb.append("apkPath = ").append(apkPath).append(", ");
		sb.append("dexPath = ").append(dexPath).append(", ");
		sb.append("packageName = ").append(packageName);
		return sb.toString();
	}
	
}
