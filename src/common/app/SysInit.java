package common.app;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import android.app.Activity;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import com.wnc.news.api.autocache.PassedTopicCache;
import com.wnc.news.dao.DictionaryDao;
import com.wnc.news.db.DatabaseManager_Main;
import com.wnc.news.db.DatabaseManager_VOA;
import com.wnc.news.db.DatabaseManager_ZB8;
import com.wnc.news.db.SQLiteHelperOfOpen;
import common.uihelper.MyAppParams;

public class SysInit
{

	public static void init(Activity context2)
	{
		Log4jUtil.configLog(MyAppParams.LOG_FOLDER + BasicDateUtil.getCurrentDateString() + ".txt");
		SharedPreferenceUtil.init(context2);
		MyAppParams.mainActivity = context2;
		MyAppParams.getInstance().setPackageName(context2.getPackageName());
		MyAppParams.getInstance().setResources(context2.getResources());
		MyAppParams.getInstance().setAppPath(context2.getFilesDir().getParent());
		MyAppParams.setScreenWidth(BasicPhoneUtil.getScreenWidth(context2));
		MyAppParams.setScreenHeight(BasicPhoneUtil.getScreenHeight(context2));

		PassedTopicCache.init();
		DictionaryDao.initTopics();
		// createDbAndFullData(context2);
		try
		{
			if (!BasicFileUtil.isExistFile(MyAppParams.NEWS_DB))
			{
				BasicFileUtil.writeFileByte(MyAppParams.NEWS_DB, IOUtils.toByteArray(AssertsUtil.getInputStream(context2, "news.db")));
			}
			if (!BasicFileUtil.isExistFile(MyAppParams.ZB8_DB))
			{
				BasicFileUtil.writeFileByte(MyAppParams.ZB8_DB, IOUtils.toByteArray(AssertsUtil.getInputStream(context2, "zb8.db")));
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		SQLiteOpenHelper myHelper = new SQLiteHelperOfOpen(context2, MyAppParams.NEWS_DB, null, 1);
		SQLiteOpenHelper myHelperVoa = new SQLiteHelperOfOpen(context2, MyAppParams.VOA_DB, null, 1);
		SQLiteOpenHelper myHelperZb8 = new SQLiteHelperOfOpen(context2, MyAppParams.ZB8_DB, null, 1);
		DatabaseManager_Main.initializeInstance(myHelper);
		DatabaseManager_VOA.initializeInstance(myHelperVoa);
		DatabaseManager_ZB8.initializeInstance(myHelperZb8);

		// NewsDao.test();
	}

	static String FIRST_RUN = "isSrtlearnFirstRun";

	private static boolean isFirstRun()
	{
		boolean isFirstRun = SharedPreferenceUtil.getShareDataByKey(FIRST_RUN, true);
		if (isFirstRun)
		{
			Log.i("Sysinit", "第一次运行");
			SharedPreferenceUtil.changeValue(FIRST_RUN, false);
			return true;
		}
		else
		{
			Log.i("Sysinit", "不是第一次运行");
		}
		return false;
	}

}
