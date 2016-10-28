package com.wnc.news.api.forums;

import java.util.List;

import org.jsoup.nodes.Element;

import com.wnc.news.api.common.AbstractForumsHtmlPicker;
import com.wnc.news.api.common.ForumsApi;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.website.WebSite;
import com.wnc.news.website.WebSiteUtil;
import common.uihelper.MyAppParams;

public class RedditApi extends AbstractForumsHtmlPicker implements ForumsApi
{
    WebSite webSite;
    String type;

    public RedditApi(final String type)
    {
        this.type = type;
        if (type.equalsIgnoreCase(MyAppParams.getInstance().getBaskModelName()))
        {
            webSite = WebSiteUtil.getRedditNBA();
        }
        else
        {
            webSite = WebSiteUtil.getRedditSoccer();
        }
    }

    @Override
    protected NewsInfo getBaseNewsInfo(Element mainDiv)
    {
        NewsInfo newsInfo = null;
        try
        {
            newsInfo = new NewsInfo();
            newsInfo.setWebsite(webSite);
            newsInfo.addKeyWord(type);
            Element aElement = mainDiv.select("a").first();
            String title = aElement.text();
            newsInfo.setTitle(title);
            newsInfo.setDate(mainDiv.select("time").last().attr("datetime"));
            String commentHref = mainDiv
                    .select("a[data-event-action=comments]").first()
                    .absUrl("href");
            newsInfo.setUrl(commentHref);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            newsInfo = null;
        }
        return newsInfo;
    }

    @Override
    public String getPage(int i)
    {
        return String.format(webSite.getFormat(), i);
    }

    @Override
    public List<NewsInfo> getAllNewsWithContent()
    {
        return getAllNews(webSite);
    }

    public NewsInfo getNewsFromUrl(String url)
    {
        try
        {
            return getNewsFromUrl(webSite, url);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}