package com.wnc.news.api.soccer;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wnc.basic.BasicStringUtil;
import com.wnc.news.api.common.DateUtil;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.api.common.TeamApi;
import com.wnc.news.website.WebSite;
import com.wnc.news.website.WebSiteUtil;
import com.wnc.string.PatternUtil;
import common.utils.JsoupHelper;

public class SquawkaTeamApi implements TeamApi
{
    private String LATEST_SAVE_DATE;
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
        final List<NewsInfo> allNews = getAllNews();
        Document doc;
        for (NewsInfo info : allNews)
        {
            try
            {
                doc = JsoupHelper.getDocumentResult(info.getUrl());
                final Elements contents = doc.select(info.getWebsite()
                        .getNews_class());
                if (contents != null)
                {
                    info.setHtml_content(contents.toString());
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return allNews;
    }

    @Override
    public List<NewsInfo> getAllNews()
    {
        LATEST_SAVE_DATE = getLatestSaveDate();
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
                        if (BasicStringUtil.isNotNullString(LATEST_SAVE_DATE)
                                && newsInfo.getDate().compareTo(
                                        LATEST_SAVE_DATE) < 0)
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

    @Override
    public NewsInfo getNewsInfo(Element mainDiv)
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
            String text = PatternUtil.getFirstPatternGroup(subTextDiv.text(),
                    "(.*?\\[…\\])");
            if (BasicStringUtil.isNullString(text))
            {
                text = subTextDiv.text();
            }
            newsInfo.setSub_text(text);
        }
        return newsInfo;
    }

    @Override
    public boolean hasReachOldLine()
    {
        // TODO Auto-generated method stub
        return false;
    }

}
