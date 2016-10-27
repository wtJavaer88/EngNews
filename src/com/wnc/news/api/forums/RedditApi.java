package com.wnc.news.api.forums;

import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wnc.news.api.common.AbstractForumsHtmlPicker;
import com.wnc.news.api.common.ForumsApi;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.website.WebSite;
import com.wnc.news.website.WebSiteUtil;
import common.uihelper.MyAppParams;
import common.utils.JsoupHelper;

public class RedditApi implements ForumsApi
{
    int MAX_PAGES = 2;
    WebSite webSite;
    AbstractForumsHtmlPicker htmlPicker;

    public RedditApi(final String type)
    {
        if (type.equalsIgnoreCase(MyAppParams.getInstance().getBaskModelName()))
        {
            webSite = WebSiteUtil.getRedditNBA();
        }
        else
        {
            webSite = WebSiteUtil.getRedditSoccer();
        }

        htmlPicker = new AbstractForumsHtmlPicker()
        {
            @Override
            protected int getMaxPages()
            {
                return MAX_PAGES;
            }

            @Override
            protected int getFromPage()
            {
                return 1;
            }

            @Override
            protected NewsInfo getNewsInfo(Element mainDiv)
            {
                NewsInfo newsInfo = null;
                try
                {
                    newsInfo = new NewsInfo();
                    newsInfo.setWebsite(webSite);
                    newsInfo.addKeyWord(type);
                    Element dateDiv = mainDiv.select(".responsive-hide")
                            .first();

                    Element aElement = mainDiv.select("a").first();
                    String title = aElement.text();
                    newsInfo.setTitle(title);
                    newsInfo.setDate(mainDiv.select("time").last()
                            .attr("datetime"));
                    String commentHref = mainDiv
                            .select("a[data-event-action=comments]").first()
                            .absUrl("href");
                    newsInfo.setUrl(commentHref);

                    Document documentResult = JsoupHelper
                            .getDocumentResult(commentHref);
                    Elements select = documentResult.select(webSite
                            .getNews_class());
                    newsInfo.setComment_counts(select.size());
                    String content = "";
                    for (Element element : select)
                    {
                        content += element.toString();
                        content += "</br>----------------------------------------</br>";
                    }
                    newsInfo.setHtml_content(content);
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

        };
    }

    @Override
    public void setMaxPages(int max)
    {
        if (max >= 1)
        {
            MAX_PAGES = max;
        }
    }

    @Override
    public List<NewsInfo> getAll()
    {
        return htmlPicker.getAllNews(webSite);
    }

    public NewsInfo getNewsFromUrl(String url)
    {
        return htmlPicker.getNewsFromUrl(webSite, url);
    }
}
