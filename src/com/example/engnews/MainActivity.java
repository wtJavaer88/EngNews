package com.example.engnews;

import java.lang.Thread.UncaughtExceptionHandler;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.example.richtext.ClickableWordRichText;
import com.example.richtext.HtmlRichText;
import com.example.richtext.NormalText;

public class MainActivity extends Activity implements UncaughtExceptionHandler
{
    private static final int MESSAGE_NEWS_GET_OK = 1;
    TextView newsContentTv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread.setDefaultUncaughtExceptionHandler(this);

        initView();
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                // getHtml();
            }
        }).start();
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            newsContentTv.append(Html.fromHtml(msg.obj.toString()));
        }
    };

    private void initView()
    {
        newsContentTv = (TextView) findViewById(R.id.tv_content);
        newsContentTv.setMovementMethod(LinkMovementMethod.getInstance());

        newsContentTv.append(new NormalText("阿森纳扎卡").getCharSequence());
        newsContentTv
                .append(new HtmlRichText(
                        "<a href=\"http://www.squawka.com/news/arsenals-granit-xhaka-admits-being-a-football-freak-hell-even-watch-league-one/797163\">点击详情</a>")
                        .getCharSequence());
        newsContentTv.append(new ClickableWordRichText(this, " despite ")
                .getCharSequence());
        newsContentTv.append("\n");
        newsContentTv
                .append(new HtmlRichText(
                        "<img src=\"http://p2.ifengimg.com/cmpp/2016/10/14/15/a1007aa4-b93b-4046-979e-140462597b2f_size18_w550_h365.jpg\"/>")
                        .getCharSequence());

    }

    private void getHtml()
    {
        System.out.println("getHtml/...");
        Document doc = null;
        try
        {
            doc = JsoupHelper
                    .getDocumentResult("http://www.squawka.com/news/arsenals-granit-xhaka-admits-being-a-football-freak-hell-even-watch-league-one/797163");
            final Elements select = doc.select(".entry-content p");
            System.out.println(select);
            Message msg = new Message();
            msg.what = MESSAGE_NEWS_GET_OK;
            msg.obj = select;
            handler.sendMessage(msg);
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
