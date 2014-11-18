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

import dalvik.system.DexClassLoader;

public class DLClassLoader extends DexClassLoader {
	private static final String SYSTEM_DEX_PATH = "/data/misc/konka/plugins/dex";

	protected DLClassLoader(String dexPath, String optimizedDirectory,
			String libraryPath, ClassLoader parent) {
		super(dexPath, optimizedDirectory, libraryPath, parent);
	}

	public static DLClassLoader getClassLoader(String apkPath, String dexPath,
			ClassLoader parent) {
		final String dexDirPath;
		if (!dexPath.isEmpty()) {
			dexDirPath = dexPath.substring(0,
					dexPath.lastIndexOf(File.separator) + 1);
		} else {
			dexDirPath = SYSTEM_DEX_PATH;
		}
		return new DLClassLoader(apkPath, dexDirPath, null, parent);
	}
}
