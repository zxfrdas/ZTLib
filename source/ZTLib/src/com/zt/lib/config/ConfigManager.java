package com.zt.lib.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.Map;
import java.util.Observable;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.zt.lib.collect.SingletonValueMap;
import com.zt.lib.exceptions.NullArgException;
import com.zt.lib.io.StreamHelper;
import com.zt.lib.util.Print;
import com.zt.lib.util.Reflector;

/**
 * 配置文件管理类。
 * <p>
 * 需要在assets目录存放默认的配置文件(filename.properties)， 程序安装后第一次运行会将默认配置文件
 * 写入应用的私有目录下。后续配置从私有目录读取。
 * <p>
 * 从文件中读取配置到配置参数类、将配置参数类写入文件时，均会通知所有注册的观察者。
 * 
 * @author zhaotong
 */
public class ConfigManager extends Observable {

	private static volatile ConfigManager instance;
	private WeakReference<Context> mContextRef;
	private ReaderWriter mRWer;
	private String filePath;
	private String fileName;
	private EnumConfigType eType;
	private IConfigData mConfigData;
	private SingletonValueMap<String, String> mNameMap;
	private Handler mHandler;

	/**
	 * 获取ConfigManager的实例。
	 * 
	 * @param context
	 *            当前上下文
	 * @param configData
	 *            需要ConfigManager管理的配置参数类
	 * @return instance of {@code ConfigManager}
	 */
	public static ConfigManager getInstance(Context context, IConfigData configData) {
		if (null == instance) {
			synchronized (ConfigManager.class) {
				if (null == instance) {
					instance = new ConfigManager(context, configData);
				}
			}
		}
		return instance;
	}

	private ConfigManager(Context context, IConfigData configData) {
		mContextRef = new WeakReference<Context>(context);
		mConfigData = configData;
		mNameMap = new SingletonValueMap<String, String>();
		if (null != mConfigData) {
			updateNameMap(Reflector.getFieldNames(mConfigData.getClass()),
					Reflector.getFieldTargetNameValues(mConfigData.getClass()));
		}
		mHandler = new Handler(Looper.getMainLooper());
	}

	private void updateNameMap(String[] names, String[] annotationNames) {
		int index = 0;
		for (String name : names) {
			Print.d("key = " + name + " value = " + annotationNames[index]);
			mNameMap.put(name, annotationNames[index]);
			index++;
		}
	}

