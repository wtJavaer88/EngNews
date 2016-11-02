package com.wnc.news.engnews.helper;

import common.utils.TimeUtil;

public class ActivityTimeUtil
{
    private int SLEEP_TIME = 100;
    private int duration;
    private volatile boolean isPaused = false;
    private volatile boolean isStoped = false;

    public ActivityTimeUtil()
    {

    }

    public void begin()
    {
        duration = 0;
        isPaused = false;
        isStoped = false;
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (!isStoped)
                {
                    try
                    {
                        Thread.sleep(SLEEP_TIME);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    if (!isPaused)
                    {
                        duration += SLEEP_TIME;
                    }
                }
            }
        }).start();

    }

    public void pause()
    {
        isPaused = true;
    }

    public void resume()
    {
        isPaused = false;
    }

    public void stop()
    {
        isStoped = true;
    }

    public void restart()
    {
        stop();
        begin();
    }

    public int getRunTime()
    {
        return duration;
    }

    public String getTranedRunTime()
    {
        return TimeUtil.timeToText(getRunTime());
    }

}
