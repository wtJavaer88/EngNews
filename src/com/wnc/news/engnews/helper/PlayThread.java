package com.wnc.news.engnews.helper;


public class PlayThread extends Thread
{
    private String path;
    private int seekTime;
    private int stopTime;
    private boolean autoStop = true;
    volatile boolean isRunning = true;
    boolean isLastOne = false;

    private final int SLEEP_TIME = 200;

    public PlayThread(String path, int seekTime, int stopTime)
    {
        System.out.println(path);
        this.path = path;
        this.seekTime = seekTime;
        this.stopTime = stopTime;
        if (seekTime == stopTime)
        {
            isLastOne = true;
        }
    }

    public PlayThread(String path, int seekTime, int stopTime, boolean autoStop)
    {
        this(path, seekTime, stopTime);
        this.autoStop = autoStop;
    }

    @Override
    public void run()
    {
        SrtVoiceHelper.play(path, seekTime);
        int duration = 0;
        while (isRunning)
        {
            if (!SrtVoiceHelper.isPlaying()
                    || (!isLastOne && duration >= stopTime - seekTime))
            {
                break;
            }
            try
            {
                Thread.sleep(SLEEP_TIME);
                duration += SLEEP_TIME;
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        if (autoStop)
        {
            SrtVoiceHelper.stop();
        }
    }

    public void stopPlay()
    {
        isRunning = false;
    }
}
