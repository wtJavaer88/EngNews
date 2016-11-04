package com.wnc.news.engnews.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.engnews.R;
import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicNumberUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.news.api.autocache.CacheSchedule;
import com.wnc.news.api.common.DirectLinkNewsFactory;
import com.wnc.news.api.common.ErrSiteNewsInfo;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.engnews.helper.NewsTest;
import com.wnc.news.engnews.kpi.KPIData;
import com.wnc.news.engnews.kpi.KPIService;
import com.wnc.news.engnews.kpi.SelectedWord;
import com.wnc.news.engnews.kpi.ViewedNews;
import com.wnc.news.richtext.HtmlRichText;
import com.wnc.string.PatternUtil;
import common.app.BasicPhoneUtil;
import common.app.ConfirmUtil;
import common.app.SysInit;
import common.app.ToastUtil;
import common.uihelper.PositiveEvent;
import common.utils.TimeUtil;

public class MainActivity extends BaseVerActivity implements OnClickListener,
        UncaughtExceptionHandler
{
    private Button bt_pick;
    private Button btn_cache1, btn_cache2, btn_cache_clear;
    private TextView proTv;

    private final int MESSAGE_EXIT_CODE = 0;
    private final int MESSAGE_PROCESS_CODE = 1;
    private final int MESSAGE_DIRECTLINK_CODE = 2;
    private final int MESSAGE_KPI_CODE = 3;
    EditText linkEt;
    Logger log = Logger.getLogger(MainActivity.class);

    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);
        Thread.setDefaultUncaughtExceptionHandler(this);
        log.info("App Start...");
        initView();

        SysInit.init(MainActivity.this);
        bgUpdate();
    }

    private void bgUpdate()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                KPIData findTodayData = KPIService.getInstance()
                        .findTodayData();
                if (findTodayData != null)
                {
                    log.info(findTodayData);
                    Message msg = new Message();
                    msg.what = MESSAGE_KPI_CODE;
                    msg.obj = findTodayData;
                    handler.sendMessage(msg);
                }
                if (BasicDateUtil.getCurrentHour() > 7
                        && BasicDateUtil.getCurrentHour() < 10)
                {
                    new NewsTest().topicUpdate();
                }
                // NewsDao.test();
                // new CETTopicUpdate().updateCounts();
                // new NewsTest().deleteEmptyDate();
                // NewsInfo newsFromUrl = new IyubaApi()
                // .getNewsFromUrl("http://voa.iyuba.com/audioitem_standard_803.html");
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
            case MESSAGE_DIRECTLINK_CODE:
                NewsContentActivity.news_info = (NewsInfo) msg.obj;
                startActivity(new Intent(getApplicationContext(),
                        NewsContentActivity.class));
                break;
            case MESSAGE_KPI_CODE:
                KPIData data = (KPIData) msg.obj;
                ((TextView) findViewById(R.id.tv_head_fav)).setText(data
                        .getLoved_news() + "");
                ((TextView) findViewById(R.id.tv_head_sel)).setText(data
                        .getSelectedWords() + "");
                ((TextView) findViewById(R.id.tv_head_his)).setText(data
                        .getViewed_news() + "");
                ((TextView) findViewById(R.id.tv_head_tim)).setText(TimeUtil
                        .timeToText(data.getTimes()));
                ((TextView) findViewById(R.id.tv_head_wor)).setText(data
                        .getTopicWords() + "");
                break;
            }
        }
    };
    Button bt_web_parse;

    private void initView()
    {
        linkEt = (EditText) findViewById(R.id.et_pick_url);
        proTv = (TextView) findViewById(R.id.tv_cache_process);
        proTv.setMovementMethod(ScrollingMovementMethod.getInstance());

        bt_pick = (Button) findViewById(R.id.btn_pick);
        bt_web_parse = (Button) findViewById(R.id.btn_web_parse);

        btn_cache1 = (Button) findViewById(R.id.btn_cache_team);
        btn_cache2 = (Button) findViewById(R.id.btn_cache_forums);

        btn_cache_clear = (Button) findViewById(R.id.btn_cache_clear);

        bt_pick.setOnClickListener(this);
        bt_web_parse.setOnClickListener(this);

        btn_cache1.setOnClickListener(this);
        btn_cache2.setOnClickListener(this);
        btn_cache_clear.setOnClickListener(this);
        findViewById(R.id.btn_theme).setOnClickListener(this);
        findViewById(R.id.btn_search).setOnClickListener(this);
        findViewById(R.id.imgbutton_head_sel).setOnClickListener(this);
        findViewById(R.id.imgbutton_head_his).setOnClickListener(this);
    }

    private void showSelected()
    {
        final Dialog dialog = new Dialog(this, R.style.CustomDialogStyle);
        dialog.setContentView(R.layout.topic_tip_ndailog);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (0.85 * BasicPhoneUtil.getScreenWidth(this));
        lp.height = (int) (0.85 * BasicPhoneUtil.getScreenHeight(this));

        final TextView tvTopic = (TextView) dialog
                .findViewById(R.id.tvTopicInfo);

        final Set<SelectedWord> findSelectedWordsToday = KPIService
                .getInstance().findSelectedWordsToday();
        Iterator<SelectedWord> iterator = findSelectedWordsToday.iterator();
        String tpContent = "";
        while (iterator.hasNext())
        {
            SelectedWord next = iterator.next();
            String mean = next.getCn_mean();
            if (mean == null)
            {
                mean = "";
            }
            tpContent += next.getWord() + "  " + mean.replace("\n", "\n    ")
                    + "\n\n";
        }
        if (tpContent.length() > 2)
        {
            tpContent = tpContent.substring(0, tpContent.length() - 2);
        }
        tvTopic.setText(tpContent);
        dialog.show();
    }

    private void showHistory()
    {
        final Dialog dialog = new Dialog(this, R.style.CustomDialogStyle);
        dialog.setContentView(R.layout.topic_tip_ndailog);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = (int) (0.85 * BasicPhoneUtil.getScreenWidth(this));
        lp.height = (int) (0.85 * BasicPhoneUtil.getScreenHeight(this));

        final TextView tvTopic = (TextView) dialog
                .findViewById(R.id.tvTopicInfo);

        Iterator<ViewedNews> iterator = KPIService.getInstance()
                .findHistoryToday().iterator();
        String tpContent = "";
        while (iterator.hasNext())
        {
            ViewedNews next = iterator.next();
            final String timeToText = TimeUtil.timeToText(next
                    .getView_duration());
            System.out.println(timeToText);
            tpContent += "<p>浏览:" + next.getView_time() + "  用时:" + timeToText
                    + "<br>";
            tpContent += "<font color=blue><a href=\"" + next.getUrl() + "\">"
                    + next.getTitle() + "</a></font></p>";
        }
        if (tpContent.length() > 2)
        {
            tpContent = tpContent.substring(0,
                    tpContent.length() - "</br>".length() * 2);
        }
        tvTopic.setMovementMethod(LinkMovementMethod.getInstance());
        tvTopic.setText(new HtmlRichText(tpContent).getCharSequence());
        dialog.show();
    }

    Thread cacheWatchThread1;
    Thread cacheWatchThread2;
    public volatile boolean cachedExit = false;

    @Override
    public void onClick(View v)
    {
        final CacheSchedule cacheSchedule = new CacheSchedule();
        switch (v.getId())
        {
        case R.id.imgbutton_head_sel:
            showSelected();
            break;
        case R.id.imgbutton_head_his:
            showHistory();
            break;
        case R.id.btn_theme:
            startActivity(new Intent(this, TabsActivity.class));
            break;
        case R.id.btn_search:
            startActivity(new Intent(this, SearchActivity.class));
            break;
        case R.id.btn_pick:
            new CacheSchedule().voaCache();
            startActivity(new Intent(this, VoaActivity.class));
            break;
        case R.id.btn_web_parse:
            new Thread(new Runnable()
            {

                @Override
                public void run()
                {
                    System.out.println("begin...");
                    String url = "http://forums.realgm.com/boards/viewtopic.php?f=6&t=1486328";
                    url = "https://www.reddit.com/r/NBASpurs/comments/59fpi7/careerhighkawhi/";
                    url = linkEt.getText().toString();
                    if (BasicStringUtil.isNullString(url))
                    {
                        return;
                    }
                    final NewsInfo newsFromUrl = DirectLinkNewsFactory
                            .getNewsFromUrl(url);
                    if (newsFromUrl instanceof ErrSiteNewsInfo)
                    {
                        ToastUtil.showShortToast(getApplicationContext(),
                                "不支持该网站!");
                        log.error("不支持该网站!");
                    }
                    else if (newsFromUrl != null
                            && newsFromUrl.getHtml_content() != null
                            && newsFromUrl.getHtml_content().length() > 200)
                    {
                        Message msg = new Message();
                        msg.what = MESSAGE_DIRECTLINK_CODE;
                        msg.obj = newsFromUrl;
                        handler.sendMessage(msg);
                    }
                    else
                    {
                        ToastUtil.showShortToast(getApplicationContext(), url
                                + "解析有误!");

                        log.error(url + "解析有误!");
                    }
                }
            }).start();
            break;
        case R.id.btn_cache_team:
            System.out.println("开始缓存!");
            if (cacheWatchThread1 != null)
            {
                cacheWatchThread1.interrupt();
            }
            // new NewsTest().cacheArsenal(cacheSchedule);
            new NewsTest().cacheTeams(cacheSchedule);
            cacheWatchThread1 = new Thread(new Runnable()
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
            cacheWatchThread1.setDaemon(true);
            cacheWatchThread1.start();
            break;
        case R.id.btn_cache_forums:
            System.out.println("开始缓存!");
            if (cacheWatchThread2 != null)
            {
                cacheWatchThread2.interrupt();
            }
            new NewsTest().cacheFormus(cacheSchedule);
            cacheWatchThread2 = new Thread(new Runnable()
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
            cacheWatchThread2.setDaemon(true);
            cacheWatchThread2.start();
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

    @Override
    protected void onDestroy()
    {
        KPIService.getInstance().closeDb();
        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        bgUpdate();
    }
}
