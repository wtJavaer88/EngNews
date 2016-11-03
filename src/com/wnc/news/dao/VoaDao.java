package com.wnc.news.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import voa.VoaNewsInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wnc.basic.BasicDateUtil;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.db.DatabaseManager_VOA;

public class VoaDao
{
    static Logger log = Logger.getLogger(VoaDao.class);
    static SQLiteDatabase database;

    public static void openDatabase()
    {
        database = DatabaseManager_VOA.getInstance().openDatabase();
    }

    public static void closeDatabase()
    {
        DatabaseManager_VOA.getInstance().closeDatabase();
    }

    public static boolean isExistUrl(SQLiteDatabase db, String url)
    {
        boolean flag = false;
        try
        {
            String sql = "select * from article where url='"
                    + StringEscapeUtils.escapeSql(url) + "'";
            Cursor c = db.rawQuery(sql, null);
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
            log.error(url, e);
        }
        return flag;
    }

    public synchronized static void insertVOANews(List<VoaNewsInfo> news)
    {
        try
        {
            openDatabase();
            for (VoaNewsInfo newsInfo : news)
            {
                insertSingleVOANews(database, newsInfo);
            }
        }
        catch (Exception e)
        {
            log.error("新闻总条数:" + news.size(), e);
        }
        finally
        {
            closeDatabase();
        }
    }

    public synchronized static void insertSingleVOANews(SQLiteDatabase db,
            VoaNewsInfo newsInfo)
    {
        try
        {
            db.execSQL(
                    "INSERT INTO Article(title,url,date,img,content_json,create_time,mp3) VALUES (?,?,?,?,?,?,?)",
                    new Object[]
                    { newsInfo.getTitle(), newsInfo.getUrl(),
                            newsInfo.getDate(), newsInfo.getHead_pic(),
                            newsInfo.getHtml_content(),
                            BasicDateUtil.getCurrentDateTimeString(),
                            newsInfo.getMp3() });
        }
        catch (Exception e)
        {
            log.error(newsInfo.getUrl(), e);
        }
    }

    public static VoaNewsInfo findtest()
    {
        List<VoaNewsInfo> findAllNewsBySql = findAllNewsBySql("SELECT * FROM ARTICLE ORDER BY DATE DESC LIMIT 0,1 ");
        if (findAllNewsBySql.size() > 0)
        {
            return findAllNewsBySql.get(0);
        }
        return null;
    }

    private synchronized static List<VoaNewsInfo> findAllNewsBySql(String sql)
    {
        List<VoaNewsInfo> list = new ArrayList<VoaNewsInfo>();
        try
        {
            openDatabase();
            Cursor c = database.rawQuery(sql, null);
            c.moveToFirst();
            VoaNewsInfo info;
            while (!c.isAfterLast())
            {
                info = new VoaNewsInfo();
                info.setHtml_content(c.getString(c
                        .getColumnIndex("content_json")));
                // info.setCet_topics(c.getString(c.getColumnIndex("cet_topics")));
                info.setHead_pic(c.getString(c.getColumnIndex("img")));
                // info.setSub_text(c.getString(c.getColumnIndex("sub_text")));
                info.setTitle(c.getString(c.getColumnIndex("title")));
                info.setDate(c.getString(c.getColumnIndex("date")));
                info.setDb_id(c.getInt(c.getColumnIndex("id")));
                info.setUrl(c.getString(c.getColumnIndex("url")));
                info.setCreate_time(c.getString(c.getColumnIndex("create_time")));
                info.setMp3(c.getString(c.getColumnIndex("mp3")));
                // info.setTopic_counts(c.getInt(c.getColumnIndex("topic_counts")));
                // info.setComment_counts(c.getInt(c
                // .getColumnIndex("comment_counts")));
                // String kw = c.getString(c.getColumnIndex("keywords"));
                list.add(info);
                c.moveToNext();
            }
        }
        catch (Exception e)
        {
            log.error("findall", e);
        }
        finally
        {
            closeDatabase();
        }
        return list;
    }

    public static List<NewsInfo> findAllNewsInfos(int from, int counts)
    {
        List<VoaNewsInfo> findAllNewsBySql = findAllNewsBySql("SELECT * FROM ARTICLE ORDER BY DATE DESC LIMIT "
                + from + "," + counts);
        return new ArrayList<NewsInfo>(findAllNewsBySql);
    }

}
