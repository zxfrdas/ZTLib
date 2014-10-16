package com.zt.lib.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.zt.lib.config.TargetName;

public class Reflector {
	
	/**
	 * 对象浅层拷贝。用于仅包含基础类型的对象的拷贝。仅赋值同名属性。
	 * @param o 要拷贝的对象
	 * @return 拷贝之后的新对象
	 */
	public static Object copyValue(Object o)
	{
		Object copyTo = null;
		try {
			copyTo = o.getClass().newInstance();
			Field[] leftFields = copyTo.getClass().getDeclaredFields();
			Field[] rightFields = o.getClass().getDeclaredFields();
			for(int i = 0; i < leftFields.length; i ++) {
				leftFields[i].setAccessible(true);
				rightFields[i].setAccessible(true);
				leftFields[i].set(copyTo, rightFields[i].get(o));
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return copyTo;
	}
	
	/**
	 * 调用对象中指定函数，包括私有函数。
	 * @param object
	 * @param methodName 
	 * @param types
	 * @param args 
	 * @return 函数返回值
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException 
	 */
	public static Object invokeMethod(Object object, String methodName, Class<?>[] types,
			Object... args) throws IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException
	{
		Method method = object.getClass().getDeclaredMethod(methodName, types);
		method.setAccessible(true);
		try {
			return method.invoke(object, args);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		throw new NoSuchMethodException();
	}
	
	/**
	 * 调用对象中指定的静态函数，包括私有函数。
	 * @param object
	 * @param methodName
	 * @param types
	 * @param args
	 * @return 函数返回值
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static Object invokeStaticMethod(Class<?> c, String methodName, Class<?>[] types,
			Object... args) throws NoSuchMethodException, IllegalArgumentException,
			InvocationTargetException
	{
		Method method = c.getDeclaredMethod(methodName, types);
		method.setAccessible(true);
		try {
			return method.invoke(null, args);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		throw new NoSuchMethodException();
	}
	
	/**
	 * 将对象中变量（公共变量）及其值构造为字符串返回。
	 * @param object 试图打印的对象
	 * @return 字符串形如"键 = 值"
	 */
	public static String toString(Object object)
	{
		StringBuilder builder = new StringBuilder();
		Field[] fields = object.getClass().getFields();
		for (Field field : fields) {
			try {
				Object o = field.get(object);
				if (o instanceof String[]) {
					for (int index = 0; index < ((String[])o).length; index ++) {
						builder.append("\n").append(field.getName()).append("[")
								.append(index).append("]").append(" = ")
								.append(((String[]) o)[index]);
					}
				} else {
					builder.append("\n" + field.getName() + " = " + o);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return builder.toString();
	}
	
	public static Field[] getFields(Object o)
	{
		return o.getClass().getDeclaredFields();
	}
	
	/**
	 * 获取对象中所有声明的变量（包括私有变量）名。
	 * @param o
	 * @return 包含所有变量名的字符串数组
	 */
	public static String[] getFieldNames(Object o)
	{
		Field[] fields = o.getClass().getDeclaredFields();
		String[] names = new String[fields.length];
		int index = 0;
		for (Field f : fields) {
			names[index] = f.getName();
			index ++;
		}
		return names;
	}
	
	/**
	 * 获取所有传入Field的名称
	 * @param fields 要获取名称的变量数组
	 * @return 包含所有变量名的字符串数组
	 */
	public static String[] getFieldNames(Field[] fields)
	{
		String[] names = new String[fields.length];
		int index = 0;
		for (Field f : fields) {
			names[index] = f.getName();
			index ++;
		}
		return names;
	}
	
	/**
	 * 获取对象中所有声明变量（包括私有变量）的值或引用。
	 * @param o
	 * @return 包含所有值或引用的对象数组
	 * @throws IllegalArgumentException
	 */
	public static Object[] getFieldValues(Object o) throws IllegalArgumentException
	{
		Field[] fields = o.getClass().getDeclaredFields();
		Object[] values = new Object[fields.length];
		int index = 0;
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				values[index] = field.get(o);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			index ++;
		}
		return values;
	}
	
	/**
	 * 设置对象中指定变量的值，变量名不能忽略大小写。
	 * @param o 需设置的对象
	 * @param fieldName 变量名
	 * @param value 设置的值
	 * @throws NoSuchFieldException 未找到指定名称的变量
	 * @throws IllegalArgumentException 试图设置的值不正确
	 */
	public static void setFieldValue(Object o, String fieldName, Object value)
			throws IllegalArgumentException,
			NoSuchFieldException
	{
		Field field = o.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		try {
			field.set(o, formatObjectType(field.getType(), value));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 尝试输入的对象转型为指定对象。
	 * <p>目前支持String转为int/Integer,float/Float,long/Long,boolean/Boolean
	 * <p>以及String[]转为 int[]/Integer[],float[]/Float[],long[]/Long[],boolean[]/Boolean[]
	 * @param type 希望转为的型
	 * @param value 被转型对象
	 * @return 转型后的对象
	 */
	public static Object formatObjectType(Class<?> type, Object value)
	{
		Object newValue = null;
		if (value instanceof String) {
			if (int.class.equals(type) || Integer.class.equals(type)) {
				newValue = Integer.valueOf(value.toString());
			} else if (float.class.equals(type) || Float.class.equals(type)) {
				newValue = Float.valueOf(value.toString());
			} else if (long.class.equals(type) || Long.class.equals(type)) {
				newValue = Long.valueOf(value.toString());
			} else if (boolean.class.equals(type) || Boolean.class.equals(type)) {
				newValue = Boolean.valueOf(value.toString());
			}
		} else if (value instanceof String[]) {
			if (int[].class.equals(type) || Integer[].class.equals(type)) {
				int index = 0;
				newValue = new Integer[((String[])value).length];
				for (String o : (String[])value) {
					((Integer[])newValue)[index] = Integer.valueOf(o);
				}
			} else if (float[].class.equals(type) || Float[].class.equals(type)) {
				int index = 0;
				newValue = new Float[((String[])value).length];
				for (String o : (String[])value) {
					((Float[])newValue)[index] = Float.valueOf(o);
				}
			} else if (long[].class.equals(type) || Long[].class.equals(type)) {
				int index = 0;
				newValue = new Long[((String[])value).length];
				for (String o : (String[])value) {
					((Long[])newValue)[index] = Long.valueOf(o);
				}
			} else if (boolean[].class.equals(type) || Boolean[].class.equals(type)) {
				int index = 0;
				newValue = new Boolean[((String[])value).length];
				for (String o : (String[])value) {
					((Boolean[])newValue)[index] = Boolean.valueOf(o);
				}
			} else if (String[].class.equals(type)) {
				int index = 0;
				newValue = new String[((String[])value).length];
				for (String o : (String[])value) {
					((String[])newValue)[index] = o;
					index ++;
				}
			}
		} else {
			newValue = value;
		}
		return newValue;
	}

	/**
	 * 获取对象中指定变量的值，变量名不能忽略大小写
	 * @param o 视图读取变量的对象
	 * @param fieldName 变量名
	 * @return 变量的值
	 * @throws NoSuchFieldException 无此变量
	 */
	public static Object getFieldValue(Object o, String fieldName) throws NoSuchFieldException
	{
		Object object = null;
		Field field = o.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		try {
			object = field.get(o);
			field.getType();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return object;
	}
	
	/**
	 * 获取输入变量@TargetName这一Annotation的值
	 * @param field 输入变量
	 * @return 变量的@TargetName值，无则返回变量名
	 */
	public static String getFieldTargetNameValue(Field field)
	{
		String name = "";
		Annotation[] annotations = field.getDeclaredAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation instanceof TargetName) {
				name = ((TargetName)annotation).value();
			} else {
				name = field.getName();
			}
		}
		return name;
	}

	/**
	 * 获取对象中所有变量中@TargetName这一Annotation属性的值。如果无此值，则返回变量本身名称。
	 * @param o
	 * @return 对象中所有变量的@TargetName值，无则返回变量名
	 */
	public static String[] getFieldTargetNameValues(Object o)
	{
		Field[] fields = o.getClass().getDeclaredFields();
		String[] values = new String[fields.length];
		int index = 0;
		for (Field field : fields) {
			values[index] = getFieldTargetNameValue(field);
			index ++;
		}
		return values;
	}
	
}
