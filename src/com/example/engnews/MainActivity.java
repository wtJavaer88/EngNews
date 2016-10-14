package com.example.engnews;

import java.lang.Thread.UncaughtExceptionHandler;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity implements UncaughtExceptionHandler
{
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
                getHtml();
            }
        }).start();
    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            newsContentTv.setText(Html.fromHtml(msg.obj.toString()));
        }
    };

    private void initView()
    {
        newsContentTv = (TextView) findViewById(R.id.tv_content);
    }

    private void getHtml()
    {
        Document doc = null;
        try
        {
            doc = JsoupHelper
                    .getDocumentResult("http://www.squawka.com/news/arsenals-granit-xhaka-admits-being-a-football-freak-hell-even-watch-league-one/797163");
            final Elements select = doc.select(".entry-content p");
            System.out.println(select);
            Message msg = new Message();
            msg.what = 1;
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
