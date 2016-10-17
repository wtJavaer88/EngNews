package com.wnc.news.engnews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.engnews.R;
import com.wnc.news.api.nba.NbaTeamApi;
import com.wnc.news.api.soccer.NewsInfo;
import com.wnc.news.api.soccer.SoccerTeamApi;

public class NewsListActivity extends ListActivity
{
	private Button bt_add;
	private EditText et_item;
	private ArrayList<HashMap<String, Object>> listItems; // 存放文字、图片信息
	private SimpleAdapter listItemAdapter; // 适配器
	private String type;

	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.activity_newslist);

		bt_add = (Button) findViewById(R.id.bt_add);
		et_item = (EditText) findViewById(R.id.et_item);

		if (getIntent() != null && getIntent().hasExtra("type"))
		{
			type = getIntent().getStringExtra("type");
		} // initListView();
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				Message msg = new Message();
				msg.what = 1;
				if (type.equalsIgnoreCase("nba"))
				{
					msg.obj = new NbaTeamApi("san-antonio-spurs").getAllNews();
				}
				else
				{
					msg.obj = new SoccerTeamApi("arsenal").getAllNews();
				}
				handler.sendMessage(msg);
			}
		}).start();

		bt_add.setOnClickListener(new ClickEvent());
	}

	Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 1:
				initListView((List<NewsInfo>) msg.obj);
				// newsContentTv.setText((CharSequence) msg.obj);
				break;
			case 2:
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 设置适配器内容
	 * 
	 * @param news
	 */
	private void initListView(List<NewsInfo> news)
	{
		listItems = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < news.size(); i++)
		{
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("title", news.get(i).getTitle()); // 文字
			map.put("image", R.drawable.ic_launcher); // 图片
			map.put("date", news.get(i).getDate());
			map.put("sub_text", news.get(i).getSub_text());
			map.put("url", news.get(i).getUrl());
			map.put("news_pic", news.get(i).getHead_pic());
			map.put("news_class", news.get(i).getWebsite().getNews_class());
			listItems.add(map);
		}
		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new SimpleAdapter(this, listItems, // listItems数据源
				R.layout.list_item, // ListItem的XML布局实现
				new String[] { "title", "image" }, // 动态数组与ImageItem对应的子项
				new int[] { R.id.ItemTitle, R.id.ItemImage } // list_item.xml布局文件里面的一个ImageView的ID,一个TextView
																// 的ID
		);
		this.setListAdapter(listItemAdapter);
	}

	@Override
	protected void onListItemClick(ListView lv, View v, int position, long id)
	{
		setTitle("你点击第" + position + "行");
		HashMap<String, Object> map = (HashMap<String, Object>) lv.getItemAtPosition(position);
		startActivity(new Intent(this, NewsContentActivity.class).putExtra("news_url", map.get("url").toString()).putExtra("news_pic", map.get("news_pic").toString()).putExtra("news_class", map.get("news_class").toString()));
	}

	class ClickEvent implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			// 向ListView里添加一项
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemTitle", "Music： " + et_item.getText().toString());
			map.put("ItemImage", R.drawable.ic_launcher); // 每次都放入同样的图片资源ID
			listItems.add(map);
			// 重新设置适配器
			setListAdapter(listItemAdapter);
		}
	}

}
