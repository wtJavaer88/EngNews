package com.wnc.news.richtext;

import java.net.URL;

import android.graphics.drawable.Drawable;
import android.text.Html;

import common.app.BasicPhoneUtil;
import common.uihelper.MyAppParams;

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
    int maxHeight = 500;

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
                if (BasicPhoneUtil
                        .isWifiConnect(MyAppParams.getInstance().mainActivity)
                        && BasicPhoneUtil.ping())
                {
                    url = new URL(source);
                    drawable = Drawable.createFromStream(url.openStream(), "");
                    if (drawable != null)
                    {
                        final int intrinsicWidth = drawable.getIntrinsicWidth();
                        final int intrinsicHeight = drawable
                                .getIntrinsicHeight();
                        if (intrinsicWidth > 0 && intrinsicHeight > 0)
                        {
                            double scale = Math.min(maxWidth / intrinsicWidth,
                                    maxHeight / intrinsicHeight);
                            drawable.setBounds(0, 0,
                                    (int) (intrinsicWidth * scale),
                                    (int) (intrinsicHeight * scale));
                        }
                    }
                }
                else
                {
                    // drawable = Drawable.createFromStream(
                    // MyAppParams.getInstance().getResources()
                    // .openRawResource(R.drawable.icon_news_bg2),
                    // "");
                    // drawable.setBounds(0, 0, maxWidth, maxWidth / 2);
                }
            }
            catch (Exception e)
            {
                System.out.println("not found " + source);
                // drawable = Drawable
                // .createFromStream(
                // MyAppParams
                // .getInstance()
                // .getResources()
                // .openRawResource(
                // R.drawable.icon_not_found), "");
                // drawable.setBounds(0, 0, maxWidth, maxWidth / 2);

            }

            return drawable;
        };
    };
}
