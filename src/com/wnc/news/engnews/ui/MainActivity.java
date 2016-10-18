package com.wnc.news.engnews.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.engnews.R;
import com.wnc.news.api.autocache.CacheSchedule;
import com.wnc.news.dao.DictionaryDao;
import common.app.ConfirmUtil;
import common.uihelper.PositiveEvent;

public class MainActivity extends Activity implements OnClickListener,
        UncaughtExceptionHandler
{
    private Button bt_nba;
    private Button bt_soccer;
    private Button btn_cache, btn_cache_clear;
    private TextView proTv;

    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        Thread.setDefaultUncaughtExceptionHandler(this);

        initView();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // List<Club> allClubs = new LeagueApi(712).getAllClubs();
                // System.out.println("allClubs数量:" + allClubs.size());
                // NewsDao.insertClubs(allClubs);
                // allClubs = new LeagueApi(682).getAllClubs();
                // System.out.println("allClubs数量:" + allClubs.size());
                // NewsDao.insertClubs(allClubs);
                // allClubs = new LeagueApi(717).getAllClubs();
                // System.out.println("allClubs数量:" + allClubs.size());
                // NewsDao.insertClubs(allClubs);
                // System.out.println(NewsDao.isSoccerTeam("barcelona"));
                DictionaryDao.initTopics();
            }
        }).start();
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
            case 1:
                proTv.setText(msg.obj.toString());
                break;
            }
        }
    };

    private void initView()
    {
        proTv = (TextView) findViewById(R.id.tv_cache_process);

        bt_nba = (Button) findViewById(R.id.btn_nba);
        bt_soccer = (Button) findViewById(R.id.btn_soccer);
        btn_cache = (Button) findViewById(R.id.btn_cache);
        btn_cache_clear = (Button) findViewById(R.id.btn_cache_clear);

        bt_nba.setOnClickListener(this);
        bt_soccer.setOnClickListener(this);
        btn_cache.setOnClickListener(this);
        btn_cache_clear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.btn_nba:
            startActivity(new Intent(this, NewsListActivity.class).putExtra(
                    "type", "nba"));
            break;
        case R.id.btn_soccer:
            startActivity(new Intent(this, NewsListActivity.class).putExtra(
                    "type", "soccer"));
            break;
        case R.id.btn_cache:
            System.out.println("开始缓存!");
            final CacheSchedule cacheSchedule = new CacheSchedule();
            // cacheSchedule.addTeam("arsenal");
            // cacheSchedule.addTeam("barcelona");
            cacheSchedule.addTeam("manchester-united");
            cacheSchedule.addTeam("manchester-city");
            cacheSchedule.addTeam("liverpool");
            cacheSchedule.addTeam("tottenham-hotspur");
            cacheSchedule.addTeam("real-madrid");
            cacheSchedule.addTeam("san-antonio-spurs");
            cacheSchedule.teamCache();
            final Thread thread = new Thread(new Runnable()
            {

                @Override
                public void run()
                {
                    while (true)
                    {
                        Map<String, Integer> map = cacheSchedule.getMap();
                        String process = "";
                        for (Map.Entry<String, Integer> entry : map.entrySet())
                        {
                            process += entry.getKey() + " 缓存的新闻数:"
                                    + entry.getValue() + "  ";
                        }
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = process;
                        handler.sendMessage(msg);
                        try
                        {
                            Thread.sleep(5000);
                        }
                        catch (InterruptedException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.setDaemon(true);
            thread.start();
            break;
        case R.id.btn_cache_clear:
            ConfirmUtil.confirmDelete(this, "确定要清除缓存吗?", new PositiveEvent()
            {
                @Override
                public void onConfirmPositive()
                {
                    final CacheSchedule cacheSchedule2 = new CacheSchedule();
                    cacheSchedule2.clearCache();
                    System.out.println("缓存清空!");
                }
            });
            break;
        default:
            break;
        }
    }

    @Override
    public void uncaughtException(Thread arg0, Throwable ex)
    {
        Log.i("AAA", "uncaughtException   " + ex);
        for (StackTraceElement o : ex.getStackTrace())
        {
            System.out.println(o.toString());
        }
    }
}
