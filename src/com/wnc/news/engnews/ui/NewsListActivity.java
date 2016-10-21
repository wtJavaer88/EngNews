package com.wnc.news.engnews.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

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
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.dao.NewsDao;

public class NewsListActivity extends ListActivity implements OnClickListener
{
    private Button bt_search;
    private EditText et_item;
    private ArrayList<HashMap<String, Object>> listItems; // 存放文字、图片信息
    private SimpleAdapter listItemAdapter; // 适配器
    private String type;
    static Logger log = Logger.getLogger(NewsListActivity.class);

    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.activity_newslist);

        bt_search = (Button) findViewById(R.id.bt_search);
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
                    // msg.obj = new
                    // NbaTeamApi("san-antonio-spurs").getAllNews();
                    msg.obj = NewsDao.findAllNBANews();
                }
                else
                {
                    // msg.obj = new SkySportsTeamApi("arsenal").getAllNews();
                    msg.obj = NewsDao.findAllSoccerNews();
                }
                handler.sendMessage(msg);
            }
        }).start();

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
            map.put("news_info", news.get(i));
            map.put("title", news.get(i).getTitle()); // 标题
            map.put("date", news.get(i).getDate()); // 日期
            map.put("image", R.drawable.ic_launcher); // 图片
            listItems.add(map);
        }
        // 生成适配器的Item和动态数组对应的元素
        listItemAdapter = new SimpleAdapter(this, listItems, // listItems数据源
                R.layout.list_item, // ListItem的XML布局实现
                new String[]
                { "title", "date", "image" }, // 动态数组与ImageItem对应的子项
                new int[]
                { R.id.ItemTitle, R.id.ItemDate, R.id.ItemImage } // list_item.xml布局文件里面的一个ImageView的ID,一个TextView
        // 的ID
        );
        this.setListAdapter(listItemAdapter);
    }

    @Override
    protected void onListItemClick(ListView lv, View v, int position, long id)
    {
        HashMap<String, Object> map = (HashMap<String, Object>) lv
                .getItemAtPosition(position);
        NewsContentActivity.news_info = (NewsInfo) map.get("news_info");
        startActivity(new Intent(this, NewsContentActivity.class));
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.bt_search:
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    final String text = et_item.getText().toString();
                    Message msg = new Message();
                    msg.what = 1;
                    final List<NewsInfo> search = NewsDao.search(text);
                    log.info("搜索" + text + "结果:" + search.size());
                    msg.obj = search;
                    handler.sendMessage(msg);
                }
            }).start();
            break;

        default:
            break;
        }

    }
}
