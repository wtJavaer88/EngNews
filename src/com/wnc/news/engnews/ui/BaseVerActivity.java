package com.wnc.news.engnews.ui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

public class BaseVerActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    // 重载此方法即可避免横竖屏切换时的数据丢失问题
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            // land
            System.out.println("ORIENTATION_LANDSCAPE");

        }
        else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            // port
            System.out.println("ORIENTATION_PORTRAIT");
        }
    }

}
