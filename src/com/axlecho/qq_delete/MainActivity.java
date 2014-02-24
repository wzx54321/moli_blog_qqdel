package com.axlecho.qq_delete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ListView listView;
	private List<Map<String, Object>> listDatas = new ArrayList<Map<String, Object>>();

	private SimpleAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		for (int i = 0; i <= 10; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("text", String.valueOf(i));
			listDatas.add(map);
		}

		String from[] = new String[] { "text" };
		int to[] = new int[] { R.id.text };
		adapter = new SimpleAdapter(this, listDatas, R.layout.list_item_view, from, to);
		listView = new ListViewEx(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				Toast.makeText(getApplicationContext(), String.valueOf(pos) + "was pressed", Toast.LENGTH_SHORT).show();
			}

		});
		setContentView(listView);

		listView.setBackgroundColor(0xefefef);
	}

	private class ListViewEx extends ListView implements OnTouchListener {
		private int pointX = -1;
		private int pointY = -1;
		private int position = -1;
		private int endX = -1;
		private int endY = -1;
		private int newpos = -1;
		private Button curDel_btn;
		private TextView curMask;
		private Context parentContext;

		public ListViewEx(Context context) {
			super(context);
			setOnTouchListener(this);
			parentContext = context;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// 手指按下,计算焦点位于ListView的那个条目
				pointX = (int) event.getX();
				pointY = (int) event.getY();
				position = listView.pointToPosition(pointX, pointY);
				if (curDel_btn != null) {
					curDel_btn.setVisibility(View.GONE);
				}
				if (curMask != null) {
					curMask.setVisibility(View.GONE);
				}

				break;
			case MotionEvent.ACTION_MOVE:

				break;
			case MotionEvent.ACTION_UP:
				endX = (int) event.getX();
				endY = (int) event.getY();
				newpos = listView.pointToPosition(endX, endY);
				// 原本想着加上这个条件（newpos==position）是不是更精确些，
				// 经过实践发现，其实我们在滑动listView的列表的时候有时候更渴望有滑动就ok
				// 只允许从右向左滑
				if (endX - pointX < -50) {
					// 获取到ListView第一个可见条目的position
					int firstVisiblePosition = listView.getFirstVisiblePosition();

					View view = listView.getChildAt(position - firstVisiblePosition);

					if (view == null)
						break;
					Button delbtn = (Button) view.findViewById(R.id.btn_del);
					TextView delbtnMask = (TextView) view.findViewById(R.id.btn_del_mask);
					TextView bottomMask = (TextView) view.findViewById(R.id.bottom_mask);

					bottomMask.setVisibility(View.VISIBLE);
					delbtn.setVisibility(View.VISIBLE);
					delbtnMask.setVisibility(View.VISIBLE);
					delbtnMask.startAnimation(AnimationUtils.loadAnimation(parentContext, R.anim.delete_show));
					delbtnMask.setVisibility(View.INVISIBLE);

					curDel_btn = delbtn;
					curMask = bottomMask;
					delbtn.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							listDatas.remove(position);
							adapter.notifyDataSetChanged();
						}
					});
				}
				break;

			default:
				break;
			}
			return true;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
