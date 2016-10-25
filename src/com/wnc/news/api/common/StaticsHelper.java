package com.wnc.news.api.common;

import android.database.sqlite.SQLiteDatabase;

import com.wnc.news.dao.NewsDao;
import com.wnc.news.website.WebSiteUtil;

public class StaticsHelper
{
    public static boolean isSoccerTeam(SQLiteDatabase db, String team)
    {
        return NewsDao.isSoccerTeam(db, team);
    }

    public static boolean isTeam(SQLiteDatabase db, String str)
    {
        return NewsDao.isTeam(db, str);
    }

    public static boolean isWebSite(SQLiteDatabase db, String str)
    {
        return WebSiteUtil.getBasketballInsiders().getName()
                .equalsIgnoreCase(str)
                || WebSiteUtil.getRealGm().getName().equalsIgnoreCase(str)
                || WebSiteUtil.getSkySports().getName().equalsIgnoreCase(str)
                || WebSiteUtil.getSquawka().getName().equalsIgnoreCase(str)
                || WebSiteUtil.getRedditNBA().getName().equalsIgnoreCase(str);
    }

    public static String getWebSiteId(SQLiteDatabase db, String str)
    {
        if (WebSiteUtil.getBasketballInsiders().getName().equalsIgnoreCase(str))
        {
            return WebSiteUtil.getBasketballInsiders().getDb_id();
        }
        if (WebSiteUtil.getRealGm().getName().equalsIgnoreCase(str))
        {
            return WebSiteUtil.getRealGm().getDb_id();
        }
        if (WebSiteUtil.getSkySports().getName().equalsIgnoreCase(str))
        {
            return WebSiteUtil.getSkySports().getDb_id();
        }
        if (WebSiteUtil.getSquawka().getName().equalsIgnoreCase(str))
        {
            return WebSiteUtil.getSquawka().getDb_id();
        }
        if (WebSiteUtil.getRedditNBA().getName().equalsIgnoreCase(str))
        {
            return WebSiteUtil.getRedditNBA().getDb_id();
        }
        return "-1";
    }
}
