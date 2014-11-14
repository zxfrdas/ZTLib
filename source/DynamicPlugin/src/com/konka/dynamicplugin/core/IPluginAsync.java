package com.konka.dynamicplugin.core;

import java.util.List;


public interface IPluginAsync {
	enum Type {
		INIT, INSTALL, UNINSTALL, ENABLE, DISABLE
	}
	
	interface IListener {
		void success(Type type, List<PluginInfo> changed);
		void fail(String reason);
	}

}
