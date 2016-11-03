package com.wnc.news.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wnc.basic.BasicDateUtil;
import com.wnc.news.engnews.kpi.KPIData;

public class KPIDao
{
    static Logger log = Logger.getLogger(KPIDao.class);

    public static boolean isExistDay(SQLiteDatabase db, String today)
    {
        boolean flag = false;
        try
        {
            String sql = "select * from news_kpi where date='"
                    + StringEscapeUtils.escapeSql(today) + "'";
            return db.rawQuery(sql, null).getCount() > 0;
        }
        catch (Exception e)
        {
            log.error(today, e);
        }
        return flag;
    }

    public static void insertRecordByday(SQLiteDatabase db, String today)
    {
        try
        {
            db.execSQL("INSERT INTO news_api(date,update_time) VALUES ('"
                    + today + "'," + BasicDateUtil.getCurrentDateTimeString()
                    + ")");
        }
        catch (Exception e)
        {
            log.error(today, e);
        }
    }

    public static boolean increaseViewed(SQLiteDatabase db, String today,
            int hlCounts, int times, int selected_count)
    {
        try
        {
            db.execSQL("UPDATE NEWS_API SET view_news=view_news+1,topic_counts+="
                    + hlCounts
                    + ",durations=durations+"
                    + times
                    + ",selected_words=selected_words+"
                    + selected_count
                    + ",update_time='"
                    + BasicDateUtil.getCurrentDateTimeString()
                    + "' WHERE DATE='" + today + "'");
        }
        catch (Exception e)
        {
            log.error(today, e);
            return false;
        }
        return true;
    }

    public static List<KPIData> getAllHistory(SQLiteDatabase db)
    {
        return findBysql(db, "SELECT * FROM NEWS_API ORDER BY DATE DESC");
    }

    public static KPIData findByDay(SQLiteDatabase db, String day)
    {
        return findBysql(db, "SELECT * FROM NEWS_API WHERE DATE='" + day + "'")
                .get(0);
    }

    private static List<KPIData> findBysql(SQLiteDatabase db, String sql)
    {
        List<KPIData> list = new ArrayList<KPIData>();
        try
        {
            Cursor c = db.rawQuery(sql, null);
            c.moveToFirst();
            KPIData info;
            while (!c.isAfterLast())
            {
                info = new KPIData();
                info.setDate(c.getString(c.getColumnIndex("content_json")));
                info.setViewed_news(c.getInt(c.getColumnIndex("view_news")));
                info.setLoved_news(c.getInt(c.getColumnIndex("fav_news")));
                info.setHighlightWords(c.getInt(c
                        .getColumnIndex("topic_counts")));
                info.setTimes(c.getInt(c.getColumnIndex("durations")));
                info.setSelectedWords(c.getInt(c
                        .getColumnIndex("selected_words")));
                list.add(info);
                c.moveToNext();
            }
        }
        catch (Exception e)
        {
            log.error("findall", e);
        }
        return list;

    }

    public static boolean addLovedNews(SQLiteDatabase db, int news_id)
    {
        try
        {
            db.execSQL("INSERT INTO fav_news(news_id,create_time) VALUES ("
                    + news_id + ",'" + BasicDateUtil.getCurrentDateTimeString()
                    + "')");
        }
        catch (Exception e)
        {
            log.error(news_id, e);
            return false;
        }
        return true;
    }
}
