package com.wnc.news.richtext;

import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;
import android.view.View;

public abstract class ClickableImageSpan extends ImageSpan
{
    public ClickableImageSpan(Drawable b)
    {
        super(b);
    }

    public abstract void onClick(View view);
}