package com.zt.daotest;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.zt.lib.database.ExecCondition;
import com.zt.lib.database.IDAO;
import com.zt.lib.util.Reflector;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}

		@Override
		public void onResume() {
			super.onResume();
			IDAO<TestItem> test = new TestItemDAO(getActivity().getApplicationContext(), TestItem.class);
			ArrayList<TestItem> items = new ArrayList<TestItem>();
			for (int i = 0; i < 10; i ++) {
				TestItem item = new TestItem();
				item.testBoolean = i % 2 == 0 ? true : false;
				item.testFloat = 2.3 + i;
				item.testInt = 10 + i;
				item.testString = "first" + i;
				items.add(item);
			}
//			test.insert(items);
//			test.update(items.get(6), ExecCondition.Build().where("testString").equal("first1").done());
//			test.delete(ExecCondition.Build().where("testInt").lessEqual(16).and().where("testBoolean").equal(true).done());
//			test.deleteAll();
			ArrayList<TestItem> items2 = new ArrayList<TestItem>();
			for (int i = 0; i < 10; i ++) {
				TestItem item = new TestItem();
				item.testBoolean = i % 2 != 0 ? true : false;
				item.testFloat = 20.3 + i;
				item.testInt = 100 + i;
				item.testString = "first" + i;
				items.add(item);
			}
//			test.updateList(items2, ExecCondition.Build().where("testInt").equal(arg))
			List<TestItem> item3 = test.query(ExecCondition.Build().where("testInt").more(12).and()
					.where("testBoolean").equal(true).and().where("testString").notEqual("first4").done());
			for (TestItem item : item3) {
				System.out.println(Reflector.toString(item));
			}
			System.out.println("db count = " + test.getCount());
		}
	}

}
