package common.app;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import common.uihelper.MyAppParams;

public class SysInit
{

	public static void init(Activity context2)
	{
		SharedPreferenceUtil.init(context2);
		MyAppParams.mainActivity = context2;
		MyAppParams.getInstance().setPackageName(context2.getPackageName());
		MyAppParams.getInstance().setResources(context2.getResources());
		MyAppParams.getInstance().setAppPath(context2.getFilesDir().getParent());
		MyAppParams.setScreenWidth(BasicPhoneUtil.getScreenWidth(context2));
		MyAppParams.setScreenHeight(BasicPhoneUtil.getScreenHeight(context2));

		if (isFirstRun())
		{
			createDbAndFullData(context2);
		}
	}

	private static void createDbAndFullData(Context context2)
	{
		boolean moveAssertDb = MoveDbUtil.moveAssertDb("srtlearn.db", "srtlearn.db", context2);
		System.out.println("移动成功标志: " + moveAssertDb);
		// 移植收藏字幕
		// SaveFavoriteSrtToDb.save();
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
