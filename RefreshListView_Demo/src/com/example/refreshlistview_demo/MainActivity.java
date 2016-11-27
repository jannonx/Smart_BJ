package com.example.refreshlistview_demo;

import com.example.refreshlistviewdemo.RefreshListView;
import com.example.refreshlistviewdemo.RefreshListView.OnRefreshDataListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MainActivity extends Activity {

	private RefreshListView rlv_refresh;
	private Mydapter mydapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initData();
		initEvent();

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 处理消息
			rlv_refresh.updateState();
		};
	};

	private void initEvent() {
		rlv_refresh.setOnRefreshDataListener(new OnRefreshDataListener() {

			@Override
			public void refreshData() {
				// 发送一个空的延迟消息
				mHandler.sendMessageDelayed(mHandler.obtainMessage(), 2000);
			}

			@Override
			public void loadingMore() {
				// 发送一个空的延迟消息
				mHandler.sendMessageDelayed(mHandler.obtainMessage(), 2000);

			}
		});
	}

	private class Mydapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 20;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv = new TextView(MainActivity.this);
			tv.setText("jannonx: " + position);
			tv.setTextSize(20);
			return tv;
		}

	}

	private void initData() {
		if (mydapter == null) {
			mydapter = new Mydapter();
			rlv_refresh.setAdapter(mydapter);
		} else {
			mydapter.notifyDataSetChanged();
		}

	}

	private void initView() {
		// 加载listview
		rlv_refresh = (RefreshListView) findViewById(R.id.lv_refresh);

	}

}
