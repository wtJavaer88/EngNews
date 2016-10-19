package com.wnc.news.engnews.ui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.selectabletv.SelectableTextView;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.engnews.R;
import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.news.api.autocache.CETTopicCache;
import com.wnc.news.api.autocache.PassedTopicCache;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.dao.DictionaryDao;
import com.wnc.news.engnews.helper.SrtVoiceHelper;
import com.wnc.news.engnews.helper.WordTipTextThread;
import com.wnc.news.engnews.ui.MorePopWindow.WordMenuListener;
import com.wnc.news.richtext.WebImgText;
import com.wnc.string.PatternUtil;
import common.app.BasicPhoneUtil;
import common.uihelper.MyAppParams;
import common.utils.JsoupHelper;

public class NewsContentActivity extends Activity implements
        UncaughtExceptionHandler, OnClickListener
{
    static Logger log = Logger.getLogger(NewsContentActivity.class);

    public static final int MESSAGE_ON_WORD_DISPOSS_CODE = 100;
    TextView newsImgTv;
    List<Topic> allFind = new ArrayList<Topic>();
    public static NewsInfo news_info;

    private Button topicListBt, wordMenuBtn;
    TextView wordTipTv;

    private SelectableTextView mTextView;
    private int mTouchX;
    private int mTouchY;

    WordTipTextThread wordTipTextThread;
    MorePopWindow morePopWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);
        Thread.setDefaultUncaughtExceptionHandler(this);

        wordTipTextThread = new WordTipTextThread(this);
        wordTipTextThread.start();
        initView();
        if (news_info != null)
        {
            setTitle(news_info.getTitle());
            initData();
        }

    }

    @Override
    protected void onDestroy()
    {
        morePopWindow.dismiss();
        super.onDestroy();
    }

    private void initData()
    {
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                Message msg2 = new Message();
                msg2.what = 2;
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
                        System.out.println("缓存数据字符数:"
                                + news_info.getHtml_content().length());

                        String str = news_info.getCet_topics();
                        if (BasicStringUtil.isNotNullString(str))
                        {
                            allFind = JSONObject.parseArray(JSONObject
                                    .parseObject(str).getString("data"),
                                    Topic.class);
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
                        System.out.println("解析网页" + news_info.getUrl());
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

    public Handler getHandler()
    {
        return handler;
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
            case 2:
                System.out.println("hanlder.." + msg.obj);
                newsImgTv.setText((CharSequence) msg.obj);
                break;
            case MESSAGE_ON_WORD_DISPOSS_CODE:
                System.out.println("自动清空!");
                wordTipTv.setVisibility(View.INVISIBLE);
                mTextView.hideCursor();
                break;
            default:
                break;
            }
        }

    };

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
            String tpContent = "";
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
        morePopWindow = new MorePopWindow(this, new WordMenuListener()
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
        wordMenuBtn = (Button) findViewById(R.id.btn_word_menu);
        topicListBt = (Button) findViewById(R.id.bt_topics);
        newsImgTv = (TextView) findViewById(R.id.tv_img);
        newsImgTv.setMovementMethod(LinkMovementMethod.getInstance());

        wordMenuBtn.setOnClickListener(this);
        topicListBt.setOnClickListener(this);
        topicListBt.setVisibility(View.INVISIBLE);
        wordTipTv = (TextView) findViewById(R.id.tv_oneword_tip);
        // newsContentTv.append(new NormalText("阿森纳扎卡").getCharSequence());
        // newsContentTv.append(new
        // HtmlRichText("<a href=\"http://www.squawka.com/news/arsenals-granit-xhaka-admits-being-a-football-freak-hell-even-watch-league-one/797163\">点击详情</a>").getCharSequence());
        // newsContentTv.append(new ClickableWordRichText(this,
        // " despite ").getCharSequence());
        // newsContentTv.append("\n");
        mTextView = (SelectableTextView) findViewById(R.id.tv_content);
        mTextView.setMovementMethod(LinkMovementMethod.getInstance());
        mTextView.setDefaultSelectionColor(0x40FF00FF);
        mTextView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                mTextView.hideCursor();
                showSelectionCursors(mTouchX, mTouchY);
                return true;
            }
        });
        mTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mTextView.hideCursor();
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
                mTextView.hideCursor();
                wordTipTv.setVisibility(View.INVISIBLE);
            }
        });

    }

    private String getCurrentWord()
    {
        return PatternUtil.getFirstPatternGroup(wordTipTv.getText().toString(),
                "\\w+");
    }

    private void showSelectionCursors(int x, int y)
    {
        int start = mTextView.getPreciseOffset(x, y);

        if (start > -1)
        {
            int end = start;
            CharSequence text = mTextView.getText();
            while ((text.charAt(end) + "").matches("[0-9a-zA-Z\\-]{1}"))
            {
                end++;
                if (end >= text.length())
                {
                    end = text.length() - 1;
                    break;
                }

            }

            while ((text.charAt(start) + "").matches("[0-9a-zA-Z\\-]{1}"))
            {
                start--;
                if (start < 0)
                {
                    break;
                }
            }

            mTextView.showSelectionControls(start + 1, end);
            CharSequence selectedText = mTextView.getCursorSelection()
                    .getSelectedText();
            log.info("selectedText:" + selectedText);

            wordTipTextThread.refresh();
            DicWord findWord = DictionaryDao.findWord(selectedText.toString());
            if (findWord != null)
            {
                wordTipTv.setVisibility(View.VISIBLE);
                wordTipTv.setText(findWord.getBase_word() + " "
                        + findWord.getCn_mean());
            }
            else
            {
                wordTipTv.setVisibility(View.VISIBLE);
                wordTipTv.setText(selectedText.toString());
            }
        }
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
            morePopWindow.showPopupWindow(wordMenuBtn);
            break;
        default:
            break;
        }
    }
}
