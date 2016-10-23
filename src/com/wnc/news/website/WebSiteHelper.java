package com.wnc.news.website;

public class WebSiteHelper
{
	public static String getPage(WebSite website, int page)
	{
		if (website.getName().contains("realgm"))
		{
			return String.format(website.getFormat(), page * 25);
		}
		return "";
	}
}
