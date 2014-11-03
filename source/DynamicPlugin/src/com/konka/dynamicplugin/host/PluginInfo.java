package com.konka.dynamicplugin.host;

public class PluginInfo {
	private String name;
	private String apkPath;
	private String dexPath;
	private String pluginClassName;
	
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
	public String getPluginClassName() {
		return pluginClassName;
	}
	public void setPluginClassName(String pluginClassName) {
		this.pluginClassName = pluginClassName;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("plugin name = ").append(name).append(", ");
		sb.append("apkPath = ").append(apkPath).append(", ");
		sb.append("dexPath = ").append(dexPath).append(", ");
		sb.append("pluginClassName = ").append(pluginClassName).append("\n");
		return sb.toString();
	}
	
}
