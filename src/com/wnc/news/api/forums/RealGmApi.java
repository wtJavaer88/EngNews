package com.wnc.news.api.forums;

import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wnc.news.api.common.AbstractForumsHtmlPicker;
import com.wnc.news.api.common.DateUtil;
import com.wnc.news.api.common.ForumsApi;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.website.WebSite;
import com.wnc.news.website.WebSiteUtil;
import common.utils.JsoupHelper;

public class RealGmApi implements ForumsApi
{
    int MAX_PAGES = 2;
    WebSite webSite = WebSiteUtil.getRealGm();
    AbstractForumsHtmlPicker htmlPicker;

    public RealGmApi()
    {
        htmlPicker = new AbstractForumsHtmlPicker()
        {

            @Override
            protected int getMaxPages()
            {
                // TODO Auto-generated method stub
                return MAX_PAGES;
            }

            @Override
            protected int getFromPage()
            {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            protected NewsInfo getNewsInfo(Element mainDiv)
            {
                NewsInfo newsInfo = null;
                try
                {
                    newsInfo = new NewsInfo();
                    newsInfo.setWebsite(webSite);

                    Element dateDiv = mainDiv.select(".responsive-hide")
                            .first();
                    newsInfo.setDate(DateUtil.getDateFromRealGame(dateDiv
                            .text()));
                    Element titleDiv = mainDiv.select(".list-inner a").first();
                    if (titleDiv != null)
                    {
                        String title = titleDiv.text();
                        String url = titleDiv.absUrl("href");
                        newsInfo.setTitle(title);
                        newsInfo.setUrl(url);
                        Document documentResult = JsoupHelper
                                .getDocumentResult(url);
                        System.out.println(url + " " + newsInfo.getDate());
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
                        System.out.println("内容长度:" + content.length());
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
            public String getPage(int i)
            {
                return String.format(webSite.getFormat(), i * 25);
            }

        };
    }

    @Override
    public void setMaxPages(int max)
    {
        if (max >= 0)
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
