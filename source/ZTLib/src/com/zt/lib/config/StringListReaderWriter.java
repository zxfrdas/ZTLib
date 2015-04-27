package com.zt.lib.config;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import android.content.Context;

/**
 * 读写配置文件的方法接口。对配置文件进行读写操作之前请确保成功loadFile，完成操作用请确保使用commit提交更改。
 * @author zhaotong
 */
public interface StringListReaderWriter {
	
	/**
	 * 读取指定名称、指定类型的配置文件。目前支持SharedPreference和properties两种配置文件格式。
	 * @param name 配置文件名，不包括后缀名
	 * @param context 上下文
	 * @throws IOException 读取出错
	 */
	public void loadFile(String name, Context context) throws IOException;
	
	/**
	 * 获取指定名称的属性的INT值
	 * @param name key
	 * @return 无此名称则返回默认值0
	 */
	public List<Integer> getInt(String name);
	
	/**
	 * 获取指定名称的属性的boolean值
	 * @param name key
	 * @return 无此名称则返回默认值false
	 */
	public List<Boolean> getBoolean(String name);
	
	/**
	 * 获取指定名称的属性的String值
	 * @param name key
	 * @return 无此名称则返回默认值""
	 */
	public List<String> getString(String name);
	
	/**
	 * 获取配置文件中所有键值对
	 * @return Returns a map containing a list of pairs key/value representing the preferences.
	 */
	public Map<String, ?> getAll();
	
	/**
	 * 设置指定名称的属性为指定值。如果不存在此名称则创建，如果有同名则覆盖。
	 * @param name the key to add
	 * @param value the value to add
	 * @return {@link ReaderWriter}
	 */
	public StringListReaderWriter set(String name, Object value);
	
	/**
	 * 设置指定名称的属性为指定INT值。如果不存在此名称则创建，如果有同名则覆盖。
	 * @param name the key to add
	 * @param value the value to add
	 * @return {@link ReaderWriter}
	 */
	public StringListReaderWriter setInt(String name, int value);
	
	/**
	 * 设置指定名称的属性为指定boolean值。如果不存在此名称则创建，如果有同名则覆盖。
	 * @param name the key to add
	 * @param value the value to add
	 * @return {@link ReaderWriter}
	 */
	public StringListReaderWriter setBoolean(String name, boolean value);
	
	/**
	 * 设置指定名称的属性为指定String值。如果不存在此名称则创建，如果有同名则覆盖。
	 * @param name the key to add
	 * @param value the value to add
	 * @return {@link ReaderWriter}
	 */
	public StringListReaderWriter setString(String name, String value);
	
	/**
	 * 加入指定Map中所有键值对
	 * @param value the map to add
	 * @return {@link ReaderWriter}
	 */
	public StringListReaderWriter setAll(Map<String, ?> value);
	
	/**
	 * 提交更改，保存至文件。
	 * @throws IOException
	 */
	public void commit() throws IOException ;
	
}
