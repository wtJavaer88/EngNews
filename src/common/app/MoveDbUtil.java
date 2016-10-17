package common.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.util.Log;

import com.wnc.basic.BasicFileUtil;

public class MoveDbUtil
{
	public static boolean moveAssertDb(String DB_NAME, final Context context)
	{
		return moveAssertDb(DB_NAME, DB_NAME, context);
	}

	public static boolean moveAssertDb(String ASSERT_DB_NAME, String DEST_DB_NAME, final Context context)
	{
		File dbFile = context.getDatabasePath(DEST_DB_NAME);
		String DB_PATH = dbFile.getPath();
		File folder = dbFile.getParentFile();
		if (!folder.exists())
		{
			if (!folder.exists())
			{
				Log.i("movedb", "db文件夹不存在");
				folder.mkdir();
			}
		}

		Log.i("moveDb", ASSERT_DB_NAME + "数据初始完毕!");
		Log.i("moveDb", "开始移动Assert数据库" + ASSERT_DB_NAME + "!!!!");
		try
		{
			return copy(DB_PATH, AssertsUtil.getInputStream(context, ASSERT_DB_NAME));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 从Sd中找个文件替换应用数据库文件
	 * 
	 * @param SdCard_DB_PATH
	 * @param DEST_DB_FILE
	 * @return
	 */

	public static boolean moveSdCardDb(String SdCard_DB_PATH, File DEST_DB_FILE)
	{
		try
		{
			return copy(DEST_DB_FILE.getAbsolutePath(), new FileInputStream(new File(SdCard_DB_PATH)));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	// 复制前先删除源文件
	private static boolean copy(String DEST_DB_PATH, InputStream is)
	{
		OutputStream os = null;
		try
		{
			BasicFileUtil.deleteFile(DEST_DB_PATH);
			os = new FileOutputStream(DEST_DB_PATH);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) != -1)
			{
				os.write(buffer, 0, length);
			}
			return true;
		}
		catch (Exception e)
		{
			Log.i("moveDb", "异常" + e.getMessage());
			e.printStackTrace();
		}
		finally
		{
			try
			{
				os.flush();
				os.close();
				is.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}
}
