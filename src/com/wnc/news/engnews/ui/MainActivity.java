package com.wnc.news.engnews.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;

import org.apache.log4j.Logger;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.engnews.R;
import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicNumberUtil;
import com.wnc.news.api.autocache.CacheSchedule;
import com.wnc.news.engnews.helper.NewsTest;
import com.wnc.string.PatternUtil;
import common.app.ConfirmUtil;
import common.app.Log4jUtil;
import common.app.SysInit;
import common.app.ToastUtil;
import common.uihelper.MyAppParams;
import common.uihelper.PositiveEvent;

public class MainActivity extends BaseVerActivity implements OnClickListener,
        UncaughtExceptionHandler
{
    private Button bt_nba;
    private Button bt_soccer;
    private Button btn_cache, btn_cache_clear;
    private TextView proTv;

    private final int MESSAGE_EXIT_CODE = 0;
    private final int MESSAGE_PROCESS_CODE = 1;
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
                if (BasicDateUtil.getCurrentHour() > 7
                        && BasicDateUtil.getCurrentHour() < 10)
                {
                    new NewsTest().topicUpdate();
                }
            }
        }).start();
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
            case MESSAGE_PROCESS_CODE:
                proTv.setText(msg.obj.toString());
                break;
            case MESSAGE_EXIT_CODE:
                isExit = false;
                break;
            }
        }
    };

    private void initView()
    {
        proTv = (TextView) findViewById(R.id.tv_cache_process);
        proTv.setMovementMethod(ScrollingMovementMethod.getInstance());

        bt_nba = (Button) findViewById(R.id.btn_nba);
        bt_soccer = (Button) findViewById(R.id.btn_soccer);
        btn_cache = (Button) findViewById(R.id.btn_cache);
        btn_cache_clear = (Button) findViewById(R.id.btn_cache_clear);

        findViewById(R.id.btn_forums).setOnClickListener(this);
        bt_nba.setOnClickListener(this);
        bt_soccer.setOnClickListener(this);
        btn_cache.setOnClickListener(this);
        btn_cache_clear.setOnClickListener(this);
        findViewById(R.id.btn_test).setOnClickListener(this);
    }

    Thread cacheWatchThread;
    public volatile boolean cachedExit = false;

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.btn_test:
            startActivity(new Intent(this, MainActivity2.class));
            break;
        case R.id.btn_nba:
            new NewsTest().testsameAntonym();
            break;
        case R.id.btn_soccer:
            break;
        case R.id.btn_forums:
            break;
        case R.id.btn_cache:
            System.out.println("开始缓存!");
            if (cacheWatchThread != null)
            {
                cacheWatchThread.interrupt();
            }
            final CacheSchedule cacheSchedule = new CacheSchedule();
            // new NewsTest().cacheArsenal(cacheSchedule);
            new NewsTest().cacheTeams(cacheSchedule);
            new NewsTest().cacheFormus(cacheSchedule);
            cacheWatchThread = new Thread(new Runnable()
            {

                @Override
                public void run()
                {
                    while (!cachedExit)
                    {
                        List<String> list = cacheSchedule.getMap();
                        String process = "";

                        final boolean allCached = cacheSchedule.isAllCached();
                        int sum = 0;
                        for (String s : list)
                        {
                            process += s + "  \n";
                            sum += BasicNumberUtil.getNumber(PatternUtil
                                    .getFirstPattern(s, "\\d+"));
                        }
                        String head = "";
                        Message msg = new Message();
                        msg.what = MESSAGE_PROCESS_CODE;

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
            cacheWatchThread.setDaemon(true);
            cacheWatchThread.start();
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

    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit()
    {
        if (!isExit)
        {
            isExit = true;
            ToastUtil.showShortToast(this, "再按一次退出程序");
            // 利用handler延迟发送更改状态信息
            handler.sendEmptyMessageDelayed(MESSAGE_EXIT_CODE, 2000);
        }
        else
        {
            finish();
            System.exit(0);
        }
    }
}
