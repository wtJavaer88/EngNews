package com.wnc.news.api.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.database.sqlite.SQLiteDatabase;

import com.wnc.news.dao.NewsDao;
import com.wnc.news.db.DatabaseManager;
import com.wnc.news.website.WebSite;
import common.utils.JsoupHelper;

public abstract class AbstractForumsHtmlPicker
{
    SQLiteDatabase db;
    Logger log = Logger.getLogger(AbstractForumsHtmlPicker.class);

    public List<NewsInfo> getAllNews(WebSite website)
    {
        List<NewsInfo> list = new ArrayList<NewsInfo>();
        Document doc = null;
        if (db == null)
        {
            db = DatabaseManager.getInstance().openDatabase();
        }
        for (int i = getFromPage(); i < getFromPage() + getMaxPages(); i++)
        {
            String page = "";
            try
            {
                page = getPage(i);
                log.info("分页:" + page);
                doc = JsoupHelper.getDocumentResult(page);
                if (doc != null)
                {
                    Elements news_divs = doc.select(website.getMain_div());
                    for (Element mainDiv : news_divs)
                    {
                        NewsInfo newsInfo = getNewsInfo(mainDiv);
                        if (newsInfo != null)
                        {
                            list.add(newsInfo);
                        }
                    }
                }
                else
                {
                    log.error("连接" + page + "失败.");
                }
            }
            catch (Exception e)
            {
                log.error("解析网页" + page + "时出错.", e);
            }
        }
        DatabaseManager.getInstance().closeDatabase();
        return list;
    }

    public abstract String getPage(int i);

    protected abstract int getFromPage();

    protected abstract int getMaxPages();

    protected boolean hasReachOldLine(NewsInfo newsInfo)
    {
        return NewsDao.isExistUrl(db, newsInfo.getUrl());
    }

    protected abstract NewsInfo getNewsInfo(Element mainDiv);
}
