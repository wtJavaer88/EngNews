package com.wnc.news.richtext;

import java.net.URL;

import android.graphics.drawable.Drawable;
import android.text.Html;

public class WebImgText implements RichText
{
    private String text;

    public WebImgText(String text)
    {
        this.text = text;
    }

    @Override
    public CharSequence getCharSequence()
    {
        return Html.fromHtml(this.text, imageGetter, null);
    }

    final int maxWidth = 800;
    final int maxHeight = 640;

    final Html.ImageGetter imageGetter = new Html.ImageGetter()
    {

        @Override
        public Drawable getDrawable(String source)
        {
            System.out.println("imageGetter:" + source);
            Drawable drawable = null;
            URL url;
            try
            {
                url = new URL(source);
                drawable = Drawable.createFromStream(url.openStream(), "");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
            final int intrinsicWidth = drawable.getIntrinsicWidth();
            final int intrinsicHeight = drawable.getIntrinsicHeight();
            if (drawable != null && intrinsicWidth > 0 && intrinsicHeight > 0)
            {
                double scale = Math.min(maxWidth / intrinsicWidth, maxHeight
                        / intrinsicHeight);
                drawable.setBounds(0, 0, (int) (intrinsicWidth * scale),
                        (int) (intrinsicHeight * scale));
            }
            return drawable;
        };
    };
}
