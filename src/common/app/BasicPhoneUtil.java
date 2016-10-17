package common.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

public class BasicPhoneUtil
{

    public static void showMediaVolume(Activity activity)
    {
        AudioManager mAudioManager = (AudioManager) activity
                .getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE, AudioManager.FX_FOCUS_NAVIGATION_UP);
    }

    public static int getScreenWidth(Activity activity)
    {
        return activity.getWindowManager().getDefaultDisplay().getWidth();
    }

    public static int getScreenHeight(Activity activity)
    {
        return activity.getWindowManager().getDefaultDisplay().getHeight();
    }

    /**
     * 判断Activity是否在最前端
     * 
     * @param activity
     * @return
     */
    public static boolean isTopActivity(Activity activity)
    {
        boolean isTop = false;
        ActivityManager am = (ActivityManager) activity
                .getSystemService(activity.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        if (cn.getClassName().contains(activity.getClass().getName()))
        {
            isTop = true;
        }
        return isTop;
    }

    /**
     * 判断wifi是否已经连接
     * 
     * @param context
     * @return
     */
    public static boolean isWifiConnect(Context context)
    {
        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected())
        {
            return true;
        }
        return false;
    }

    /**
     * 判断数据连接是否打开
     * 
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断是否能真正连接上外网，速度较慢
     * 
     * @return
     */
    public static boolean ping()
    {
        String result = null;
        try
        {
            String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址1次
            // 读取ping的内容，可以不加
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null)
            {
                stringBuffer.append(content);
            }
            Log.d("------ping-----",
                    "result content : " + stringBuffer.toString());
            // ping的状态
            int status = p.waitFor();
            if (status == 0)
            {
                result = "success";
                return true;
            }
            else
            {
                result = "failed";
            }
        }
        catch (IOException e)
        {
            result = "IOException";
        }
        catch (InterruptedException e)
        {
            result = "InterruptedException";
        }
        finally
        {
            Log.d("----result---", "result = " + result);
        }
        return false;
    }

    public static int getCurrentAPIVersion(Context context)
    {
        return Build.VERSION.SDK_INT;
    }
}
