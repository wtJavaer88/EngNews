package com.wnc.news.richtext;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.wnc.news.engnews.ui.VoaActivity;
import common.app.AppRescouceReflect;
import common.uihelper.MyAppParams;

public class ClickableVoiceRichText implements RichText
{
    private Activity activity;
    private int seektime;
    private int stopTime;

    public ClickableVoiceRichText(Activity activity, int seektime, int stopTime)
    {
        this.activity = activity;
        this.seektime = seektime;
        this.stopTime = stopTime;
    }

    @SuppressWarnings("deprecation")
    @Override
    public CharSequence getCharSequence()
    {
        Bitmap b = BitmapFactory.decodeResource(MyAppParams.getInstance()
                .getResources(), AppRescouceReflect
                .getAppRrawbleID("icon_play_voice"));
        if (b == null)
        {
            Log.i("linkpic", "文件不存在");
            return "";
        }
        Drawable drawable = new BitmapDrawable(b);
        ImageSpan imgSpan = new ImageSpan(drawable);
        SpannableString spanString = new SpannableString("icon");
        spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    class Clickable extends ClickableSpan implements OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            if (activity instanceof VoaActivity)
            {
                ((VoaActivity) activity).play(seektime, stopTime);
            }
        }
    }

}
