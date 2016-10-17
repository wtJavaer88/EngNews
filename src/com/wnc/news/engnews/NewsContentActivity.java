package com.wnc.news.engnews;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.selectabletv.SelectableTextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import word.Topic;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.engnews.R;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.dao.DictionaryDao;
import com.wnc.news.dao.NewsDao;
import com.wnc.news.richtext.WebImgText;
import common.app.BasicPhoneUtil;
import common.utils.JsoupHelper;

public class NewsContentActivity extends Activity implements
        UncaughtExceptionHandler, OnClickListener
{
    private static final int MESSAGE_NEWS_GET_OK = 1;
    TextView newsContentTv;
    TextView newsImgTv;
    List<Topic> allFind = new ArrayList<Topic>();
    public static NewsInfo news_info;

    private Button topicListBt;

    private SelectableTextView mTextView;
    private int mTouchX;
    private int mTouchY;
    private final static int DEFAULT_SELECTION_LEN = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);
        Thread.setDefaultUncaughtExceptionHandler(this);

        initView();

        if (news_info != null)
        {
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
                Document doc;
                try
                {
                    Elements contents = null;
                    if (news_info.getHtml_content() != null)
                    {
                        NewsInfo news = NewsDao.findFirstNews();
                        Document parse = Jsoup.parse(news.getHtml_content());
                        contents = parse.getAllElements();
                    }
                    else
                    {
                        doc = JsoupHelper.getDocumentResult(news_info.getUrl());
                        contents = doc.select(news_info.getWebsite()
                                .getNews_class());
                    }
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = contents;
                    handler.sendMessage(msg);

                    if (BasicPhoneUtil.isWifiConnect(getApplicationContext()))
                    {
                        Message msg2 = new Message();
                        msg2.what = 2;
                        msg2.obj = new WebImgText("<img src=\""
                                + news_info.getHead_pic() + "\"/>")
                                .getCharSequence();
                        handler.sendMessage(msg2);
                    }
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
                // newsContentTv.setText((CharSequence) msg.obj);
                splitArticle((Elements) msg.obj);
                break;
            case 2:
                newsImgTv.setText((CharSequence) msg.obj);
                break;
            default:
                break;
            }
        }

        private void splitArticle(Elements elements)
        {
            for (Element element : elements)
            {
                String dialog = element.toString();
                Set<Topic> set = DictionaryDao.findCETWords(dialog.replace(
                        "target=\"_blank\"", ""));
                if (set.size() == 0)
                {
                    newsContentTv.append(new WebImgText(dialog)
                            .getCharSequence());
                }
                else
                {
                    for (Topic topic : set)
                    {
                        if (!allFind.contains(topic))
                        {
                            allFind.add(topic);
                        }
                    }
                    getDealResult(dialog, set);
                    topicListBt.setVisibility(View.VISIBLE);
                    topicListBt.setText("" + allFind.size());
                }
            }
        }

        private String getDealResult(String aString, Set<Topic> keys)
        {
            StringBuilder result = new StringBuilder();
            int openTag = aString.indexOf("<a ");
            int closeTag;
            while (openTag > -1)
            {
                closeTag = aString.indexOf("</a>") + 4;
                String left = aString.substring(0, openTag);
                result.append(deal(left, keys));
                result.append(aString.substring(openTag, closeTag));
                aString = aString.substring(closeTag);

                openTag = aString.indexOf("<a ");
            }
            result.append(deal(aString, keys));
            // 给关键词加上超链接
            newsContentTv.append(new WebImgText(result.toString())
                    .getCharSequence());
            return result.toString();
        }

        private String deal(String s, Set<Topic> keys)
        {
            for (Topic key : keys)
            {
                s = s.replace(key.getMatched_word(),
                        "<a href=\"http://m.iciba.com/" + key.getMatched_word()
                                + "\" style=\"color:red;font-size:14px\">"
                                + key.getMatched_word() + "</font></a>");
            }
            return s;
        }
    };

    @SuppressLint("NewApi")
    private void showTopicList()
    {
        if (allFind != null && allFind.size() > 0)
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

    private void initView()
    {
        topicListBt = (Button) findViewById(R.id.bt_topics);
        newsContentTv = (TextView) findViewById(R.id.tv_content);
        newsContentTv.setMovementMethod(LinkMovementMethod.getInstance());
        newsImgTv = (TextView) findViewById(R.id.tv_img);
        newsImgTv.setMovementMethod(LinkMovementMethod.getInstance());

        topicListBt.setOnClickListener(this);
        topicListBt.setVisibility(View.INVISIBLE);
        // newsContentTv.append(new NormalText("阿森纳扎卡").getCharSequence());
        // newsContentTv.append(new
        // HtmlRichText("<a href=\"http://www.squawka.com/news/arsenals-granit-xhaka-admits-being-a-football-freak-hell-even-watch-league-one/797163\">点击详情</a>").getCharSequence());
        // newsContentTv.append(new ClickableWordRichText(this,
        // " despite ").getCharSequence());
        // newsContentTv.append("\n");
        mTextView = (SelectableTextView) findViewById(R.id.main_text);
        mTextView.setDefaultSelectionColor(0x40FF00FF);
        mTextView
                .setText(Html
                        .fromHtml("textview的长按事件是设置了属性textisselectable为true，就可以在长按之后<a href=\"www.baidu.com\">弹出功能框，加入想屏蔽或是修改功能框</a>就可以对textview设置setCustomSelectionActionModeCallback这个回调，然后在其方法中进行操作，但是设置了这个方法在一些手机上没有问题的，小米手机上却没有什么作用，那么要是想在所有手机上都有效果应该怎么做呢？求指导？"));
        mTextView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                System.out.println("longclick");
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

    }

    private void showSelectionCursors(int x, int y)
    {
        int start = mTextView.getPreciseOffset(x, y);

        if (start > -1)
        {
            int end = start + DEFAULT_SELECTION_LEN;
            if (end >= mTextView.getText().length())
            {
                end = mTextView.getText().length() - 1;
            }
            mTextView.showSelectionControls(start, end);
            System.out
                    .println(mTextView.getCursorSelection().getSelectedText());
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

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
        case R.id.bt_topics:
            showTopicList();
            break;
        default:
            break;
        }
    }
}
