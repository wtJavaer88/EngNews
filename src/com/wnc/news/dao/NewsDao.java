package com.wnc.news.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wnc.news.api.common.Club;
import com.wnc.news.api.common.NewsInfo;
import common.uihelper.MyAppParams;

public class NewsDao
{
    static SQLiteDatabase database;

    public static void openDatabase()
    {
        if (isConnect())
        {
            return;
        }
        try
        {
            String databaseFilename = MyAppParams.NEWS_DB;
            database = SQLiteDatabase.openOrCreateDatabase(databaseFilename,
                    null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void closeDatabase()
    {
        if (database != null)
        {
            database.close();
            database = null;
        }
    }

    public static boolean isConnect()
    {
        return database != null && database.isOpen();
    }

    public synchronized static void deleteAllNews()
    {
        try
        {
            openDatabase();
            database.delete("news", null, null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeDatabase();
        }
    }

    public synchronized static void updateContentAndTopic(String url,
            String newContent, String cetTopics)
    {
        try
        {
            openDatabase();
            ContentValues cv = new ContentValues();
            cv.put("html_content", newContent);
            cv.put("cet_topics", cetTopics);
            if (database.update("news", cv, "url = ?", new String[]
            { url }) == 1)
            {
                System.out.println("成功更新");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeDatabase();
        }
    }

    public synchronized static void insertNews(List<NewsInfo> news)
    {
        try
        {
            openDatabase();
            for (NewsInfo newsInfo : news)
            {
                try
                {
                    database.execSQL(
                            "INSERT INTO NEWS(title,url,sub_text,date,head_pic,keywords,html_content,website_id) VALUES (?,?,?,?,?,?,?,?)",
                            new Object[]
                            { newsInfo.getTitle(), newsInfo.getUrl(),
                                    newsInfo.getSub_text(), newsInfo.getDate(),
                                    newsInfo.getHead_pic(),
                                    newsInfo.getKeywords(),
                                    newsInfo.getHtml_content(),
                                    newsInfo.getWebsite().getDb_id() });
                }
                catch (Exception e)
                {
                    System.out.println(newsInfo.getUrl() + "插入异常");
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeDatabase();
        }
    }

    public synchronized static void insertClubs(List<Club> clubs)
    {
        try
        {
            openDatabase();
            for (Club club : clubs)
            {
                try
                {
                    database.execSQL(
                            "INSERT INTO CLUB(league,full_name,short_name,abbreviation,cn_name,club_stats_url,photo) VALUES (?,?,?,?,?,?,?)",
                            new Object[]
                            { club.getLeague(), club.getFull_name(),
                                    club.getShort_name(),
                                    club.getAbbreviation(), club.getCn_name(),
                                    club.getClub_stats_url(), club.getPhoto() });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeDatabase();
        }
    }

    public static boolean isExistUrl(String url)
    {
        boolean flag = false;
        try
        {
            openDatabase();
            String sql = "select * from news where url='" + url
                    + "' order by date desc";
            Cursor c = database.rawQuery(sql, null);
            c.moveToFirst();
            while (!c.isAfterLast())
            {
                System.out.println("find url:" + url);
                flag = true;
                break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeDatabase();
        }
        return flag;
    }

    public static boolean isSoccerTeam(String team)
    {
        boolean flag = false;
        try
        {
            openDatabase();
            String sql = "select * from club where full_name like '%" + team
                    + "%' and league in(641,682,712,717)";
            Cursor c = database.rawQuery(sql, null);
            c.moveToFirst();
            while (!c.isAfterLast())
            {
                flag = true;
                break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeDatabase();
        }
        return flag;
    }

    public static List<NewsInfo> findAllNBANews()
    {
        return findAllNews("basketballinsiders");
    }

    public static List<NewsInfo> findAllSoccerNews()
    {
        return findAllNews("squawka,skysports");
    }

    public static List<NewsInfo> findAllNews(String url_filter)
    {
        List<NewsInfo> list = new ArrayList<NewsInfo>();
        try
        {
            openDatabase();
            String[] keys = url_filter.split(",");
            String f = "";
            for (String s : keys)
            {
                f += " or url like '%" + s + "%'";
            }
            String sql = "select * from news where 1=2 " + f
                    + " order by date desc";
            Cursor c = database.rawQuery(sql, null);
            c.moveToFirst();
            NewsInfo info = new NewsInfo();
            while (!c.isAfterLast())
            {
                info = new NewsInfo();
                info.setHtml_content(c.getString(c
                        .getColumnIndex("html_content")));
                info.setCet_topics(c.getString(c.getColumnIndex("cet_topics")));
                info.setHead_pic(c.getString(c.getColumnIndex("head_pic")));
                info.setSub_text(c.getString(c.getColumnIndex("sub_text")));
                info.setTitle(c.getString(c.getColumnIndex("title")));
                info.setDate(c.getString(c.getColumnIndex("date")));
                info.setDb_id(c.getString(c.getColumnIndex("id")));
                list.add(info);
                c.moveToNext();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeDatabase();
        }
        return list;
    }
}
