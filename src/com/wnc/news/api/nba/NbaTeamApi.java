package com.wnc.news.api.nba;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wnc.news.api.soccer.JsoupHelper;
import com.wnc.news.api.soccer.NewsInfo;
import com.wnc.news.api.soccer.WebSite;

public class NbaTeamApi
{
	String team;
	final int MAX_PAGES = 5;
	final String FORMAT = "http://www.basketballinsiders.com/tag/%s/page/%d/";
	WebSite webSite;

	public NbaTeamApi(String team)
	{
		this.team = team;
		webSite = new WebSite();
		webSite.setName("basketballinsiders");
		webSite.setNews_class("#content-area p");
		webSite.setMain_div(".infinite-post");
	}

	public List<NewsInfo> getAllNews()
	{
		List<NewsInfo> list = new ArrayList<NewsInfo>();
		Document doc = null;
		for (int i = 1; i <= MAX_PAGES; i++)
		{
			try
			{
				String page = String.format(FORMAT, team, i);
				doc = JsoupHelper.getDocumentResult(page);
				if (doc != null)
				{
					Elements news_divs = doc.select(webSite.getMain_div());
					for (Element mainDiv : news_divs)
					{
						NewsInfo newsInfo = getNewsInfo(mainDiv);
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

	private NewsInfo getNewsInfo(Element mainDiv)
	{
		NewsInfo newsInfo = new NewsInfo();
		newsInfo.addKeyWord(team);
		newsInfo.setWebsite(webSite);

		Element imgDiv = mainDiv.select(".blog-layout1-img").first();
		if (imgDiv != null)
		{
			String url = imgDiv.select("a").first().absUrl("href");
			String img = imgDiv.select("img").first().absUrl("src");
			newsInfo.setUrl(url);
			newsInfo.setHead_pic(img);
		}
		Element titleDiv = mainDiv.select(".blog-layout1-text").first();
		if (titleDiv != null)
		{
			String title = titleDiv.select("a").first().text();
			newsInfo.setTitle(title);
			newsInfo.setSub_text(titleDiv.select("p").first().text());
		}
		return newsInfo;
	}
}