	/**
	 * 载入配置文件，读取配置项。
	 * <p>
	 * 如果文件不存在，会先判断assets目录下是否存在默认配置文件。
	 * <p>
	 * 若存在，则创建文件并写入默认配置，若不存在，则创建空文件。
	 * 
	 * @param name
	 *            配置文件名
	 * @param defaultName
	 *            assets目录下指定名称的默认配置文件
	 * @param type
	 *            保存配置文件类型
	 * @throws IllegalArgumentException
	 */
	public synchronized void initConfigFile(String name, String defaultName,
			EnumConfigType type) throws IllegalArgumentException {
		eType = type;
		fileName = name;
		setFilePath(name);
		mRWer = ReaderWriterFactory.getInstance().getReaderWriterImpl(eType);
		try {
			if (null == defaultName || "".equals(defaultName)) {
				defaultName = fileName;
			}
			if (!new File(filePath).exists()) {
				resetToDefault(defaultName);
			} else {
				mRWer.loadFile(fileName, mContextRef.get());
				try {
					reLoadAllValue();
				} catch (NullPointerException e) {
					new File(filePath).delete();
					resetToDefault(defaultName);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setFilePath(String name) {
		switch (eType)
		{
		case XML:
			String temp = mContextRef.get().getFilesDir().getAbsolutePath();
			int lastSeparate = temp.lastIndexOf("/");
			StringBuilder builder = new StringBuilder(
					temp.substring(0, lastSeparate));
			builder.append("/shared_prefs/").append(name).append(eType.value());
			filePath = builder.toString();
			break;

		case PROP:
			filePath = mContextRef.get().getFilesDir().getAbsolutePath() + "/"
					+ name + eType.value();
			break;
		}
	}

	/**
	 * 获取配置参数类的唯一实例，供UI根据用户选择修改配置数据。
	 * 
	 * @return
	 */
	public IConfigData getConfigData() {
		return mConfigData;
	}

	/**
	 * 获取当前配置文件所在的绝对路径
	 * 
	 * @return filePath
	 */
	public String getCurFilePath() {
		return filePath;
	}

	/**
	 * 获取指定名称的值
	 * 
	 * @param key
	 * @return value to get
	 */
	public Object getValue(String key) {
		return mRWer.get(key);
	}

	/**
	 * 获取包括所有值的数组
	 * 
	 * @return 长度可能为0
	 */
	public Object[] getValues() {
		Map<String, ?> map = mRWer.getAll();
		Object[] values = new Object[map.size()];
		int index = 0;
		for (Object o : map.values()) {
			values[index] = o;
			index++;
		}
		return values;
	}

	/**
	 * 获取包括所有键的字符串数组
	 * 
	 * @return 长度可能为0
	 */
	public String[] getKeys() {
		Map<String, ?> map = mRWer.getAll();
		String[] str = new String[map.size()];
		int index = 0;
		for (String s : map.keySet()) {
			str[index] = s;
			index++;
		}
		return str;
	}

	/**
	 * 从assets目录下读取指定名称的默认配置文件，恢复内存中数值和文件中数值。
	 * 
	 * @param name
	 *            默认配置文件名
	 */
	public synchronized void resetToDefault(String name) throws IOException {
		InputStream is = null;
		try {
			is = mContextRef.get().getAssets()
					.open(name + EnumConfigType.PROP.value());
		} catch (FileNotFoundException e) {
			is = null;
		}
		if (null != is) {
			StreamHelper.output(
					is,
					mContextRef.get().openFileOutput(
							fileName + EnumConfigType.PROP.value(),
							Context.MODE_MULTI_PROCESS));
			mRWer = ReaderWriterFactory.getInstance().getReaderWriterImpl(
					EnumConfigType.PROP);
			mRWer.loadFile(fileName, mContextRef.get());
			try {
				reLoadAllValue();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			if (EnumConfigType.XML == eType) {
				mRWer = ReaderWriterFactory.getInstance().getReaderWriterImpl(
						EnumConfigType.XML);
				mRWer.loadFile(fileName, mContextRef.get());
				commit();
				new File(mContextRef.get().getFilesDir() + "/" + fileName
						+ EnumConfigType.PROP.value()).delete();
			}
		}
	}

	/**
	 * 重新从文件中读取配置数据赋值给配置参数类，放弃了所有未提交的更改。
	 * 
	 * @throws IllegalArgumentException
	 */
	public void reLoadAllValue() throws IllegalArgumentException,
			NullPointerException {
		reLoadAllValue(mRWer);
	}

	private synchronized void reLoadAllValue(ReaderWriter rw)
			throws NullPointerException {
		if (null == mConfigData) {
			return;
		}
		Map<String, ?> map = rw.getAll();
		for (Map.Entry<String, ?> entry : map.entrySet()) {
			Print.d("key = " + entry.getKey() + ", value = " + entry.getValue());
			try {
				Reflector.setFieldValue(mConfigData,
						mNameMap.getKeyByValue(entry.getKey()), entry.getValue());
			} catch (NoSuchFieldException e) {
				continue;
			}
		}
		notifyConfigChanged();
	}

	/**
	 * 将指定输入流中的数据赋值给配置参数类，不写入文件。
	 * <p>
	 * 可用于根据规定，在一定条件下临时变更配置参数
	 * 
	 * @param is
	 *            包含配置参数键值对的文件输入流
	 * @throws NullArgException
	 *             输入流为空时抛出错误
	 */
	public synchronized void tempLoadFile(InputStream is) throws NullArgException {
		if (null == is)
			throw new NullArgException();
		String tempFile = "tempFile";
		try {
			StreamHelper.output(
					is,
					mContextRef.get().openFileOutput(
							tempFile + EnumConfigType.PROP.value(),
							Context.MODE_MULTI_PROCESS));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ReaderWriter tempRWer = ReaderWriterFactory.getInstance()
				.getReaderWriterImpl(EnumConfigType.PROP);
		try {
			tempRWer.loadFile(tempFile, mContextRef.get());
		} catch (IOException e) {
			e.printStackTrace();
		}
		reLoadAllValue(tempRWer);
	}

	/**
	 * 提交更改，将所有数据写入文件
	 * 
	 * @throws IOException
	 */
	public void commit() throws IOException {
		setAllValue();
		notifyConfigChanged();
	}

	/**
	 * 将配置参数类中的值写入文件。
	 * 
	 * @throws IOException
	 */
	private synchronized ConfigManager setAllValue() throws IOException {
		if (null == mConfigData) {
			return this;
		}
		String[] names = Reflector.getFieldNames(mConfigData.getClass());
		Object[] values = Reflector.getFieldValues(mConfigData);
		Map<String, Object> map = new Hashtable<String, Object>();
		for (int i = 0; i < names.length; i++) {
			map.put(mNameMap.get(names[i]), values[i]);
		}
		mRWer.setAll(map).commit();
		return this;
	}

	private synchronized void notifyConfigChanged() {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				instance.setChanged();
				instance.notifyObservers(mConfigData);
			}
		});
	}

}