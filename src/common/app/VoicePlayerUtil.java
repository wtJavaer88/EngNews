package common.app;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

public class VoicePlayerUtil
{
	public static void playAssetVoice(Context context, String fileName) throws IOException
	{
		AssetFileDescriptor fileDescriptor = context.getAssets().openFd(fileName);
		MediaPlayer mediaPlayer = new MediaPlayer();
		mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(), fileDescriptor.getLength());
		mediaPlayer.prepare();
		mediaPlayer.start();
	}
}
