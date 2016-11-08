package com.wnc.news.engnews.helper;

import common.utils.TimeUtil;

public class ActivityTimeUtil
{
    private int SLEEP_TIME = 100;
    private int duration;
    private volatile boolean isPaused = false;
    private volatile boolean isStoped = false;

    Thread timerThread;

    public void begin()
    {
        if (timerThread != null)
        {
            return;
        }
        timerThread = new Thread(new Runnable()
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
        });
        timerThread.setDaemon(true);
        timerThread.start();
    }

    public void pause()
    {
        isPaused = true;
    }

    public void resume()
    {
        isPaused = false;
    }

    /**
     * 在Activity销毁的时候才能调用,代表后台线程终止
     */
    public void stop()
    {
        isStoped = true;
    }

    /**
     * 从0开始, 重新计时
     */
    public void restart()
    {
        duration = 0;
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
