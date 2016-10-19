package com.wnc.news.engnews.helper;

import android.os.Message;

import com.wnc.news.engnews.ui.NewsContentActivity;

public class WordTipTextThread extends Thread
{
    int DISPOSS_TIME = 6000;
    int process = 0;
    private static final int SLEEP_TIME = 500;
    NewsContentActivity activity;
    private volatile boolean isShowing = false;

    public WordTipTextThread(NewsContentActivity activity)
    {
        this.activity = activity;
    }

    /**
     * 开启新的监听
     */
    public void refresh()
    {
        this.process = 0;
        isShowing = true;
    }

    /**
     * 停止监听
     */
    public void stopListen()
    {
        isShowing = false;
        this.process = 0;
    }

    @Override
    public void run()
    {
        while (true)
        {
            if (process >= DISPOSS_TIME)
            {
                if (isShowing)
                {
                    Message msg = new Message();
                    msg.what = NewsContentActivity.MESSAGE_ON_WORD_DISPOSS_CODE;
                    activity.getHandler().sendMessage(msg);
                    stopListen();
                }
            }
            try
            {
                Thread.sleep(SLEEP_TIME);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            if (isShowing)
            {
                process += SLEEP_TIME;
            }
        }
    }
}
