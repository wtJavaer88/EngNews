package com.wnc.news.engnews.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import net.selectabletv.SelectableTextView;
import net.selectabletv.SelectableTextView.OnCursorStateChangedListener;

import org.apache.log4j.Logger;

import word.DicWord;
import word.Topic;
import word.WordExpand;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.engnews.R;
import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicNumberUtil;
import com.wnc.basic.BasicRunTimeUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.news.api.autocache.CETTopicCache;
import com.wnc.news.api.autocache.PassedTopicCache;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.dao.DictionaryDao;
import com.wnc.news.engnews.helper.ActivityMgr;
import com.wnc.news.engnews.helper.ActivityTimeUtil;
import com.wnc.news.engnews.helper.AppConfig;
import com.wnc.news.engnews.helper.NewsContentUtil;
import com.wnc.news.engnews.helper.OptedDictData;
import com.wnc.news.engnews.helper.SrtVoiceHelper;
import com.wnc.news.engnews.helper.ViewNewsHolder;
import com.wnc.news.engnews.helper.WordTipTextThread;
import com.wnc.news.engnews.kpi.KPIService;
import com.wnc.news.engnews.kpi.SelectedWord;
import com.wnc.news.engnews.network.CibaCacheHelper;
import com.wnc.news.engnews.network.WebUrlHelper;
import com.wnc.news.engnews.ui.popup.NewsMenuPopWindow;
import com.wnc.news.engnews.ui.popup.NewsMenuPopWindow.NewsMenuListener;
import com.wnc.news.engnews.ui.popup.SectionPopWindow;
import com.wnc.news.engnews.ui.popup.SectionPopWindow.WordSectionListener;
import com.wnc.news.engnews.ui.popup.WordMenuPopWindow;
import com.wnc.news.engnews.ui.popup.WordMenuPopWindow.WordMenuListener;
import com.wnc.news.richtext.ClickableMovementMethod;
import com.wnc.news.richtext.HtmlRichText;
import com.wnc.string.PatternUtil;
import common.app.BasicPhoneUtil;
import common.app.ClipBoardUtil;
import common.app.MessagePopWindow;
import common.app.ToastUtil;
import common.uihelper.MyAppParams;
import common.uihelper.gesture.CtrlableHorGestureDetectorListener;
import common.uihelper.gesture.FlingPoint;
import common.uihelper.gesture.MyCtrlableGestureDetector;
import common.utils.UrlPicDownloader;

