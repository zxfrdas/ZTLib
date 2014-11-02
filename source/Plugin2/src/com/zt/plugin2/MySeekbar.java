package com.zt.plugin2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class MySeekbar extends LinearLayout implements OnSeekBarChangeListener {
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

}
