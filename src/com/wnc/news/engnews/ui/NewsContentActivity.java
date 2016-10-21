package com.wnc.news.engnews.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.selectabletv.SelectableTextView;
import net.selectabletv.SelectableTextView.OnCursorStateChangedListener;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import word.DicWord;
import word.Topic;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.LinkMovementMethod;
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
import com.wnc.basic.BasicStringUtil;
import com.wnc.news.api.autocache.CETTopicCache;
import com.wnc.news.api.autocache.PassedTopicCache;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.dao.DictionaryDao;
import com.wnc.news.engnews.helper.NewsContentUtil;
import com.wnc.news.engnews.helper.SrtVoiceHelper;
import com.wnc.news.engnews.helper.WordTipTextThread;
import com.wnc.news.engnews.ui.SectionPopWindow.WordSectionListener;
import com.wnc.news.engnews.ui.WordMenuPopWindow.WordMenuListener;
import com.wnc.news.richtext.WebImgText;
import com.wnc.string.PatternUtil;
import common.app.BasicPhoneUtil;
import common.uihelper.MyAppParams;
import common.utils.JsoupHelper;

public class NewsContentActivity extends Activity implements
        UncaughtExceptionHandler, OnClickListener
{
    static Logger log = Logger.getLogger(NewsContentActivity.class);
    View main;

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

    private void initData()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Message msg2 = new Message();
                msg2.what = MESSAGE_ON_IMG_TEXT;
                msg2.obj = new WebImgText("<img src=\""
                        + news_info.getHead_pic() + "\"/>").getCharSequence();
                handler.sendMessage(msg2);
            }
        }).start();
        new Thread(new Runnable()
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
                            log.error(news_info.getUrl() + " 没找到任何Topic!");
                        }

                        msg.obj = news_info.getHtml_content();
                        msg.what = 1;
                    }
                    else
                    {
                        log.info("解析网页" + news_info.getUrl());
                        Document doc = JsoupHelper.getDocumentResult(news_info
                                .getUrl());
                        contents = doc.select(news_info.getWebsite()
                                .getNews_class());
                        msg.what = 11;
                        msg.obj = contents;
                    }

                    handler.sendMessage(msg);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
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
                mTextView.setText(new com.wnc.news.richtext.HtmlRichText(
                        msg.obj.toString()).getCharSequence());
                if (hasTopics())
                {
                    topicListBt.setVisibility(View.VISIBLE);
                    topicListBt.setText("" + allFind.size());

                }
                break;
            case 11:
                mTextView.setText(new WebImgText(new CETTopicCache()
                        .splitArticle(msg.obj.toString(), allFind))
                        .getCharSequence());
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

    private void hideWordZone()
    {
        wordMenuBtn.setVisibility(View.GONE);
        wordTipTv.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("NewApi")
    private void showTopicList()
    {
        if (hasTopics())
        {
            Dialog dialog = new Dialog(this, R.style.CustomDialogStyle);
            dialog.setContentView(R.layout.topic_tip_wdailog);
            dialog.setCanceledOnTouchOutside(true);
            Window window = dialog.getWindow();

            WindowManager.LayoutParams lp = window.getAttributes();
            int width = BasicPhoneUtil.getScreenWidth(this);
            lp.width = (int) (0.8 * width);

            final TextView tvTopic = (TextView) dialog
                    .findViewById(R.id.tvTopicInfo);
            Iterator<Topic> iterator = allFind.iterator();
            String tpContent = "总词数/生词率: "
                    + totalWords
                    + "/"
                    + BasicNumberUtil.convertScienceNum(100.0 * allFind.size()
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

    private void initView()
    {
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
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://m.iciba.com/" + getCurrentWord()));
                startActivity(i);
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
        });
        sectionPopWindow = new SectionPopWindow(this, new WordSectionListener()
        {

            @Override
            public void doTranslate()
            {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri
                        .parse("http://fanyi.baidu.com/translate?aldtype=16047&query=&keyfrom=baidu&smartresult=dict&lang=auto2zh#en/zh/"
                                + getCurrentSection()));
                startActivity(i);
            }

            @Override
            public void doFavorite()
            {
                System.out.println(getCurrentSection());
                // BasicFileUtil.writeFileString(MyAppParams.PASS_TXT,
                // getCurrentSection() + "\r\n", "UTF-8", true);
                common.app.ToastUtil.showShortToast(getApplicationContext(),
                        "操作成功!");
                log.info("收藏成功: ");
            }

            @Override
            public void doCopy()
            {

            }
        });
        newsMenuBtn = (ImageButton) findViewById(R.id.imgbt_news_menu);
        wordMenuBtn = (Button) findViewById(R.id.btn_word_menu);
        topicListBt = (Button) findViewById(R.id.bt_topics);
        newsImgTv = (TextView) findViewById(R.id.tv_img);
        newsImgTv.setMovementMethod(LinkMovementMethod.getInstance());

        wordMenuBtn.setVisibility(View.GONE);

        newsMenuBtn.setOnClickListener(this);
        wordMenuBtn.setOnClickListener(this);
        topicListBt.setOnClickListener(this);
        topicListBt.setVisibility(View.INVISIBLE);
        wordTipTv = (TextView) findViewById(R.id.tv_oneword_tip);

        mTextView = (SelectableTextView) findViewById(R.id.tv_content);
        mTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mTextView.setDefaultSelectionColor(0x40FF00FF);
        mTextView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                hideWordZone();
                hideCursor();
                showSelectionCursors(mTouchX, mTouchY);
                return true;
            }
        });
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
        mTextView.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View arg0)
            {
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
            String selectedText = NewsContentUtil.getSuitWord(mTextView, start);
            log.info("selectedText:" + selectedText);

            wordTipTextThread.refresh();
            DicWord findWord = DictionaryDao.findWord(selectedText);
            if (findWord != null)
            {
                wordTipTv.setVisibility(View.VISIBLE);
                wordTipTv.setText(findWord.getBase_word() + " "
                        + findWord.getCn_mean());
                wordMenuBtn.setVisibility(View.VISIBLE);
            }
            else
            {
                wordTipTv.setVisibility(View.VISIBLE);
                wordTipTv.setText(selectedText);
                wordMenuBtn.setVisibility(View.VISIBLE);
                wordTipTextThread.stopListen();
                wordMenuPopWindow.showPopupWindow(topicListBt);
            }
        }
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
        super.onDestroy();
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

    private String getCurrentSection()
    {
        return NewsContentUtil.getSection(mTextView.getText().toString(),
                mTextView.getCursorSelection().getStart(), mTextView
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
    }
}
