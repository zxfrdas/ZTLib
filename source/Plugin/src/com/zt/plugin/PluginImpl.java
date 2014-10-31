package com.zt.plugin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

import com.konka.dynamicplugin.plugin.IPlugin;

public class PluginImpl implements IPlugin {
	private Context mHostContext;
	private LayoutInflater mInflater;

	public PluginImpl() {
		
	}
	
	@Override
	public void setContext(Context context) {
		mHostContext = context;
		mInflater = (LayoutInflater) mHostContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public <T> T getPluginView() {
		Button view = (Button) mInflater.inflate(R.layout.button, null);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		view.setLayoutParams(params);
		view.setText("view in plugin");
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(mHostContext, "点击了plugin的button", Toast.LENGTH_SHORT).show();
			}
		});
		return (T) view;
	}

}
