package com.wnc.news.engnews.helper;

import java.io.File;
import java.io.FileInputStream;
import java.util.Queue;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.wnc.basic.BasicFileUtil;

public class SrtVoiceHelper
{
	static MediaPlayer player;
	static boolean isPlaying = false;

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

	@SuppressWarnings("resource")
	public synchronized static void play(String voicePath)
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
			player.start();
			isPlaying = true;
			player.setOnCompletionListener(new OnCompletionListener()
			{
				@Override
				public void onCompletion(MediaPlayer mp)
				{
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
