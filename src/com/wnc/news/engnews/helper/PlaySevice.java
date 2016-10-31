package com.wnc.news.engnews.helper;

import java.io.File;
import java.io.FileInputStream;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnSeekCompleteListener;

public class PlaySevice
{
    private String path;
    private volatile boolean autoStop = true;
    volatile boolean isRunning = true;
    boolean isLastOne = false;

    private final int SLEEP_TIME = 200;

    public PlaySevice(String path, boolean autoStop)
    {
        this.path = path;
        this.autoStop = autoStop;
    }

    public void setAutoStop(boolean autoStop)
    {
        this.autoStop = autoStop;
    }

    MediaPlayer player;

    public void initPlayer()
    {
        try
        {
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(fis.getFD());
            player.prepare();
            player.setOnCompletionListener(new OnCompletionListener()
            {

                @Override
                public void onCompletion(MediaPlayer arg0)
                {
                    isRunning = false;
                    player.pause();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void play(final int seekTime, final int stopTime)
    {
        if (seekTime == stopTime)
        {
            isLastOne = true;
        }
        else
        {
            isLastOne = false;
        }
        try
        {
            if (seekTime > 0)
            {
                player.seekTo(seekTime);
                player.setOnSeekCompleteListener(new OnSeekCompleteListener()
                {
                    @Override
                    public void onSeekComplete(MediaPlayer arg0)
                    {
                        player.start();
                    }
                });
            }
            else
            {
                player.start();
            }
            isRunning = true;
            new Thread(new Runnable()
            {
                int duration = 0;

                @Override
                public void run()
                {
                    while (isRunning)
                    {
                        if ((!isLastOne && duration >= stopTime - seekTime))
                        {
                            break;
                        }
                        try
                        {
                            Thread.sleep(SLEEP_TIME);
                            if (player.isPlaying())
                            {
                                duration += SLEEP_TIME;
                            }
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    if (autoStop)
                    {
                        pause();
                    }
                }
            }).start();

        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }

    }

    public void pause()
    {
        player.pause();
    }

    public void pauseOrPlay()
    {
        if (player.isPlaying())
        {
            pause();
        }
        else
        {
            player.start();
        }
    }
}
