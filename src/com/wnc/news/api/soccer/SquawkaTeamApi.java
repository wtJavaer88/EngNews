package com.wnc.news.api.soccer;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wnc.basic.BasicStringUtil;
import com.wnc.news.api.autocache.NewsContentService;
import com.wnc.news.api.common.DateUtil;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.api.common.TeamApi;
import com.wnc.news.dao.NewsDao;
import com.wnc.news.website.WebSite;
import com.wnc.news.website.WebSiteUtil;
import com.wnc.string.PatternUtil;
import common.utils.JsoupHelper;

public class SquawkaTeamApi implements TeamApi
{
    String team;
    int MAX_PAGES = 1;
    WebSite webSite = WebSiteUtil.getSquawka();

    public SquawkaTeamApi(String team)
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
        for (int i = 0; i < MAX_PAGES; i++)
        {
            try
            {
                String page = String.format(webSite.getFormat(), team, i);
                doc = JsoupHelper.getDocumentResult(page);
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
        NewsInfo newsInfo = new NewsInfo();
        newsInfo.addKeyWord(team);
        newsInfo.setWebsite(webSite);

        try
        {
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
                String text = PatternUtil.getFirstPatternGroup(
                        subTextDiv.text(), "(.*?\\[…\\])");
                if (BasicStringUtil.isNullString(text))
                {
                    text = subTextDiv.text();
                }
                newsInfo.setSub_text(text);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return newsInfo;
    }

    @Override
    public boolean hasReachOldLine(NewsInfo newsInfo)
    {
        return NewsDao.isExistUrl(newsInfo.getUrl());
    }

}
