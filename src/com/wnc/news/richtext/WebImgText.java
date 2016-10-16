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

	final Html.ImageGetter imageGetter = new Html.ImageGetter()
	{

		@Override
		public Drawable getDrawable(String source)
		{
			System.out.println(source);
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
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			return drawable;
		};
	};
}
