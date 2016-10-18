package com.wnc.news.api.nba;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wnc.news.api.autocache.NewsContentService;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.api.common.TeamApi;
import com.wnc.news.dao.NewsDao;
import com.wnc.news.website.WebSite;
import com.wnc.news.website.WebSiteUtil;

public class NbaTeamApi implements TeamApi
{
    String team;
    int MAX_PAGES = 5;
    WebSite webSite = WebSiteUtil.getBasketballInsiders();

    public NbaTeamApi(String team)
    {
        this.team = team;

    }

    public void setMaxPages(int max)
    {
        if (max >= 1)
        {
            MAX_PAGES = max;
        }
    }

    @Override
    public List<NewsInfo> getAllNewsWithContent()
    {
        final NewsContentService newsContentService = new NewsContentService(
                getAllNews());
        newsContentService.execute();
        return newsContentService.getResult();
    }

    @Override
    public List<NewsInfo> getAllNews()
    {
        List<NewsInfo> list = new ArrayList<NewsInfo>();
        Document doc = null;
        for (int i = 1; i <= MAX_PAGES; i++)
        {
            try
            {
                String page = String.format(webSite.getFormat(), team, i);
                doc = common.utils.JsoupHelper.getDocumentResult(page);
                if (doc != null)
                {
                    Elements news_divs = doc.select(webSite.getMain_div());
                    for (Element mainDiv : news_divs)
                    {
                        NewsInfo newsInfo = getNewsInfo(mainDiv);
                        if (newsInfo != null)
                        {
                            if (hasReachOldLine(newsInfo))
                            {
                                // return list;
                            }
                            else
                            {
                                list.add(newsInfo);
                            }
                        }
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

    @Override
    public NewsInfo getNewsInfo(Element mainDiv)
    {
        NewsInfo newsInfo = null;
        try
        {
            newsInfo = new NewsInfo();
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
        }
        catch (Exception e)
        {
            e.printStackTrace();
            newsInfo = null;
        }
        return newsInfo;
    }

    @Override
    public boolean hasReachOldLine(NewsInfo newsInfo)
    {
        return NewsDao.isExistUrl(newsInfo.getUrl());
    }
}
