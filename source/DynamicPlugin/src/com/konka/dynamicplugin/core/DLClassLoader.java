/*
 * Copyright (C) 2014 singwhatiwanna(任玉刚) <singwhatiwanna@qq.com>
 * 
 * collaborator:田啸,宋思宇
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.konka.dynamicplugin.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import android.util.Log;
import dalvik.system.DexClassLoader;

public class DLClassLoader extends DexClassLoader {
	private static final String SYSTEM_DEX_PATH = "/data/misc/konka/plugins/dex";
	private static final long OUT_TIME = 10 * 1000;// 10s
	private static Map<ClassLoader, String> mDexPaths = new WeakHashMap<ClassLoader, String>();
	private static Map<String, Long> mModifiedTimes = new HashMap<String, Long>();

	protected DLClassLoader(String dexPath, String optimizedDirectory, String libraryPath,
			ClassLoader parent) {
		super(dexPath, optimizedDirectory, libraryPath, parent);
	}

	@Override
	public Class<?> loadClass(String className) throws ClassNotFoundException {
		boolean isNeedReload = false;
		final String currentDexPath = mDexPaths.get(this);
		final long currentModified = new File(currentDexPath).lastModified();
		final Long lastModified = mModifiedTimes.get(currentDexPath);
		if (null == lastModified) {
			mModifiedTimes.put(currentDexPath, currentModified);
		} else if (currentModified - lastModified.longValue() > OUT_TIME) {
			// compare last modify time with new dex file modify time
			isNeedReload = true;
			// update last modified time
			mModifiedTimes.put(currentDexPath, Long.valueOf(currentModified));
		}
		Log.d(PluginManager.class.getSimpleName(), "loadclass, dex = " + currentDexPath
				+ ", need reload = " + isNeedReload);
		Class<?> clazz = null;
		if (isNeedReload) {
			// changed, need reload
			clazz = findClass(className);
		} else {
			// not change, use default logic
			clazz = super.loadClass(className);
		}
		return clazz;
	}

	public static DLClassLoader getClassLoader(String apkPath, String dexPath, ClassLoader parent) {
		final String dexDirPath;
		if (!dexPath.isEmpty()) {
			dexDirPath = dexPath.substring(0, dexPath.lastIndexOf(File.separator) + 1);
		} else {
			dexDirPath = SYSTEM_DEX_PATH;
		}
		DLClassLoader cl = new DLClassLoader(apkPath, dexDirPath, null, parent);
		mDexPaths.put(cl, dexPath);
		return cl;
	}

	public static void removeDexPath(ClassLoader loader) {
		mDexPaths.remove(loader);
	}

}
