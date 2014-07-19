package com.example.ztlibtester;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

import com.zt.lib.database.ExecCondition;
import com.zt.lib.database.IDAO;
import com.zt.lib.util.Reflector;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		IDAO<TestItem> test = new TestItemDAO(getApplicationContext(), TestItem.class);
		ArrayList<TestItem> items = new ArrayList<TestItem>();
		for (int i = 0; i < 10; i++) {
			TestItem item = new TestItem();
			item.testBoolean = i % 2 == 0 ? true : false;
			item.testFloat = 2.3 + i;
			item.testInt = 10 + i;
			item.testString = "first" + i;
			items.add(item);
		}
		// test.insert(items);
		// test.update(items.get(6),
		// ExecCondition.Build().where("testString").equal("first1").done());
		// test.delete(ExecCondition.Build().where("testInt").lessEqual(16).and().where("testBoolean").equal(true).done());
		// test.deleteAll();
		ArrayList<TestItem> items2 = new ArrayList<TestItem>();
		for (int i = 0; i < 10; i++) {
			TestItem item = new TestItem();
			item.testBoolean = i % 2 != 0 ? true : false;
			item.testFloat = 20.3 + i;
			item.testInt = 100 + i;
			item.testString = "first" + i;
			items.add(item);
		}
		// test.updateList(items2,
		// ExecCondition.Build().where("testInt").equal(arg))
		List<TestItem> item3 = test.query(ExecCondition.Build().where("testInt").more(12).and()
				.where("testBoolean").equal(true).and().where("testString").notEqual("first4")
				.done());
		for (TestItem item : item3) {
			System.out.println(Reflector.toString(item));
		}
		System.out.println("db count = " + test.getCount());
	}
}
