package com.wnc.news.richtext;

import android.text.Html;

public class HtmlRichText implements RichText
{
	private String text;

	public HtmlRichText(String text)
	{
		this.text = text;
	}

	@Override
	public CharSequence getCharSequence()
	{
		return Html.fromHtml(this.text);
	}
}
