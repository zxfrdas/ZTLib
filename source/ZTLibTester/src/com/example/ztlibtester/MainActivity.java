package com.example.ztlibtester;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.ztlibtester.auto.TestItemDAO;
import com.example.ztlibtester.auto.TestItemProxy;
import com.zt.lib.database.dao.IDAO;

public class MainActivity extends Activity implements OnClickListener {

	Button insert;
	Button query;
	Button delete;
	TextView time;
	IDAO<TestItem> dao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		insert = (Button) findViewById(R.id.insert);
		query = (Button) findViewById(R.id.query);
		delete = (Button) findViewById(R.id.delete);
		time = (TextView) findViewById(R.id.time);
		dao = TestItemDAO.getInstance(getApplicationContext());
		insert.setOnClickListener(this);
		query.setOnClickListener(this);
		delete.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		long use = 0;
		if (R.id.insert == id) {
			use = insert(5000);
		} else if (R.id.query == id) {
			use = query();
		} else if (R.id.delete == id) {
			use = delete();
		}
		int count = count();
		time.setText("耗时 = " + use + ", 平均一次操作耗时 = " + (double) use / count);
	}
	
	public long insert(int count) {
		ArrayList<TestItem> items = new ArrayList<TestItem>();
		for (int i = 0; i < count; i++) {
			TestItem item = new TestItem();
			item.testBoolean = i % 2 == 0 ? true : false;
			item.testFloat = 2.3 + i;
			item.testInt = 10 + i;
			item.testString = "first" + i;
			items.add(item);
		}
		long start = SystemClock.currentThreadTimeMillis();
		dao.insert(items);
		return SystemClock.currentThreadTimeMillis() - start;
	}
	
	public long query() {
		long start = SystemClock.currentThreadTimeMillis();
//		dao.queryAll();
		dao.query(dao.buildCondition().where(TestItemProxy.testBoolean).equal(true)
				.buildDone());
		return SystemClock.currentThreadTimeMillis() - start;
	}
	
	public long delete() {
		long start = SystemClock.currentThreadTimeMillis();
		dao.deleteAll();
		return SystemClock.currentThreadTimeMillis() - start;
	}
	
	public int count() {
		long start = SystemClock.currentThreadTimeMillis();
		int count = dao.getCount();
		Log.d("ZT", (SystemClock.currentThreadTimeMillis() - start) + "");
		Log.d("ZT", "count = " + count);
		return count;
	}
	
	
}
