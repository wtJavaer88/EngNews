package com.wnc.news.api.soccer;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wnc.basic.BasicStringUtil;
import com.wnc.string.PatternUtil;

public class SoccerTeamApi
{
	private String LATEST_SAVE_DATE;
	String team;
	final int MAX_PAGES = 5;
	final String FORMAT = "http://www.squawka.com/teams/%s/news?getitems=true&ajax=true&pg=%d";
	WebSite webSite;

	public SoccerTeamApi(String team)
	{
		this.team = team;
		webSite = new WebSite();
		webSite.setName("basketballinsiders");
		webSite.setNews_class(".entry-content p");
		webSite.setMain_div(".news-sub");
	}

	public List<NewsInfo> getAllNews()
	{
		LATEST_SAVE_DATE = getLatestSaveDate();
		List<NewsInfo> list = new ArrayList<NewsInfo>();
		Document doc = null;
		for (int i = 0; i < MAX_PAGES; i++)
		{
			try
			{
				String page = String.format(FORMAT, team, i);
				doc = JsoupHelper.getDocumentResult(page);
				if (doc != null)
				{
					Elements news_divs = doc.select(".news-sub");
					for (Element mainDiv : news_divs)
					{
						NewsInfo newsInfo = getNewsInfo(mainDiv);
						if (BasicStringUtil.isNotNullString(LATEST_SAVE_DATE) && newsInfo.getDate().compareTo(LATEST_SAVE_DATE) < 0)
						{
							return list;
						}
						list.add(newsInfo);
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}

	private String getLatestSaveDate()
	{
		// TODO 获取最近新闻日期
		return "20161001";
	}

	private NewsInfo getNewsInfo(Element mainDiv)
	{
		NewsInfo newsInfo = new NewsInfo();
		newsInfo.addKeyWord(team);
		newsInfo.setWebsite(webSite);

		Element dateDiv = mainDiv.select("div").get(1);
		if (dateDiv != null)
		{
			newsInfo.setDate(DateUtil.getDateFromEngMonth(dateDiv.text()));
		}
		Element imgDiv = mainDiv.select(".news-sub-image").first();
		if (imgDiv != null)
		{
			String url = imgDiv.absUrl("href");
			String img = imgDiv.select("img").first().absUrl("src");
			newsInfo.setUrl(url);
			newsInfo.setHead_pic(img);
		}
		Element titleDiv = mainDiv.select(".news-sub-heading").first();
		if (titleDiv != null)
		{
			String title = titleDiv.text();
			newsInfo.setTitle(title);
		}
		// news-sub-text
		Element subTextDiv = mainDiv.select(".news-sub-text").first();
		if (subTextDiv != null)
		{
			String text = PatternUtil.getFirstPatternGroup(subTextDiv.text(), "(.*?\\[…\\])");
			if (BasicStringUtil.isNullString(text))
			{
				text = subTextDiv.text();
			}
			newsInfo.setSub_text(text);
		}
		return newsInfo;
	}
}
