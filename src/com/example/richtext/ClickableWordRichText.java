package com.example.richtext;

import android.app.Activity;
import android.text.SpannableString;
import android.text.Spanned;

public class ClickableWordRichText implements RichText
{
    private String word;
    private Activity activity;

    public ClickableWordRichText(Activity activity, String text)
    {
        this.activity = activity;
        this.word = text;
    }

    @Override
    public CharSequence getCharSequence()
    {
        SpannableString spanableInfo = new SpannableString(word);
        spanableInfo.setSpan(new Clickable(activity, word), 0, word.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanableInfo;
    }

}
