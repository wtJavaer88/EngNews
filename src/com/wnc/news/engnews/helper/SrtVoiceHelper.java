package com.wnc.news.engnews.helper;

import java.io.File;
import java.io.FileInputStream;
import java.util.Queue;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;

import com.wnc.basic.BasicFileUtil;

public class SrtVoiceHelper
{
    static MediaPlayer player;
    static boolean isPlaying = false;
    static String lastVoice;

    public synchronized static void stop()
    {
        try
        {
            if (player != null)
            {
                player.reset();
                player.release();
                player = null;
                isPlaying = false;
            }
        }
        catch (Exception e)
        {
            player = null;
            isPlaying = false;
            System.out.println("voiceStopEx." + e.getMessage());
        }
    }

    public synchronized static void play(String voicePath)
    {
        play(voicePath, 0);
    }

    @SuppressWarnings("resource")
    public synchronized static void play(String voicePath, final int seek)
    {
        try
        {
            stop();
            File file = new File(voicePath);
            FileInputStream fis = new FileInputStream(file);
            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(fis.getFD());
            player.prepare();
            if (seek > 0)
            {

                player.setOnPreparedListener(new OnPreparedListener()
                {

                    @Override
                    public void onPrepared(MediaPlayer arg0)
                    {
                        player.seekTo(seek);
                        player.setOnSeekCompleteListener(new OnSeekCompleteListener()
                        {

                            @Override
                            public void onSeekComplete(MediaPlayer arg0)
                            {
                                player.start();
                            }
                        });
                    }
                });
            }
            else
            {
                player.start();
            }
            lastVoice = voicePath;
            isPlaying = true;
            player.setOnCompletionListener(new OnCompletionListener()
            {

                @Override
                public void onCompletion(MediaPlayer arg0)
                {
                    isPlaying = false;
                }
            });
        }
        catch (Exception e)
        {
            player = null;
            isPlaying = false;
            System.out.println("voicePlayEx." + e.getMessage());
            e.printStackTrace();
        }
    }

    public static int getcurrentPos()
    {
        if (player != null)
        {
            return player.getCurrentPosition();
        }
        return 0;
    }

    public static boolean isPlaying()
    {
        return isPlaying;
    }

    public static void playInList(final Queue<String> queue)
    {
        if (queue == null || queue.size() == 0)
        {
            return;
        }
        try
        {
            stop();
            String voicePath = queue.poll();
            if (!BasicFileUtil.isExistFile(voicePath))
            {
                return;
            }
            File file = new File(voicePath);
            FileInputStream fis = new FileInputStream(file);
            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(fis.getFD());
            player.prepare();
            player.start();
            isPlaying = true;
            player.setOnCompletionListener(new OnCompletionListener()
            {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    playInList(queue);
                }
            });
        }
        catch (Exception e)
        {
            System.out.println("player:" + player);
            player = null;
            isPlaying = false;
            System.out.println("voicePlayEx." + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
