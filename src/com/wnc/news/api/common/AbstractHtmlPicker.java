package com.wnc.news.api.common;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wnc.news.dao.NewsDao;
import com.wnc.news.website.WebSite;
import common.utils.JsoupHelper;

public abstract class AbstractHtmlPicker
{

    public List<NewsInfo> getAllNews(WebSite website, String team)
    {
        List<NewsInfo> list = new ArrayList<NewsInfo>();
        Document doc = null;
        for (int i = getFromPage(); i < getFromPage() + getMaxPages(); i++)
        {
            String page = "";
            try
            {
                page = String.format(website.getFormat(), team, i);
                System.out.println("分页:" + page);
                doc = JsoupHelper.getDocumentResult(page);
                if (doc != null)
                {
                    Elements news_divs = doc.select(website.getMain_div());
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

    protected abstract int getFromPage();

    protected abstract int getMaxPages();

    protected boolean hasReachOldLine(NewsInfo newsInfo)
    {
        return NewsDao.isExistUrl(newsInfo.getUrl());
    }

    protected abstract NewsInfo getNewsInfo(Element mainDiv);
}
