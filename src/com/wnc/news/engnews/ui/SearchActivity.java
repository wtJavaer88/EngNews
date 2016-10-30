package com.wnc.news.engnews.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
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
import com.wnc.basic.BasicStringUtil;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.dao.NewsDao;
import com.wnc.news.engnews.helper.NewsContentUtil;
import com.wnc.news.engnews.helper.SpecialAdapter;
import com.wnc.news.engnews.helper.ViewNewsHolder;

public class SearchActivity extends ListActivity implements OnClickListener
{
	private Button bt_search;
	private EditText et_item;
	private ArrayList<HashMap<String, Object>> listItems; // 存放文字、图片信息
	private SimpleAdapter listItemAdapter; // 适配器
	private String keyword = "";
	static Logger log = Logger.getLogger(SearchActivity.class);

	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_newslist);

		bt_search = (Button) findViewById(R.id.bt_search);
		et_item = (EditText) findViewById(R.id.et_item);

		if (getIntent() != null && getIntent().hasExtra("keyword"))
		{
			keyword = getIntent().getStringExtra("keyword");
			et_item.setText(keyword);
			search(keyword);
		} // initListView();

		bt_search.setOnClickListener(this);
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
			NewsInfo newsInfo = news.get(i);
			map.put("news_info", newsInfo);
			map.put("title", newsInfo.getTitle()); // 标题
			CharSequence section = NewsContentUtil.getSection(newsInfo.getHtml_content(), keyword);
			if (BasicStringUtil.isNullString(section.toString()))
			{
				section = newsInfo.getTitle();
			}
			map.put("s_title", section);
			final String date = newsInfo.getDate();
			if (date != null && date.length() >= 8)
			{
				map.put("date", date.replace("-", "").substring(4, 8)); // 日期
			}
			else
			{
				map.put("date", "");
			}
			map.put("image", R.drawable.ic_launcher); // 图片
			listItems.add(map);
		}
		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new SpecialAdapter(this, listItems, // listItems数据源
				R.layout.list_item, // ListItem的XML布局实现
				new String[] { "s_title", "date", "image" }, // 动态数组与ImageItem对应的子项
				new int[] { R.id.ItemTitle, R.id.ItemDate, R.id.ItemImage } // list_item.xml布局文件里面的一个ImageView的ID,一个TextView
		// 的ID
		).setKey(keyword);
		this.setListAdapter(listItemAdapter);
	}

	@Override
	protected void onListItemClick(ListView lv, View v, int position, long id)
	{
		HashMap<String, Object> map = (HashMap<String, Object>) lv.getItemAtPosition(position);
		final NewsInfo newsInfo = (NewsInfo) map.get("news_info");
		ViewNewsHolder.refreh(newsInfo);
		NewsContentActivity.news_info = newsInfo;
		startActivity(new Intent(this, NewsContentActivity.class));
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.bt_search:
			keyword = et_item.getText().toString();
			search(keyword);
			break;

		default:
			break;
		}

	}

	private void search(final String text)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				Message msg = new Message();
				msg.what = 1;
				final List<NewsInfo> matchedList = NewsDao.search(text);
				log.info("搜索" + text + "结果:" + matchedList.size());
				if (matchedList.size() > 0)
				{
					ViewNewsHolder.refrehList(matchedList);
				}
				msg.obj = matchedList;
				handler.sendMessage(msg);
			}
		}).start();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			// land
			System.out.println("ORIENTATION_LANDSCAPE");

		}
		else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			// port
			System.out.println("ORIENTATION_PORTRAIT");
		}
	}
}