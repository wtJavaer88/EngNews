package com.wnc.news.engnews;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.engnews.R;
import com.wnc.news.api.autocache.CacheSchedule;
import com.wnc.news.dao.DictionaryDao;

public class MainActivity extends Activity implements OnClickListener,
        UncaughtExceptionHandler
{
    private Button bt_nba;
    private Button bt_soccer;
    private Button btn_cache, btn_cache_clear;

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
                DictionaryDao.initTopics();
            }
        }).start();
    }

    private void initView()
    {
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
            final CacheSchedule cacheSchedule = new CacheSchedule();
            cacheSchedule.teamCache("arsenal");
            break;
        case R.id.btn_cache_clear:
            final CacheSchedule cacheSchedule2 = new CacheSchedule();
            cacheSchedule2.clearCache("");
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
