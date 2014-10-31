package com.zt.plugin2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.konka.android.tv.KKPictureManager;
import com.konka.dynamicplugin.plugin.IPlugin;

public class PluginImpl implements IPlugin {
	private Context mHostContext;
	private LayoutInflater mInflater;
	private KKPictureManager mPictureManager;
	private short miBacklightValue = 0;
	private Thread mSetBacklightByThread = null;
	private boolean mbSetBacklightByThreadDone = false;
	
	@Override
	public void setContext(Context context) {
		mHostContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mPictureManager = KKPictureManager.getInstance(mHostContext);
		miBacklightValue = mPictureManager.getBacklight();

	}
	
	private void setBacklightByThread() {
		Log.d("log", "#### setBacklightByThread()");
		Log.d("log", "#### miBacklightValue = " + miBacklightValue);
		if(mSetBacklightByThread!=null && mSetBacklightByThread.isAlive()){
			return;
		}else{
			mSetBacklightByThread = new Thread() {
				@SuppressLint("NewApi")
				public void run() {
					
					while ( mbSetBacklightByThreadDone==false) {

						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						mPictureManager.setBacklight(miBacklightValue);
						Log.d("log", "#### mPictureManager.setBacklight()");
						mbSetBacklightByThreadDone = true;
					}

				}; 
			};
			mSetBacklightByThread.start();
		}		
	}

	@Override
	public <T> T getPluginView() {
		final LinearLayout layout = (LinearLayout) mInflater.inflate(R.layout.seekbar, null);
		final TextView text = (TextView) layout.findViewById(R.id.text);
		final SeekBar seekBar = (SeekBar) layout.findViewById(R.id.seekbar);
		seekBar.setProgress(miBacklightValue);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				text.setText("plugin progres = " + progress);
				
				if (progress <= 0)
					return;

				if (progress >= 100)
					return;

				miBacklightValue = (short) progress;

				mbSetBacklightByThreadDone = false;
				setBacklightByThread();

			}
		});
		return (T) layout;
	}

}
