package common.uihelper;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Environment;

import com.example.engnews.R;
import com.wnc.basic.BasicFileUtil;

public class MyAppParams
{
	private String packageName;
	private Resources resources;
	private String appPath;

	private final static String workPath = Environment.getExternalStorageDirectory().getPath() + "/wnc/app/news/";

	public final static String SELECTED_WORDS_TXT = workPath + "selected_words.txt";
	public final static String FAVORITE_TXT = workPath + "favorite.txt";
	public final static String LOVE_NEWS_TXT = workPath + "love_news.txt";
	public final static String VIEWED_NEWS_TXT = workPath + "viewed_news.txt";

	public final static String PASS_TXT = workPath + "pass.txt";
	public final static String VOA_MP3_PATH = Environment.getExternalStorageDirectory().getPath() + "/wnc/res/voa/";
	public final static String NEWS_DB = workPath + "news.db";
	public final static String VOA_DB = workPath + "voa.db";
	public final static String ZB8_DB = workPath + "zb8.db";
	public final static String VOICE_FOLDER = Environment.getExternalStorageDirectory().getPath() + "/wnc/res/voice/";
	public final static String LOG_FOLDER = workPath + "/logs/";

	public final static String DICTIONARY_DB = Environment.getExternalStorageDirectory().getPath() + "/wnc/app/srtlearn/dictionary.db";
	public final static String FAVORITE_DB = workPath + "favorite.db";

	public static Activity mainActivity;

	private String backupDbPath;

	private String zipPath;

	private static int screenWidth;
	private static int screenHeight;

	private static MyAppParams singletonMyAppParams = new MyAppParams();

	private MyAppParams()
	{
		this.backupDbPath = this.workPath + "backupdb/";

		this.zipPath = this.workPath + "zip/";

		BasicFileUtil.makeDirectory(this.backupDbPath);
		BasicFileUtil.makeDirectory(LOG_FOLDER);
		BasicFileUtil.makeDirectory(this.zipPath);
	}

	public String getBaskModelName()
	{
		return getResources().getString(R.string.model_b);
	}

	public String getSoccModelName()
	{
		return getResources().getString(R.string.model_s);
	}

	public String getForuModelName()
	{
		return getResources().getString(R.string.model_f);
	}

	public String getVoaModelName()
	{
		return getResources().getString(R.string.model_v);
	}

	public static MyAppParams getInstance()
	{
		return singletonMyAppParams;
	}

	public String getZipPath()
	{
		return this.zipPath;
	}

	public String getBackupDbPath()
	{
		return this.backupDbPath;
	}

	public static int getScreenWidth()
	{
		return screenWidth;
	}

	public static void setScreenWidth(int screenWidth)
	{
		MyAppParams.screenWidth = screenWidth;
	}

	public static int getScreenHeight()
	{
		return screenHeight;
	}

	public static void setScreenHeight(int screenHeight)
	{
		MyAppParams.screenHeight = screenHeight;
	}

	public void setPackageName(String name)
	{
		if (name == null)
		{
			return;
		}
		if (this.packageName == null)
		{
			this.packageName = name;
		}
	}

	public String getPackageName()
	{
		return this.packageName;
	}

	public void setAppPath(String path)
	{
		if (path == null)
		{
			return;
		}
		if (this.appPath == null)
		{
			this.appPath = path;
		}
	}

	public void setResources(Resources res)
	{
		if (res == null)
		{
			return;
		}
		if (this.resources == null)
		{
			this.resources = res;
		}
	}

	public Resources getResources()
	{
		return this.resources;
	}

	public String getWorkPath()
	{
		return this.workPath;
	}

	public String getAppPath()
	{
		return this.appPath;
	}

}
