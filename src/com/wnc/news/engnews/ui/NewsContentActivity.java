package com.wnc.news.engnews.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import net.selectabletv.SelectableTextView;
import net.selectabletv.SelectableTextView.OnCursorStateChangedListener;

import org.apache.log4j.Logger;
import org.jsoup.select.Elements;

import word.DicWord;
import word.Topic;
import word.WordExpand;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.engnews.R;
import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicNumberUtil;
import com.wnc.basic.BasicRunTimeUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.news.api.autocache.CETTopicCache;
import com.wnc.news.api.autocache.PassedTopicCache;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.dao.DictionaryDao;
import com.wnc.news.dao.NewsDao;
import com.wnc.news.db.DatabaseManager;
import com.wnc.news.engnews.helper.NewsContentUtil;
import com.wnc.news.engnews.helper.SrtVoiceHelper;
import com.wnc.news.engnews.helper.ViewNewsHolder;
import com.wnc.news.engnews.helper.WebUrlHelper;
import com.wnc.news.engnews.helper.WordTipTextThread;
import com.wnc.news.engnews.ui.popup.NewsMenuPopWindow;
import com.wnc.news.engnews.ui.popup.NewsMenuPopWindow.NewsMenuListener;
import com.wnc.news.engnews.ui.popup.SectionPopWindow;
import com.wnc.news.engnews.ui.popup.SectionPopWindow.WordSectionListener;
import com.wnc.news.engnews.ui.popup.WordMenuPopWindow;
import com.wnc.news.engnews.ui.popup.WordMenuPopWindow.WordMenuListener;
import com.wnc.news.richtext.HtmlRichText;
import com.wnc.news.richtext.WebImgText;
import com.wnc.string.PatternUtil;
import common.app.BasicPhoneUtil;
import common.app.ClipBoardUtil;
import common.app.ToastUtil;
import common.uihelper.MyAppParams;
import common.uihelper.gesture.CtrlableHorGestureDetectorListener;
import common.uihelper.gesture.FlingPoint;
import common.uihelper.gesture.MyCtrlableGestureDetector;

