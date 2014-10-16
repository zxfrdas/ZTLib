package com.zt.lib.database.dao;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.zt.lib.database.dao.sqlite.SQLite3DAO;
import com.zt.lib.util.Print;

/**
 * 动态代理。
 * <p>通过此代理数据库操作的每个操作方法都会打印耗时。
 * @see SQLite3DAO
 */
public class DAOProxy implements InvocationHandler {

	private Object recorder;
	
	public DAOProxy(Object recorder) {
		this.recorder = recorder;
	}
	
	public IDAO<?> bind()
	{
		return (IDAO<?>) Proxy.newProxyInstance(recorder
				.getClass().getClassLoader(), recorder.getClass()
				.getSuperclass().getInterfaces(), this);
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable
	{
		long start = System.currentTimeMillis();
		Object result = method.invoke(recorder, args);
		long end = System.currentTimeMillis();
		Print.d(method.getName() + "() cost = " + (end - start));
		return result;
	}

}
