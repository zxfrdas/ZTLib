package com.konka.dynamicplugin.core;

import java.io.FileNotFoundException;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.view.View;

public interface IPluginManager {

	/**
	 * 设置操作监听器。如果接口实现为异步操作，则需对此监听器做处理。
	 * 
	 * @param listener
	 */
	void setActionListener(IPluginAsync.IListener listener);

	/**
	 * 初始化资源控制器，以便运行中进行动态切换资源
	 * 
	 * @param controller
	 *            宿主/插件资源控制器
	 */
	void setResourceController(ResourceController controller);

	/**
	 * 构建宿主应用的插件数据库。
	 * <p>
	 * 如果无数据，则在指定路径下查找插件APK文件并解析插入数据库。
	 * <p>
	 * 路径1:{@code /data/misc/konka/plugins/plugin/}
	 * <p>
	 * 路径2:{@code /data/data/packageName/app_plugins/}
	 * 
	 * @param context
	 *            {@code getApplicationContext()}即可
	 * @throws FileNotFoundException 
	 */
	void initPlugins(Context context) throws FileNotFoundException;

	/**
	 * 获取目前被记录的所有插件，不论是否安装、是否启用。
	 * 
	 * @return 所有已被记录的插件APK信息。无则返回空列表。
	 */
	List<PluginInfo> getAllRecordedPlugins();

	/**
	 * 安装指定插件
	 * 
	 * @param context
	 *            {@code getApplicationContext()}即可
	 * @param pluginInfo
	 *            指定插件的插件信息
	 */
	void installPlugin(Context context, PluginInfo pluginInfo);

	/**
	 * 卸载指定插件
	 * 
	 * @param context
	 *            {@code getApplicationContext()}即可
	 * @param pluginInfo
	 *            指定插件的插件信息
	 */
	void uninstallPlugin(Context context, PluginInfo pluginInfo);

	/**
	 * 获取所有已经被宿主安装的插件
	 * 
	 * @return 所有已安装的插件。无则返回空列表。
	 */
	List<PluginInfo> getInstalledPlugins();

	/**
	 * 获取所有已经启用，可供宿主获取视图显示的插件
	 * <p>
	 * 插件根据用户启用的顺序在返回列表中正序排列。
	 * 
	 * @return 所有已启用的插件。无则返回空列表。
	 */
	List<PluginInfo> getEnablePlugins();

	/**
	 * 启用指定插件
	 * 
	 * @param plugin
	 *            指定插件的信息
	 */
	void enablePlugin(PluginInfo plugin);

	/**
	 * 禁用指定插件
	 * 
	 * @param plugin
	 *            指定插件的信息
	 */
	void disablePlugin(PluginInfo plugin);

	/**
	 * 获取指定插件提供的视图
	 * 
	 * @param context
	 *            {@code getHostContext()}
	 * @param pluginInfo
	 *            指定插件的信息
	 * @return {@code View} 指定插件提供的视图。如果插件APK未根据要求实现，则返回{@code null}
	 */
	View getPluginView(Context context, PluginInfo pluginInfo);

	/**
	 * 获取当前{@code AssetManager}
	 * 
	 * @return 未找到则返回{@code null}
	 */
	AssetManager getAssets();

	/**
	 * 获取当前{@code Resources}
	 * 
	 * @return 未找到则返回{@code null}
	 */
	Resources getResources();

	/**
	 * 获取当前{@code Theme}
	 * 
	 * @return 未找到则返回{@code null}
	 */
	Theme getTheme();

	/**
	 * 获取当前{@code ClassLoader}
	 * 
	 * @return 未找到则返回{@code null}
	 */
	ClassLoader getClassLoader();
}
