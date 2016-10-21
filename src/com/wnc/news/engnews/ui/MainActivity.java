package com.wnc.news.engnews.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Map;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.engnews.R;
import com.wnc.basic.BasicDateUtil;
import com.wnc.news.api.autocache.CacheSchedule;
import common.app.ConfirmUtil;
import common.app.Log4jUtil;
import common.app.SysInit;
import common.uihelper.MyAppParams;
import common.uihelper.PositiveEvent;

public class MainActivity extends Activity implements OnClickListener,
        UncaughtExceptionHandler
{
    private Button bt_nba;
    private Button bt_soccer;
    private Button btn_cache, btn_cache_clear;
    private TextView proTv;

    Logger log = Logger.getLogger(MainActivity.class);

    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        Thread.setDefaultUncaughtExceptionHandler(this);

        Log4jUtil.configLog(MyAppParams.LOG_FOLDER
                + BasicDateUtil.getCurrentDateString() + ".txt");
        log.info("App Start...");

        initView();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                SysInit.init(MainActivity.this);
                // new NewsTest().topicUpdate();

                // new NewsTest().test();
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
            new NewsTest().cacheTeams(cacheSchedule);
            // new NewsTest().cacheArsenal(cacheSchedule);

            final Thread thread = new Thread(new Runnable()
            {

                @Override
                public void run()
                {
                    while (true)
                    {
                        Map<String, Integer> map = cacheSchedule.getMap();
                        String process = "";

                        final boolean allCached = cacheSchedule.isAllCached();
                        int sum = 0;
                        for (Map.Entry<String, Integer> entry : map.entrySet())
                        {
                            process += entry.getKey() + " 缓存的新闻数:"
                                    + entry.getValue() + "  ";
                            sum += entry.getValue();
                        }
                        String head = "";
                        Message msg = new Message();
                        msg.what = 1;

                        if (allCached)
                        {
                            head = " 缓存全部结束(" + sum + ")\n";
                            msg.obj = head + process;
                            handler.sendMessage(msg);
                            break;
                        }
                        else
                        {
                            head = "正在缓存... (" + sum + ")\n";
                            msg.obj = head + process;
                            handler.sendMessage(msg);
                        }
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
        log.error("uncaughtException   ", ex);
    }
}
