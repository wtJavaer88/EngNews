package common.uihelper;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Environment;

import com.wnc.basic.BasicFileUtil;

public class MyAppParams
{
    private String packageName;
    private Resources resources;
    private String appPath;

    private final static String workPath = Environment
            .getExternalStorageDirectory().getPath() + "/wnc/app/srtlearn/";
    private String localLogPath;
    public final static String SWF_FOLDER = Environment
            .getExternalStorageDirectory().getPath() + "/wnc/res/swf/";
    public static final String SRT_FOLDER = Environment
            .getExternalStorageDirectory().getPath() + "/wnc/res/srt/";
    public final static String VIDEO_FOLDER = Environment
            .getExternalStorageDirectory().getPath() + "/wnc/res/video/";
    public final static String THUMB_PICFOLDER = Environment
            .getExternalStorageDirectory().getPath() + "/wnc/res/srtpic/";
    public final static String FAVORITE_TXT = workPath + "favorite.txt";
    public final static String SRT_DB = workPath + "srt.db";
    public final static String DICTIONARY_DB = workPath + "dictionary.db";
    public final static String FAVORITE_DB = workPath + "favorite.db";

    public static Activity mainActivity;

    private String backupDbPath;

    private String zipPath;

    private static int screenWidth;
    private static int screenHeight;

    private static MyAppParams singletonMyAppParams = new MyAppParams();

    private MyAppParams()
    {
        this.localLogPath = this.workPath + "log/";
        this.backupDbPath = this.workPath + "backupdb/";

        this.zipPath = this.workPath + "zip/";

        BasicFileUtil.makeDirectory(this.localLogPath);
        BasicFileUtil.makeDirectory(this.backupDbPath);
        BasicFileUtil.makeDirectory(SWF_FOLDER);
        BasicFileUtil.makeDirectory(SRT_FOLDER);
        BasicFileUtil.makeDirectory(THUMB_PICFOLDER);
        BasicFileUtil.makeDirectory(this.zipPath);
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

    public String getLocalLogPath()
    {
        return this.localLogPath;
    }

    public String getAppPath()
    {
        return this.appPath;
    }

}