public abstract class BaseNewsActivity extends BaseVerActivity implements
        CtrlableHorGestureDetectorListener, UncaughtExceptionHandler,
        OnClickListener
{
    // 最短承认的浏览时间
    private static final int VIEW_ADMIT_60 = 60;
    public static NewsInfo news_info;
    static Logger log = Logger.getLogger(BaseNewsActivity.class);
    final int REQUEST_SEARCH_CODE = 1;
    public final static int MESSAGE_ON_WORD_DISPOSS_CODE = 100;
    public final static int MESSAGE_ON_CONTEXT_TEXT = 1;
    public final static int MESSAGE_ON_IMG_TEXT = 2;
    public final static int MESSAGE_ON_RUNTIME_TEXT = 3;
    public final static int MESSAGE_ON_WORDMEAN_TEXT = 4;
    private static final int MESSAGE_ON_DOWNSOUND_ERROR_TEXT = 201;
    private static final int MESSAGE_ON_DOWNSOUND_SUCCESS_TEXT = 202;
    private static final int MESSAGE_ON_NETMEAN_ERROR = 203;
    View main;
    public GestureDetector gestureDetector;
    /**
     * 多余多层次的搜索,使用栈来管理数据
     */
    Stack<List<NewsInfo>> newsStack = new Stack<List<NewsInfo>>();

    TextView newsImgTv;
    protected List<Topic> allFind = new ArrayList<Topic>();

    public Button topicListBt, wordMenuBtn;
    ImageButton newsMenuBtn;
    TextView wordTipTv;

    public SelectableTextView mTextView;
    public ScrollView mScrollView;

    public int mTouchX;
    public int mTouchY;

    WordTipTextThread wordTipTextThread;
    WordMenuPopWindow wordMenuPopWindow;
    SectionPopWindow sectionPopWindow;
    NewsMenuPopWindow newsMenuPopWindow;
    MessagePopWindow messagePopWindow;

    public int totalWords;

    ActivityTimeUtil activityTimeUtil = new ActivityTimeUtil();
    public volatile boolean runtimeWatch = true;
    KPIService kPIHelper = KPIService.getInstance();
    int mTouchX2;
    int mTouchY2;
    int topicBtClickCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        kPIHelper.refreshCurDay();
        adaptNight();

        getLayoutInflater();
        main = LayoutInflater.from(this).inflate(
                R.layout.activity_news_content, null);
        hideVirtualBts();
        setContentView(main);
        initView();

        this.gestureDetector = new GestureDetector(this,
                new MyCtrlableGestureDetector(this, 0.5, 0, this, null));

        Thread.setDefaultUncaughtExceptionHandler(this);
        wordTipTextThread = new WordTipTextThread(this);
        wordTipTextThread.start();

    }

    private void adaptNight()
    {
        if (AppConfig.isNightModel())
        {
            this.setTheme(R.style.NightTheme);
        }
        else
        {
            this.setTheme(R.style.DayTheme);
        }
    }

    /**
     * 隐藏虚拟按键
     */
    @SuppressLint("NewApi")
    protected void hideVirtualBts()
    {
        // 普通
        final int currentAPIVersion = BasicPhoneUtil
                .getCurrentAPIVersion(getApplicationContext());
        // System.out.println("Level ........" + currentAPIVersion);
        if (currentAPIVersion < 19)
        {
            main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        else
        {
            // 保留任务栏
            main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    protected final void timeCountBegin()
    {
        activityTimeUtil.begin();
        Thread t = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                while (runtimeWatch)
                {
                    handler.sendEmptyMessage(MESSAGE_ON_RUNTIME_TEXT);
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    @SuppressLint("HandlerLeak")
    protected Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
            case (MESSAGE_ON_WORDMEAN_TEXT):
                final String mean = msg.obj.toString();
                wordTipTv.append(" " + mean);
                DictionaryDao.insertNewWord(getCurrentWord(), mean);
                messagePopWindow.dismiss();
                break;
            case MESSAGE_ON_IMG_TEXT:
                newsImgTv.setVisibility(View.VISIBLE);
                newsImgTv.setText((CharSequence) msg.obj);
                break;
            case MESSAGE_ON_DOWNSOUND_ERROR_TEXT:
                messagePopWindow.setMsgAndShow("下载音频异常!", mTextView);
                break;
            case MESSAGE_ON_DOWNSOUND_SUCCESS_TEXT:
                messagePopWindow.setMsgAndShow("下载音频成功!", mTextView);
                break;
            case MESSAGE_ON_NETMEAN_ERROR:
                messagePopWindow.setMsgAndShow("网络查找单词失败!", mTextView);
                break;

            case MESSAGE_ON_RUNTIME_TEXT:
                ((TextView) findViewById(R.id.bt_activity_runtime))
                        .setText(activityTimeUtil.getTranedRunTime());
                break;
            }
            dispatchMsg(msg);
        }

    };

    protected abstract void dispatchMsg(Message msg);

    @Override
    public void doLeft(FlingPoint p1, FlingPoint p2)
    {
        changeNews(ViewNewsHolder.getPre());
    }

    private void changeNews(NewsInfo info)
    {
        if (info != null && news_info.getUrl() != info.getUrl())
        {
            recordViewed();
            activityTimeUtil.restart();
            this.newsImgTv.setText("");
            news_info = info;
            setNewsTitle(news_info.getTitle());
            initData();
        }
    }

    protected abstract void initData();

    @Override
    public void doRight(FlingPoint p1, FlingPoint p2)
    {
        changeNews(ViewNewsHolder.getNext());
    }

    @Override
    public void uncaughtException(Thread arg0, Throwable ex)
    {
        log.error("uncaughtException", ex);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.bt_topics:
            showTopicList();
            break;
        case R.id.btn_word_menu:
            wordTipTextThread.stopListen();
            wordMenuPopWindow.showPopupWindow(wordMenuBtn);
            break;
        case R.id.imgbt_news_menu:
            System.out.println("显示新闻菜单!");
            newsMenuPopWindow.showPopupWindow(newsMenuBtn);
            break;
        default:
            break;
        }
    }

    @Override
    protected void onDestroy()
    {
        try
        {
            recordViewed();
            runtimeWatch = false;
            sectionPopWindow.dismiss();
            wordMenuPopWindow.dismiss();
            newsMenuPopWindow.dismiss();
            messagePopWindow.dismiss();
            this.activityTimeUtil.stop();
            hideCursor();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    protected void recordViewed()
    {
        // activityTimeUtil.stop();
        System.out.println("recordViewed Runtime:"
                + activityTimeUtil.getTranedRunTime());
        if (activityTimeUtil.getRunTime() > VIEW_ADMIT_60 * 1000)
        {
            ActivityMgr.viewedNewsRecord(this, news_info,
                    activityTimeUtil.getRunTime());
            kPIHelper.increaseViewed(activityTimeUtil.getRunTime(),
                    allFind.size(), news_info);
        }
    }

    protected void initView()
    {
        messagePopWindow = new MessagePopWindow(this);
        newsMenuPopWindow = new NewsMenuPopWindow(this, new NewsMenuListener()
        {
            @Override
            public void toSrcPage()
            {
                if (news_info != null)
                {
                    ActivityMgr.gotoIE(BaseNewsActivity.this,
                            news_info.getUrl());
                }
            }

            @Override
            public void doFavorite()
            {
                kPIHelper.increaseLoved(news_info.getDb_id());
                ActivityMgr.loveNewsRecord(BaseNewsActivity.this, news_info);
            }

            @Override
            public void setting()
            {
                newsMenuSetting();
            }
        });
        wordMenuPopWindow = new WordMenuPopWindow(this, new WordMenuListener()
        {
            @Override
            public void doSound()
            {

                final String voicePath = MyAppParams.VOICE_FOLDER
                        + getCurrentWord() + ".mp3";
                if (BasicFileUtil.isExistFile(voicePath))
                {
                    SrtVoiceHelper.play(voicePath);
                }
                else
                {
                    // common.app.ToastUtil.showShortToast(
                    // getApplicationContext(), "找不到声音文件!");
                    messagePopWindow
                            .setMsgAndShow("找不到声音文件,正在为你下载!", mTextView);

                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                Thread.sleep(1000);
                                final String soundUrl = CibaCacheHelper
                                        .getCibaTranslate(getCurrentWord())
                                        .getSoundStr();
                                UrlPicDownloader.download(soundUrl, voicePath);
                            }
                            catch (Exception e)
                            {
                                handler.sendEmptyMessage(MESSAGE_ON_DOWNSOUND_ERROR_TEXT);
                                e.printStackTrace();
                            }
                            if (BasicFileUtil.getFileSize(voicePath) == 0)
                            {
                                BasicFileUtil.deleteFile(voicePath);
                            }
                            else
                            {
                                handler.sendEmptyMessage(MESSAGE_ON_DOWNSOUND_SUCCESS_TEXT);
                            }
                        }
                    }).start();

                }
            }

            @Override
            public void doCopy()
            {
                common.app.ClipBoardUtil
                        .setNormalContent(getApplicationContext(), wordTipTv
                                .getText().toString());
                common.app.ToastUtil.showShortToast(getApplicationContext(),
                        "操作成功!");
            }

            @Override
            public void toNet()
            {
                ActivityMgr.gotoIE(BaseNewsActivity.this,
                        WebUrlHelper.getWordUrl(getCurrentWord()));
            }

            @Override
            public void doPassTopic()
            {
                BasicFileUtil.writeFileString(MyAppParams.PASS_TXT,
                        getCurrentWord() + "\r\n", "UTF-8", true);
                PassedTopicCache.getPassedTopics().add(getCurrentWord());
                common.app.ToastUtil.showShortToast(getApplicationContext(),
                        "操作成功!");
                log.info("Pass: " + getCurrentWord());
            }

            @Override
            public void doExpand()
            {
                final Dialog dialog = new Dialog(BaseNewsActivity.this,
                        R.style.CustomDialogStyle);
                dialog.setContentView(R.layout.topic_tip_wdailog);
                dialog.setCanceledOnTouchOutside(true);
                Window window = dialog.getWindow();

                WindowManager.LayoutParams lp = window.getAttributes();
                lp.width = (int) (0.85 * BasicPhoneUtil
                        .getScreenWidth(BaseNewsActivity.this));
                lp.height = (int) (0.85 * BasicPhoneUtil
                        .getScreenHeight(BaseNewsActivity.this));
                lp.verticalMargin = 20;

                final SelectableTextView tvTopic = (SelectableTextView) dialog
                        .findViewById(R.id.tvTopicInfo);

                tvTopic.setMovementMethod(LinkMovementMethod.getInstance());
                tvTopic.setOnTouchListener(new View.OnTouchListener()
                {
                    @Override
                    public boolean onTouch(View v, MotionEvent event)
                    {
                        mTouchX2 = (int) event.getX();
                        mTouchY2 = (int) event.getY();
                        return false;
                    }
                });
                tvTopic.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        tvTopic.setMovementMethod(ScrollingMovementMethod
                                .getInstance());

                        int start = tvTopic
                                .getPreciseOffset(mTouchX2, mTouchY2);

                        if (start > -1)
                        {
                            newsStack.push(ViewNewsHolder.getCurList());
                            String selectedText = NewsContentUtil
                                    .getSuitWordAndSetPos(tvTopic, start);
                            log.info("selectedText:" + selectedText);
                            ClipBoardUtil.setNormalContent(
                                    getApplicationContext(), selectedText);
                            startActivityForResult(new Intent(
                                    getApplicationContext(),
                                    SearchActivity.class).putExtra("keyword",
                                    selectedText), REQUEST_SEARCH_CODE);
                        }
                        return true;
                    }
                });
                tvTopic.setOnClickListener(new OnClickListener()
                {

                    @Override
                    public void onClick(View arg0)
                    {
                        tvTopic.setMovementMethod(LinkMovementMethod
                                .getInstance());
                    }
                });

                if (OptedDictData.getSeekWordList().size() > 0)
                {
                    final Integer topic_id = OptedDictData.getSeekWordList()
                            .peek().getTopic_id();
                    if (OptedDictData.getWordExpandContentMap().containsKey(
                            topic_id))
                    {
                        tvTopic.setText(OptedDictData.getWordExpandContentMap()
                                .get(topic_id));
                        dialog.show();
                    }
                    else
                    {
                        WordExpand wordExpand = DictionaryDao
                                .findSameAntonym(OptedDictData
                                        .getSeekWordList().peek().getTopic_id());

                        if (wordExpand != null)
                        {
                            try
                            {
                                String article = "<p>"
                                        + wordExpand.toString().replace("\n",
                                                "<br>") + "</p>";
                                String splitArticle = new CETTopicCache()
                                        .splitArticle(article,
                                                new ArrayList<Topic>());
                                final CharSequence charSequence = new HtmlRichText(
                                        splitArticle).getCharSequence();
                                OptedDictData.addWordExpand(topic_id,
                                        charSequence);
                                tvTopic.setText(charSequence);
                                dialog.show();
                            }
                            catch (Exception e)
                            {
                                log.error("wordExpand", e);
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            ToastUtil.showShortToast(getApplicationContext(),
                                    "找不到扩展的内容!");
                        }
                    }
                }

            }
        });
        sectionPopWindow = new SectionPopWindow(this, new WordSectionListener()
        {

            @Override
            public void doTranslate()
            {
                ActivityMgr.gotoIE(BaseNewsActivity.this, WebUrlHelper
                        .getTranslateUrl(getCurrentSectionAndSetPos()));
                log.info("翻译: " + getCurrentSectionAndSetPos());
            }

            @Override
            public void doFavorite()
            {
                BasicFileUtil.writeFileString(MyAppParams.FAVORITE_TXT,
                        getCurrentSectionAndSetPos() + "\r\n", "UTF-8", true);
                common.app.ToastUtil.showShortToast(getApplicationContext(),
                        "操作成功!");
                log.info("收藏成功: " + getCurrentSectionAndSetPos());
            }

            @Override
            public void doCopy()
            {
                common.app.ClipBoardUtil.setNormalContent(
                        getApplicationContext(), getCurrentSectionAndSetPos());
                common.app.ToastUtil.showShortToast(getApplicationContext(),
                        "操作成功!");
            }
        });
        newsMenuBtn = (ImageButton) findViewById(R.id.imgbt_news_menu);
        wordMenuBtn = (Button) findViewById(R.id.btn_word_menu);
        topicListBt = (Button) findViewById(R.id.bt_topics);
        newsImgTv = (TextView) findViewById(R.id.tv_img);
        newsImgTv.setMovementMethod(LinkMovementMethod.getInstance());

        wordMenuBtn.setVisibility(View.INVISIBLE);

        newsMenuBtn.setOnClickListener(this);
        wordMenuBtn.setOnClickListener(this);
        topicListBt.setOnClickListener(this);
        topicListBt.setVisibility(View.INVISIBLE);
        wordTipTv = (TextView) findViewById(R.id.tv_oneword_tip);

        mTextView = (SelectableTextView) findViewById(R.id.tv_content);
        mTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mTextView.setDefaultSelectionColor(0x40FF00FF);
        // 事件调用顺序OnTouch --> OnLongClick --> OnClick
        mTextView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                mTouchX = (int) event.getX();
                mTouchY = (int) event.getY();
                return false;
            }
        });
        mTextView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                enableTextViewLink(false);
                hideWordZone();
                hideCursor();
                showSelectionCursors(mTouchX, mTouchY);
                return true;
            }
        });

        mTextView.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
                enableTextViewLink(true);
                wordTipTextThread.refresh();
                hideWordZone();
                hideCursor();
            }
        });
        mTextView
                .setOnCursorStateChangedListener(new OnCursorStateChangedListener()
                {

                    @Override
                    public void onShowCursors(View v)
                    {
                    }

                    @Override
                    public void onPositionChanged(View v, int x, int y,
                            int oldx, int oldy)
                    {
                    }

                    @Override
                    public void onHideCursors(View v)
                    {
                    }

                    @Override
                    public void onDragStarts(View v)
                    {

                    }

                    @Override
                    public void onDragStop(View v)
                    {
                        String selectedText = mTextView.getCursorSelection()
                                .getSelectedText().toString();
                        if (selectedText.contains(" "))
                        {
                            hideWordZone();
                            getCurrentSectionAndSetPos();

                            final int h = common.app.BasicPhoneUtil
                                    .getScreenHeight(BaseNewsActivity.this);

                            // System.out.println(h + "  "
                            // + mTextView.getCursorPoint()[0] + ", "
                            // + mTextView.getCursorPoint()[1]);
                            if (mTextView.getCursorPoint()[1] < h * 0.8)
                            {
                                sectionPopWindow.showAtLocation(mTextView,
                                        Gravity.NO_GRAVITY,
                                        mTextView.getCursorPoint()[0] + 100,
                                        mTextView.getCursorPoint()[1]);
                            }
                            else
                            {
                                sectionPopWindow.showAtLocation(mTextView,
                                        Gravity.CENTER, 0, 0);
                            }
                        }
                        else
                        {
                            showWordZone(selectedText);
                        }
                    }
                });
        mScrollView = (ScrollView) findViewById(R.id.scrollView_news_content);
        // mScrollView.setOnTouchListener(new TouchScrollListenerImpl());
    }

    protected void newsMenuSetting()
    {

    }

    protected void hideCursor()
    {
        try
        {
            mTextView.hideCursor();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
    {
        boolean flag = true;
        final boolean onTouchEvent = this.gestureDetector
                .onTouchEvent(paramMotionEvent);
        if (!onTouchEvent)
        {
            flag = super.dispatchTouchEvent(paramMotionEvent);
        }

        return flag;
    }

    @Override
    protected void onPause()
    {
        activityTimeUtil.pause();
        System.out.println("onPause Runtime:"
                + activityTimeUtil.getTranedRunTime());

        super.onPause();
    };

    @Override
    protected void onResume()
    {
        activityTimeUtil.resume();
        super.onPause();
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("welcome back..." + requestCode);

        if (requestCode == REQUEST_SEARCH_CODE && newsStack.size() > 0)
        {
            // System.out.println("newsStack.size():" + newsStack.size());
            ViewNewsHolder.refrehList(newsStack.pop());
            ViewNewsHolder.refreh(news_info);
        }
    }

    protected void setNewsTitle(String title)
    {
        ((TextView) findViewById(R.id.tv_news_title)).setText(title);
    }

    public Handler getHandler()
    {
        return handler;
    }

    protected boolean hasTopics()
    {
        return allFind != null && allFind.size() > 0;
    }

    protected void showTopicCounts()
    {
        if (allFind.size() > 0)
        {
            topicListBt.setVisibility(View.VISIBLE);
            topicListBt.setText("" + allFind.size());
        }
    }

    @SuppressLint("NewApi")
    protected void showTopicList()
    {
        topicBtClickCount++;
        if (hasTopics())
        {
            final Dialog dialog = new Dialog(this, R.style.CustomDialogStyle);
            dialog.setContentView(R.layout.topic_tip_wdailog);
            dialog.setCanceledOnTouchOutside(true);
            Window window = dialog.getWindow();

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = (int) (0.85 * BasicPhoneUtil.getScreenWidth(this));
            lp.height = (int) (0.85 * BasicPhoneUtil.getScreenHeight(this));

            final SelectableTextView tvTopic = (SelectableTextView) dialog
                    .findViewById(R.id.tvTopicInfo);

            tvTopic.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    mTouchX2 = (int) event.getX();
                    mTouchY2 = (int) event.getY();
                    return false;
                }
            });
            tvTopic.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    seekWordAndScroll(tvTopic, mTouchX2, mTouchY2);
                    dialog.dismiss();
                    return true;
                }
            });

            List<Topic> allFind2 = new ArrayList<Topic>(allFind);
            if (topicBtClickCount % 2 == 1)
            {
                allFind2 = allFind;
            }
            else
            {
                Collections.sort(allFind2, new Comparator<Topic>()
                {

                    @Override
                    public int compare(Topic arg0, Topic arg1)
                    {
                        return arg0.getMatched_word().compareToIgnoreCase(
                                arg1.getMatched_word());
                    }
                });
            }
            Iterator<Topic> iterator = allFind2.iterator();
            String tpContent = "总词数/生词率: "
                    + totalWords
                    + "/"
                    + BasicNumberUtil.convertScienceNum(100.0 * allFind2.size()
                            / totalWords, 1) + "%\n";
            while (iterator.hasNext())
            {
                Topic next = iterator.next();
                tpContent += next.getMatched_word() + "  "
                        + next.getMean_cn().replace("\n", "\n    ") + "\n\n";
            }
            if (tpContent.length() > 2)
            {
                tpContent = tpContent.substring(0, tpContent.length() - 2);
            }
            tvTopic.setText(tpContent);
            dialog.show();
        }
    }

    private void seekWordAndScroll(SelectableTextView tvTopic, int x, int y)
    {
        int start = tvTopic.getPreciseOffset(x, y);

        if (start > -1)
        {
            String selectedText = NewsContentUtil.getSuitWordAndSetPos(tvTopic,
                    start);
            log.info("selectedText:" + selectedText);
            {
                Layout layout = mTextView.getLayout();
                final String string = mTextView.getText().toString();
                int i = string.indexOf(selectedText);
                if (i == -1)
                {
                    return;
                }
                for (int line = 0; line < layout.getLineCount(); line++)
                {
                    final int lineEnd = layout.getLineEnd(line);
                    if (lineEnd > i)
                    {
                        mScrollView.scrollTo(0, layout.getLineTop(line));
                        break;
                    }
                }
            }
        }
    }

    protected void enableTextViewLink(boolean flag)
    {
        if (!flag)
        {
            mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        }
        else
        {
            mTextView.setMovementMethod(ClickableMovementMethod.getInstance());
        }
    }

    protected void showSelectionCursors(int x, int y)
    {
        int start = mTextView.getPreciseOffset(x, y);

        if (start > -1)
        {
            String selectedText = NewsContentUtil.getSuitWordAndSetPos(
                    mTextView, start);
            log.info("selectedText:" + selectedText);
            ActivityMgr.selectedWordRecord(this, news_info, selectedText);
            wordTipTextThread.refresh();
            showWordZone(selectedText);
        }
    }

    protected void hideWordZone()
    {
        wordMenuBtn.setVisibility(View.INVISIBLE);
        wordTipTv.setVisibility(View.INVISIBLE);
        switchWordTipColor(false);
    }

    protected void showWordZone(final String selectedText)
    {
        if (BasicStringUtil.isNullString(selectedText))
        {
            return;
        }
        DicWord findWord = null;
        BasicRunTimeUtil util = new BasicRunTimeUtil("");
        util.beginRun();
        for (DicWord d : OptedDictData.getSeekWordList())
        {
            if (selectedText.equalsIgnoreCase(d.getBase_word())
                    || selectedText.equalsIgnoreCase(d.getWord_done())
                    || selectedText.equalsIgnoreCase(d.getWord_er())
                    || selectedText.equalsIgnoreCase(d.getWord_est())
                    || selectedText.equalsIgnoreCase(d.getWord_ing())
                    || selectedText.equalsIgnoreCase(d.getWord_past())
                    || selectedText.equalsIgnoreCase(d.getWord_pl())
                    || selectedText.equalsIgnoreCase(d.getWord_third()))
            {
                findWord = d;
                util.finishRun();
                System.out.println("查缓存的时间:" + util.getRunMilliSecond());
                break;
            }
        }
        if (findWord == null)
        {
            findWord = DictionaryDao.findWord(selectedText);
            util.finishRun();
            System.out.println("查字典的时间:" + util.getRunMilliSecond());
        }

        if (findWord != null)
        {
            increaseSlectedCounts(selectedText, findWord.getTopic_id());
            wordTipTv.setText(findWord.getBase_word() + " "
                    + findWord.getCn_mean());
            OptedDictData.getSeekWordList().push(findWord);
            if (DictionaryDao.findSameAntonym(findWord.getTopic_id()) != null)
            {
                wordMenuPopWindow.openExpand();
            }
            else
            {
                wordMenuPopWindow.closeExpand();
            }
        }
        else
        {
            increaseSlectedCounts(selectedText, -1);
            wordTipTv.setText(selectedText);
            messagePopWindow.setMsgAndShow("字典中没有, 将去网络查找!", mTextView);
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        String basicInfo = CibaCacheHelper.getCibaTranslate(
                                getCurrentWord()).getBasicInfo();
                        if (BasicStringUtil.isNotNullString(basicInfo))
                        {
                            log.info("获取释义成功!" + basicInfo);
                            Message msg = new Message();
                            msg.what = MESSAGE_ON_WORDMEAN_TEXT;
                            msg.obj = basicInfo;
                            handler.sendMessage(msg);
                        }
                    }
                    catch (Exception e)
                    {
                        handler.sendEmptyMessage(MESSAGE_ON_NETMEAN_ERROR);
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        wordTipTextThread.stopListen();
        wordMenuPopWindow.showPopupWindow(wordMenuBtn);

        wordTipTv.setVisibility(View.VISIBLE);
        wordMenuBtn.setVisibility(View.VISIBLE);

    }

    protected String getCurrentWord()
    {
        return PatternUtil.getFirstPatternGroup(wordTipTv.getText().toString(),
                "\\w+");
    }

    protected String getCurrentSectionAndSetPos()
    {
        return NewsContentUtil.getSectionAndSetPos(mTextView, mTextView
                .getCursorSelection().getStart(), mTextView
                .getCursorSelection().getEnd());
    }

    public void increaseSlectedCounts(String selectedText, int topic_id)
    {
        if (kPIHelper.getLatelyWords().contains(new SelectedWord(selectedText)))
        {
            switchWordTipColor(true);
        }
        kPIHelper.addToLatelyWords(selectedText, topic_id);
        kPIHelper.addSelectedWord(news_info.getDb_id(), selectedText, topic_id);
        kPIHelper.updateKPISelected(1);
    }

    private void switchWordTipColor(boolean b)
    {
        if (b)
        {
            wordTipTv.setTextColor(0xffff0000);
        }
        else
        {
            wordTipTv.setTextColor(0xff000000);
        }

    }
}
