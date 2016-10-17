package com.wnc.news.engnews;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.example.engnews.R;
import com.wnc.basic.BasicStringUtil;
import com.wnc.news.dao.DictionaryDao;
import com.wnc.news.richtext.WebImgText;
import common.utils.JsoupHelper;

public class NewsContentActivity extends Activity implements
        UncaughtExceptionHandler
{
    private static final int MESSAGE_NEWS_GET_OK = 1;
    TextView newsContentTv;
    TextView newsImgTv;

    private String news_url;
    private String news_class;
    private String news_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);
        Thread.setDefaultUncaughtExceptionHandler(this);

        initView();

        if (getIntent() != null && getIntent().hasExtra("news_url")
                && getIntent().hasExtra("news_class"))
        {
            news_url = getIntent().getStringExtra("news_url");
            news_class = getIntent().getStringExtra("news_class");
            news_pic = getIntent().getStringExtra("news_pic");
            if (BasicStringUtil.isNotNullString(news_url)
                    && BasicStringUtil.isNotNullString(news_class))
            {
                initData();
            }
        }

    }

    private void initData()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                DictionaryDao.initTopics();
                Document doc;
                try
                {
                    doc = JsoupHelper.getDocumentResult(news_url);

                    final Elements contents = doc.select(news_class);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = contents;
                    handler.sendMessage(msg);

                    Message msg2 = new Message();
                    msg2.what = 2;
                    msg2.obj = new WebImgText("<img src=\"" + news_pic + "\"/>")
                            .getCharSequence();
                    handler.sendMessage(msg2);
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
                newsContentTv.append(Html.fromHtml(msg.obj.toString()));
                break;
            }
        }

        private void splitArticle(Elements elements)
        {
            // WORD=means
            for (Element element : elements)
            {
                String dialog = element.toString();
                Set<String> set = DictionaryDao.findCETWords(dialog.replace(
                        "target=\"_blank\"", ""));
                if (set.size() == 0)
                {
                    newsContentTv.append(new WebImgText(dialog)
                            .getCharSequence());
                }
                else
                {
                    System.out.println("find.." + set.size());
                    getDealResult(dialog, set);
                }
            }
        }

        private String getDealResult(String aString, Set<String> keys)
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

        private String deal(String s, Set<String> keys)
        {
            for (String key : keys)
            {
                s = s.replace(key, "<a href=\"http://m.iciba.com/" + key
                        + "\" style=\"color:red;font-size:14px\">" + key
                        + "</font></a>");
            }
            return s;
        }
    };

    private void initView()
    {
        newsContentTv = (TextView) findViewById(R.id.tv_content);
        newsContentTv.setMovementMethod(LinkMovementMethod.getInstance());
        newsImgTv = (TextView) findViewById(R.id.tv_img);
        newsImgTv.setMovementMethod(LinkMovementMethod.getInstance());

        // newsContentTv.append(new NormalText("阿森纳扎卡").getCharSequence());
        // newsContentTv.append(new
        // HtmlRichText("<a href=\"http://www.squawka.com/news/arsenals-granit-xhaka-admits-being-a-football-freak-hell-even-watch-league-one/797163\">点击详情</a>").getCharSequence());
        // newsContentTv.append(new ClickableWordRichText(this,
        // " despite ").getCharSequence());
        // newsContentTv.append("\n");

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
