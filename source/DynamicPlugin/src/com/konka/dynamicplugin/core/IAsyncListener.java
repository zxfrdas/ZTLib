package com.konka.dynamicplugin.core;

public interface IAsyncListener {
	void success();
	void fail(String reason);
}