public class NewsContentActivity extends Activity implements
        CtrlableHorGestureDetectorListener, UncaughtExceptionHandler,
        OnClickListener
{
    final int REQUEST_SEARCH_CODE = 1;
    static Logger log = Logger.getLogger(NewsContentActivity.class);
    View main;
    private GestureDetector gestureDetector;

    public static final int MESSAGE_ON_WORD_DISPOSS_CODE = 100;
    public static final int MESSAGE_ON_IMG_TEXT = 2;
    TextView newsImgTv;
    List<Topic> allFind = new ArrayList<Topic>();

    public static NewsInfo news_info;
    private Button topicListBt, wordMenuBtn;
    ImageButton newsMenuBtn;
    TextView wordTipTv;

    private SelectableTextView mTextView;
    private ScrollView mScrollView;

    private int mTouchX;
    private int mTouchY;

    WordTipTextThread wordTipTextThread;
    WordMenuPopWindow wordMenuPopWindow;
    SectionPopWindow sectionPopWindow;
    NewsMenuPopWindow newsMenuPopWindow;

    private int totalWords;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        main = getLayoutInflater().from(this).inflate(
                R.layout.activity_news_content, null);
        hideVirtualBts();
        setContentView(main);

        this.gestureDetector = new GestureDetector(this,
                new MyCtrlableGestureDetector(this, 0.25, 0, this, null));
        Thread.setDefaultUncaughtExceptionHandler(this);

        wordTipTextThread = new WordTipTextThread(this);
        wordTipTextThread.start();

        initView();
        if (news_info != null)
        {
            setNewsTitle(news_info.getTitle());
            initData();
        }

    }

    Thread dataThread1;
    Thread dataThread2;

    private void initData()
    {
        hideCursor();
        hideWordZone();
        if (dataThread1 != null)
        {
            dataThread1.interrupt();
        }
        if (dataThread2 != null)
        {
            dataThread2.interrupt();
        }
        dataThread1 = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Message msg2 = new Message();
                msg2.what = MESSAGE_ON_IMG_TEXT;
                msg2.obj = new WebImgText("<img src=\""
                        + news_info.getHead_pic() + "\"/>").getCharSequence();
                if (Thread.currentThread().isInterrupted())
                {
                    System.out.println("should stop thread1 no send msg");
                    return;
                }
                handler.sendMessage(msg2);
            }
        });
        dataThread1.setDaemon(true);
        dataThread1.start();

        dataThread2 = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Elements contents = null;
                    Message msg = new Message();
                    if (news_info != null
                            && news_info.getHtml_content() != null)
                    {
                        totalWords = PatternUtil.getAllPatternGroup(
                                news_info.getHtml_content(), "['\\w]+").size();
                        log.info("该缓存新闻单词数:" + totalWords);
                        String str = news_info.getCet_topics();
                        if (BasicStringUtil.isNotNullString(str))
                        {
                            allFind = JSONObject.parseArray(JSONObject
                                    .parseObject(str).getString("data"),
                                    Topic.class);
                            log.info("该缓存新闻关键词数:" + allFind.size());
                        }
                        else
                        {
                            log.error(news_info.getUrl() + " 没找到任何Topic,将重新查找!");
                            final String splitArticle = new CETTopicCache()
                                    .splitArticle(news_info.getHtml_content(),
                                            allFind);
                            if (allFind.size() > 0)
                            {
                                news_info.setHtml_content(splitArticle);
                                JSONObject jobj = new JSONObject();
                                jobj.put("data", allFind);
                                news_info.setCet_topics(jobj.toString());
                                news_info.setTopic_counts(allFind.size());
                                SQLiteDatabase db = DatabaseManager
                                        .getInstance().openDatabase();
                                if (!NewsDao.isExistUrl(db, news_info.getUrl()))
                                {
                                    NewsDao.insertSingleNews(db, news_info);
                                }
                                DatabaseManager.getInstance().closeDatabase();
                            }
                        }

                        msg.obj = news_info.getHtml_content();
                        msg.what = 1;
                    }
                    if (Thread.currentThread().isInterrupted())
                    {
                        return;
                    }
                    handler.sendMessage(msg);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        dataThread2.setDaemon(true);
        dataThread2.start();
    }

    Handler handler = new Handler()
    {

        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
            case 1:
                mTextView.setText(new com.wnc.news.richtext.HtmlRichText(
                        msg.obj.toString()).getCharSequence());
                if (hasTopics())
                {
                    topicListBt.setVisibility(View.VISIBLE);
                    topicListBt.setText("" + allFind.size());
                }

                break;
            case MESSAGE_ON_IMG_TEXT:
                newsImgTv.setVisibility(View.VISIBLE);
                newsImgTv.setText((CharSequence) msg.obj);
                break;
            case MESSAGE_ON_WORD_DISPOSS_CODE:
                System.out.println("自动清空!");
                hideVirtualBts();
                hideWordZone();
                break;
            default:
                break;
            }
        }
    };

    private void hideCursor()
    {
        try
        {
            mTextView.hideCursor();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    int mTouchX2;
    int mTouchY2;
    int topicBtClickCount = 0;

    @SuppressLint("NewApi")
    private void showTopicList()
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

    private boolean hasTopics()
    {
        return allFind != null && allFind.size() > 0;
    }

    static Map<Integer, CharSequence> wordExpandContentMap = new HashMap<Integer, CharSequence>();

    private void initView()
    {
        newsMenuPopWindow = new NewsMenuPopWindow(this, new NewsMenuListener()
        {

            @Override
            public void toSrcPage()
            {
                if (news_info != null)
                {
                    gotoIE(news_info.getUrl());
                }
            }

            @Override
            public void doFavorite()
            {
                String url = news_info.getUrl();
                BasicFileUtil.writeFileString(MyAppParams.FAVORITE_TXT, url
                        + "\r\n", "UTF-8", true);
                common.app.ToastUtil.showShortToast(getApplicationContext(),
                        "操作成功!");
                log.info("收藏新闻成功: " + url);
            }
        });
        wordMenuPopWindow = new WordMenuPopWindow(this, new WordMenuListener()
        {
            @Override
            public void doSound()
            {
                String voicePath = MyAppParams.VOICE_FOLDER + getCurrentWord()
                        + ".mp3";
                if (BasicFileUtil.isExistFile(voicePath))
                {
                    SrtVoiceHelper.play(voicePath);
                }
                else
                {
                    common.app.ToastUtil.showShortToast(
                            getApplicationContext(), "找不到声音文件!");
                    log.info("找不到声音文件:" + voicePath);
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
                gotoIE(WebUrlHelper.getWordUrl(getCurrentWord()));
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
                System.out.println("doexpand");
                final Dialog dialog = new Dialog(NewsContentActivity.this,
                        R.style.CustomDialogStyle);
                dialog.setContentView(R.layout.topic_tip_wdailog);
                dialog.setCanceledOnTouchOutside(true);
                Window window = dialog.getWindow();

                WindowManager.LayoutParams lp = window.getAttributes();
                lp.width = (int) (0.85 * BasicPhoneUtil
                        .getScreenWidth(NewsContentActivity.this));
                lp.height = (int) (0.85 * BasicPhoneUtil
                        .getScreenHeight(NewsContentActivity.this));
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

                if (seekWordList.size() > 0)
                {
                    final Integer topic_id = seekWordList.peek().getTopic_id();
                    if (wordExpandContentMap.containsKey(topic_id))
                    {
                        tvTopic.setText(wordExpandContentMap.get(topic_id));
                        dialog.show();
                    }
                    else
                    {
                        System.out.println("not contains");
                        WordExpand wordExpand = DictionaryDao
                                .findSameAntonym(seekWordList.peek()
                                        .getTopic_id());

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
                                wordExpandContentMap
                                        .put(topic_id, charSequence);
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
                gotoIE(WebUrlHelper
                        .getTranslateUrl(getCurrentSectionAndSetPos()));
                log.info("翻译: " + getCurrentSectionAndSetPos());
            }

            @Override
            public void doFavorite()
            {
                System.out.println(getCurrentSectionAndSetPos());
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
                        // final String selectedText = mTextView
                        // .getCursorSelection().getSelectedText()
                        // .toString();
                        // if (selectedText.contains(" "))
                        // {
                        // System.out.println("选择句子:" + selectedText);
                        // }
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
                                    .getScreenHeight(NewsContentActivity.this);

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
        mScrollView.setOnTouchListener(new TouchScrollListenerImpl());
    }

    int flag = 0;

    private class TouchScrollListenerImpl implements OnTouchListener
    {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent)
        {
            switch (motionEvent.getAction())
            {
            case MotionEvent.ACTION_UP:
                flag = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if (flag == 0)
                {
                    int scrollY = view.getScrollY();
                    int height = view.getHeight();
                    int scrollViewMeasuredHeight = mScrollView.getChildAt(0)
                            .getMeasuredHeight();
                    if (scrollY == 0)
                    {
                        // System.out.println("滑动到了顶部 scrollY=" + scrollY);
                        // newsImgTv.setVisibility(View.VISIBLE);
                        flag = 1;
                    }
                    else if ((scrollY + height) == scrollViewMeasuredHeight)
                    {
                        // System.out.println("滑动到了底部 scrollY=" + scrollY);
                        // System.out.println("滑动到了底部 height=" + height);
                        // System.out.println("滑动到了底部 scrollViewMeasuredHeight="
                        // + scrollViewMeasuredHeight);

                    }
                    else
                    {
                        // newsImgTv.setVisibility(View.GONE);
                        flag = 1;
                    }
                }
                break;

            default:
                break;
            }
            return false;
        }
    };

    private void showSelectionCursors(int x, int y)
    {
        int start = mTextView.getPreciseOffset(x, y);

        if (start > -1)
        {
            String selectedText = NewsContentUtil.getSuitWordAndSetPos(
                    mTextView, start);
            log.info("selectedText:" + selectedText);
            wordTipTextThread.refresh();
            showWordZone(selectedText);
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

    private void hideWordZone()
    {
        wordMenuBtn.setVisibility(View.INVISIBLE);
        wordTipTv.setVisibility(View.INVISIBLE);
    }

    static Stack<DicWord> seekWordList = new Stack<DicWord>();

    private void showWordZone(String selectedText)
    {
        if (BasicStringUtil.isNullString(selectedText))
        {
            return;
        }
        DicWord findWord = null;
        BasicRunTimeUtil util = new BasicRunTimeUtil("");
        util.beginRun();
        for (DicWord d : seekWordList)
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
            wordTipTv.setText(findWord.getBase_word() + " "
                    + findWord.getCn_mean());
            seekWordList.push(findWord);
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
            wordTipTv.setText(selectedText);

        }
        wordTipTextThread.stopListen();
        wordMenuPopWindow.showPopupWindow(wordMenuBtn);

        wordTipTv.setVisibility(View.VISIBLE);
        wordMenuBtn.setVisibility(View.VISIBLE);

    }

    private void setNewsTitle(String title)
    {
        ((TextView) findViewById(R.id.tv_news_title)).setText(title);
    }

    @Override
    protected void onDestroy()
    {
        sectionPopWindow.dismiss();
        wordMenuPopWindow.dismiss();
        newsMenuPopWindow.dismiss();
        super.onDestroy();
    }

    /**
     * 多余多层次的搜索,使用栈来管理数据
     */
    Stack<List<NewsInfo>> newsStack = new Stack<List<NewsInfo>>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("welcome back..." + requestCode);

        if (requestCode == REQUEST_SEARCH_CODE && newsStack.size() > 0)
        {
            ViewNewsHolder.refrehList(newsStack.pop());
            ViewNewsHolder.refreh(news_info);
        }
    }

    @Override
    public void uncaughtException(Thread arg0, Throwable ex)
    {
        log.error("uncaughtException", ex);
    }

    /**
     * 隐藏虚拟按键
     */
    @SuppressLint("NewApi")
    private void hideVirtualBts()
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

    private String getCurrentWord()
    {
        return PatternUtil.getFirstPatternGroup(wordTipTv.getText().toString(),
                "\\w+");
    }

    private String getCurrentSectionAndSetPos()
    {
        return NewsContentUtil.getSectionAndSetPos(mTextView, mTextView
                .getCursorSelection().getStart(), mTextView
                .getCursorSelection().getEnd());
    }

    public Handler getHandler()
    {
        return handler;
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
            showNewsMenu();
            break;
        default:
            break;
        }
    }

    private void showNewsMenu()
    {
        System.out.println("显示新闻菜单!");
        newsMenuPopWindow.showPopupWindow(newsMenuBtn);
    }

    private void gotoIE(String page)
    {
        try
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(page));
            startActivity(intent);
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

    private void enableTextViewLink(boolean flag)
    {
        if (!flag)
        {
            mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        }
        else
        {
            mTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @Override
    public void doLeft(FlingPoint p1, FlingPoint p2)
    {
        changeNews(ViewNewsHolder.getPre());
    }

    private void changeNews(NewsInfo info)
    {
        if (info != null && news_info.getUrl() != info.getUrl())
        {
            this.newsImgTv.setText("");
            news_info = info;
            setNewsTitle(news_info.getTitle());
            initData();
        }
    }

    @Override
    public void doRight(FlingPoint p1, FlingPoint p2)
    {
        changeNews(ViewNewsHolder.getNext());
    }
}
