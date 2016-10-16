package common.app;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ToastUtil
{
    static Toast toast = null;

    public static void showShortToast(Context context, Object toastMsg)
    {
        cancel();
        if (valid(context, toastMsg))
        {
            toast = Toast.makeText(context, toastMsg.toString(),
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public static void showLongToast(Context context, Object toastMsg)
    {
        cancel();
        if (valid(context, toastMsg))
        {
            toast = Toast.makeText(context, toastMsg.toString(),
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public static void cancel()
    {
        if (toast != null)
        {
            toast.cancel();
        }
    }

    private static boolean valid(Context context, Object toastMsg)
    {
        Log.i("TOAST", "info:" + toastMsg);
        if (context == null)
        {
            Log.e("TOAST", "请不要传NULL的context");
            return false;
        }
        else if (toastMsg == null)
        {
            Toast.makeText(context, "请不要传NULL的msg", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (context instanceof Activity
                && !BasicPhoneUtil.isTopActivity((Activity) context))
        {
            return false;
        }
        return true;
    }

}
