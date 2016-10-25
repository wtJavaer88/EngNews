package com.wnc.news.engnews.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.widget.act.abs.AutoCompletable;
import net.widget.act.abs.MyActAdapter;
import net.widget.act.token.SemicolonTokenizer;

import org.apache.log4j.Logger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SimpleAdapter;

import com.example.engnews.R;
import com.wnc.news.act.ActTeam;
import com.wnc.news.act.TeamAutoAdapter;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.dao.NewsDao;
import com.wnc.news.engnews.helper.ViewNewsHolder;
import common.uihelper.MyAppParams;

@SuppressLint("ValidFragment")
public class PageFragment extends ListFragment implements
        UncaughtExceptionHandler, OnClickListener
{
    View view;
    public boolean hasExecute = false;
    Logger log = Logger.getLogger(PageFragment.class);
    private Button bt_search;
    private List<AutoCompletable> items = new ArrayList<AutoCompletable>();

    private MultiAutoCompleteTextView actv;
    private ArrayList<HashMap<String, Object>> listItems; // 存放文字、图片信息
    private SimpleAdapter listItemAdapter; // 适配器
    private String type;

    public PageFragment(String type)
    {
        this.type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        this.view = inflater.inflate(R.layout.activity_newslist, null);
        return this.view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        if (!this.hasExecute)
        {
            this.hasExecute = true;
        }
        Thread.setDefaultUncaughtExceptionHandler(this);
        initview();
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                Message msg = new Message();
                msg.what = 1;
                if (type.equalsIgnoreCase(MyAppParams.getInstance()
                        .getBaskModelName()))
                {
                    msg.obj = NewsDao.findAllNBANews();
                }
                else if (type.equalsIgnoreCase(MyAppParams.getInstance()
                        .getSoccModelName()))
                {
                    msg.obj = NewsDao.findAllSoccerNews();
                }
                else if (type.equalsIgnoreCase(MyAppParams.getInstance()
                        .getForuModelName()))
                {
                    msg.obj = NewsDao.findAllForumInfos();
                }
                else
                {
                    msg.obj = new ArrayList<NewsInfo>();
                }
                handler.sendMessage(msg);
            }
        }).start();

        bt_search.setOnClickListener(this);
        super.onActivityCreated(savedInstanceState);
    }

    private void initview()
    {
        bt_search = (Button) view.findViewById(R.id.bt_search);
        actv = (MultiAutoCompleteTextView) view.findViewById(R.id.et_item);
        items.add(new ActTeam("arsenal", "阿森纳"));
        items.add(new ActTeam("barcelona", "巴塞罗那"));
        items.add(new ActTeam("manchester-united", "曼联"));
        items.add(new ActTeam("manchester-city", "曼城"));
        items.add(new ActTeam("tottenham-hotspur", "热刺"));
        items.add(new ActTeam("real-madrid", "皇马"));
        items.add(new ActTeam("liverpool", "利物浦"));
        items.add(new ActTeam("san-antonio-spurs", "马刺"));
        items.add(new ActTeam("golden-state-warriors", "勇士"));
        items.add(new ActTeam("skysports", "天空体育"));
        items.add(new ActTeam("squawka", ""));
        items.add(new ActTeam("basketballinsiders", "深度篮球"));
        items.add(new ActTeam("reddit", ""));
        items.add(new ActTeam("soccer", "足球"));
        items.add(new ActTeam("nba", "篮球"));

        MyActAdapter adapter = new TeamAutoAdapter(getActivity(), items, 12);
        actv.setAdapter(adapter);
        actv.setThreshold(1);// 输入几个字符后开始提示
        actv.setTokenizer(new SemicolonTokenizer(" ", " "));
        actv.setOnTouchListener(new View.OnTouchListener()
        {
            // 按住和松开的标识
            int touch_flag = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                touch_flag++;
                if (touch_flag % 2 == 1)
                {
                    // 手动调用
                    actv.showDropDown();
                }
                return false;
            }
        });
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

    List<NewsInfo> news = new ArrayList<NewsInfo>();

    public List<NewsInfo> getNews()
    {
        return news;
    }

    /**
     * 设置适配器内容
     * 
     * @param news
     */
    private void initListView(List<NewsInfo> news)
    {
        this.news = news;
        listItems = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < news.size(); i++)
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("news_info", news.get(i));
            map.put("title", news.get(i).getTitle()); // 标题
            final String date = news.get(i).getDate();
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
        listItemAdapter = new SimpleAdapter(getActivity(), listItems, // listItems数据源
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
    public void onListItemClick(ListView lv, View v, int position, long id)
    {
        HashMap<String, Object> map = (HashMap<String, Object>) lv
                .getItemAtPosition(position);
        ViewNewsHolder.refreh((NewsInfo) map.get("news_info"));
        NewsContentActivity.news_info = (NewsInfo) map.get("news_info");
        startActivity(new Intent(getActivity(), NewsContentActivity.class));
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
                    final String text = actv.getText().toString();
                    Message msg = new Message();
                    msg.what = 1;
                    final List<NewsInfo> search = NewsDao.search(text + " "
                            + type);
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

    @Override
    public void uncaughtException(Thread arg0, Throwable ex)
    {
        log.error("uncaughtException   ", ex);
    }

}
