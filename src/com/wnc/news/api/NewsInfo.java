package com.wnc.news.api;

import java.util.HashSet;
import java.util.Set;

public class NewsInfo
{
	String title;
	String url;
	String sub_text;
	String date;
	String head_pic;
	Set<String> keywords = new HashSet<String>();

	public boolean addKeyWord(String keyword)
	{
		return keywords.add(keyword);
	}

	public boolean removeKeyWord(String keyword)
	{
		return keywords.remove(keyword);
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getSub_text()
	{
		return sub_text;
	}

	public void setSub_text(String sub_text)
	{
		this.sub_text = sub_text;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public String getHead_pic()
	{
		return head_pic;
	}

	public void setHead_pic(String head_pic)
	{
		this.head_pic = head_pic;
	}

	@Override
	public String toString()
	{
		return "NewsInfo [title=" + title + ", url=" + url + ", sub_text=" + sub_text + ", date=" + date + ", head_pic=" + head_pic + ", keywords=" + keywords + "]";
	}
}
