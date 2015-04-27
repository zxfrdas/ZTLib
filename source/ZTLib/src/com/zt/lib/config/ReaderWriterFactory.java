package com.zt.lib.config;

import java.util.HashMap;
import java.util.Map;

public final class ReaderWriterFactory {
	
	private Map<String, String> classNameMap;

	private static class InstanceHolder {
		static ReaderWriterFactory INSTANCE = new ReaderWriterFactory();
	}
	
	public static ReaderWriterFactory getInstance()
	{
		return InstanceHolder.INSTANCE;
	}
	
	private ReaderWriterFactory()
	{
		classNameMap = new HashMap<String, String>();
		classNameMap.put(EnumConfigType.XML.value(), "Xml");
		classNameMap.put(EnumConfigType.PROP.value(), "Prop");
	}
	
	@SuppressWarnings("unchecked")
	public StringListReaderWriter getReaderWriterImpl(EnumConfigType eType)
	{
		StringListReaderWriter configRWer = null;
		Class<StringListReaderWriter> c = null;
		try {
			c = (Class<StringListReaderWriter>) Class.forName(
					"com.zt.lib.config.ReaderWriterImpl" + "." + classNameMap.get(eType.value()) +
					"ReaderWriterImpl");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			configRWer = c.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return configRWer;
	}
	
}
