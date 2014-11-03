package com.zt.plugin2;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MySeekbar extends LinearLayout implements OnSeekBarChangeListener,
		OnFocusChangeListener {
	private SeekBar mSeekBar;
	private TextView mTextView;

	public MySeekbar(Context context) {
		super(context);
	}

	public MySeekbar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MySeekbar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.seekbar, this);
		mSeekBar = (SeekBar) findViewById(R.id.seekbar);
		mTextView = (TextView) findViewById(R.id.text);
		mSeekBar.setOnSeekBarChangeListener(this);
		mSeekBar.setOnFocusChangeListener(this);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		mTextView.setText("plugin progres = " + progress);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		String text = (hasFocus ? "获取到焦点" : "丢失焦点") + ", view = " + v;
		Log.d("ZT", text);
		Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
	}

}
