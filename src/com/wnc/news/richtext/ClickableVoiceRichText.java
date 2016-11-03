package com.wnc.news.richtext;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;

import com.wnc.news.engnews.ui.VoaActivity;
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

    @Override
    public CharSequence getCharSequence()
    {
        final Resources resources = MyAppParams.getInstance().getResources();
        int pic_id = resources.getIdentifier("icon_play_dialog", "drawable",
                MyAppParams.getInstance().getPackageName());
        Drawable drawable = resources.getDrawable(pic_id);
        // System.out.println(drawable.getIntrinsicWidth());
        drawable.setBounds(0, 0, 80, 80);
        ImageSpan imgSpan = new ClickableImageSpan(drawable)
        {

            @Override
            public void onClick(View view)
            {
                ((VoaActivity) activity).play(seektime, stopTime);
            }
        };
        SpannableString spanString = new SpannableString("icon");
        spanString.setSpan(imgSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

}
