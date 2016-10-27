package com.wnc.news.richtext;

import android.app.Activity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;

import com.wnc.news.engnews.ui.VoaActivity;

public class ClickableRichText implements RichText
{
    private Activity activity;
    private String text;
    private int seektime;
    private int stopTime;

    public ClickableRichText(String text, Activity activity, int seektime,
            int stopTime)
    {
        this.activity = activity;
        this.text = text;
        this.seektime = seektime;
        this.stopTime = stopTime;
    }

    @Override
    public CharSequence getCharSequence()
    {
        SpannableString spanableInfo = new SpannableString(this.text);
        spanableInfo.setSpan(new Clickable(), 0, this.text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanableInfo;
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
