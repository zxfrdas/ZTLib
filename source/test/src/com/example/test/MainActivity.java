package com.example.test;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.example.test.IPublisher.INotifier;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity implements INotifier<TestItem> {
	IPublisher<TestItem> mPublisher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
		mPublisher = new MutipleSourcePublisher();
		new Thread(new Runnable() {

			@Override
			public void run() {
				mPublisher.book("test", MainActivity.this);
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mPublisher.book("haha", MainActivity.this);
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mPublisher.book("wawa", MainActivity.this);
			}
		}).start();
    }

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onPublish(Collection<TestItem> items) {
		for (TestItem item : items) {
			Log.d("ZT", "videoname = " + item.getVideoName());
			mPublisher.displayDetails(item);
		}		
	}
    
}
