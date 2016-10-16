package common.app;

import common.uihelper.MyAppParams;

import android.app.Activity;

public class AppRescouceReflect
{
    /**
     * 获取某个图片的id
     * 
     * @param picname
     * @return
     */
    public static int getAppRrawbleID(String picname)
    {
        return MyAppParams
                .getInstance()
                .getResources()
                .getIdentifier(picname, "drawable",
                        MyAppParams.getInstance().getPackageName());
    }

    /**
     * 获取某个View控件ID,如文本框,按钮
     * 
     * @param rawname
     * @return
     */
    public static int getAppViewID(Activity activity, String rawname)
    {
        return MyAppParams
                .getInstance()
                .getResources()
                .getIdentifier(rawname, "id",
                        MyAppParams.getInstance().getPackageName());
    }

    /**
     * 获取界面某个控件的id
     * 
     * @param ctrlid
     * @return
     */
    public static int getAppControlID(String ctrlid)
    {
        return MyAppParams
                .getInstance()
                .getResources()
                .getIdentifier(ctrlid, "id",
                        MyAppParams.getInstance().getPackageName());
    }

}
