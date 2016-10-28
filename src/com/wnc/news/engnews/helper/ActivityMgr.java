package com.wnc.news.engnews.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ActivityMgr
{
    public static void gotoIE(Context activity, String page)
    {
        try
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(page));
            activity.startActivity(intent);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
