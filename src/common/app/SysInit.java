package common.app;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import android.app.Activity;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.wnc.basic.BasicFileUtil;
import com.wnc.news.api.autocache.PassedTopicCache;
import com.wnc.news.dao.DictionaryDao;
import com.wnc.news.db.DatabaseManager;
import com.wnc.news.db.SQLiteHelperOfOpen;
import common.uihelper.MyAppParams;

public class SysInit
{

    public static void init(Activity context2)
    {
        SharedPreferenceUtil.init(context2);
        MyAppParams.mainActivity = context2;
        MyAppParams.getInstance().setPackageName(context2.getPackageName());
        MyAppParams.getInstance().setResources(context2.getResources());
        MyAppParams.getInstance()
                .setAppPath(context2.getFilesDir().getParent());
        MyAppParams.setScreenWidth(BasicPhoneUtil.getScreenWidth(context2));
        MyAppParams.setScreenHeight(BasicPhoneUtil.getScreenHeight(context2));

        PassedTopicCache.init();
        DictionaryDao.initTopics();
        // createDbAndFullData(context2);
        if (!BasicFileUtil.isExistFile(MyAppParams.NEWS_DB))
        {
            try
            {
                BasicFileUtil.writeFileByte(MyAppParams.NEWS_DB, IOUtils
                        .toByteArray(AssertsUtil.getInputStream(context2,
                                "news.db")));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        SQLiteOpenHelper myHelper = new SQLiteHelperOfOpen(context2,
                MyAppParams.NEWS_DB, null, 1);
        DatabaseManager.initializeInstance(myHelper);
        // NewsDao.test();
    }

    static String FIRST_RUN = "isSrtlearnFirstRun";

    private static boolean isFirstRun()
    {
        boolean isFirstRun = SharedPreferenceUtil.getShareDataByKey(FIRST_RUN,
                true);
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
