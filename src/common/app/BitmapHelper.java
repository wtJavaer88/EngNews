package common.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import common.uihelper.MyAppParams;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

public class BitmapHelper
{

    public static Bitmap getLocalBitmap(String filePath)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();

        // 获取一个缩放比例
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = (options.outWidth + 500) / 1000;
        options.inJustDecodeBounds = false;

        options.inPreferredConfig = Config.ARGB_8888;
        options.inPurgeable = true;// 允许可清除
        options.inInputShareable = true;// 以上options的两个属性必须联合使用才会有效果
        InputStream is = null;
        try
        {
            is = new FileInputStream(filePath);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(is, null, options);
        // return BitmapFactory.decodeFile(filePath, null);
    }

    public static Bitmap getAppBitmap(String picname)
    {
        return BitmapFactory.decodeResource(MyAppParams.getInstance()
                .getResources(), AppRescouceReflect.getAppRrawbleID(picname));
    }

    public static Bitmap getAppBitmap(int picId)
    {
        return BitmapFactory.decodeResource(MyAppParams.getInstance()
                .getResources(), picId);
    }

}
