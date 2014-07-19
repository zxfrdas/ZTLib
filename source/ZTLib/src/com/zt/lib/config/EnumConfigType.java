package com.zt.lib.config;

/**
 * 配置文件支持的类型枚举
 * @author zhaotong
 */
public enum EnumConfigType {
	XML(".xml"),
	PROP(".properties");
	
	private String value;
	
	private EnumConfigType(String value)
	{
		this.value = value;
	}
	
	/**
	 * 获取此类型配置文件的后缀名
	 * @return 后缀名(包括".")
	 */
	public String value()
	{
		return this.value;
	}
}
