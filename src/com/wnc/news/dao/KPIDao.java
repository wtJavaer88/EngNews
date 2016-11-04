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
            db.execSQL("INSERT INTO NEWS_KPI(date,update_time) VALUES ('"
                    + today + "','" + BasicDateUtil.getCurrentDateTimeString()
                    + "')");
        }
        catch (Exception e)
        {
            log.error(today, e);
        }
    }

    public static void insertViewHistory(SQLiteDatabase db, int news_id,
            int topic_counts, int times)
    {
        try
        {
            db.execSQL("INSERT INTO VIEW_HISTORY(news_id,topic_counts,view_duration,create_time) VALUES ("
                    + news_id
                    + ","
                    + topic_counts
                    + ","
                    + times
                    + ",'"
                    + BasicDateUtil.getCurrentDateTimeString() + "')");
        }
        catch (Exception e)
        {
            log.error(news_id, e);
        }
    }

    public static boolean increaseViewed(SQLiteDatabase db, String today,
            int topic_counts, int times, int selected_count)
    {
        try
        {
            db.execSQL("UPDATE NEWS_KPI SET view_news=view_news+1,topic_counts=topic_counts+"
                    + topic_counts
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
        return findBysql(db, "SELECT * FROM NEWS_KPI ORDER BY DATE DESC");
    }

    public static KPIData findByDay(SQLiteDatabase db, String day)
    {
        final List<KPIData> findBysql = findBysql(db,
                "SELECT * FROM NEWS_KPI WHERE DATE='" + day + "'");
        if (findBysql.size() > 0)
        {
            return findBysql.get(0);
        }
        return null;
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
                info.setDate(c.getString(c.getColumnIndex("date")));
                info.setViewed_news(c.getInt(c.getColumnIndex("view_news")));
                info.setLoved_news(c.getInt(c.getColumnIndex("fav_news")));
                info.setTopicWords(c.getInt(c.getColumnIndex("topic_counts")));
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

    public static boolean addLovedNews(SQLiteDatabase db, int news_id,
            String today)
    {
        try
        {
            db.execSQL("INSERT INTO fav_news(news_id,create_time) VALUES ("
                    + news_id + ",'" + BasicDateUtil.getCurrentDateTimeString()
                    + "')");
            db.execSQL("UPDATE NEWS_KPI SET fav_news=fav_news+1 WHERE DATE='"
                    + today + "'");

        }
        catch (Exception e)
        {
            log.error(news_id, e);
            return false;
        }
        return true;
    }

    public static boolean addSelectedWord(SQLiteDatabase db, int news_id,
            String word)
    {
        if (isExistSelected(db, news_id, word))
        {
            return true;
        }
        try
        {
            db.execSQL("INSERT INTO select_word_save(news_id,word,create_time) VALUES ("
                    + news_id
                    + ",'"
                    + word
                    + "','"
                    + BasicDateUtil.getCurrentDateTimeString() + "')");
        }
        catch (Exception e)
        {
            log.error(news_id, e);
            return false;
        }
        return true;
    }

    private static boolean isExistSelected(SQLiteDatabase db, int news_id,
            String word)
    {
        boolean flag = false;
        try
        {
            String sql = "select * from select_word_save where news_id="
                    + news_id + " and word='" + word + "'";
            return db.rawQuery(sql, null).getCount() > 0;
        }
        catch (Exception e)
        {
            log.error(word, e);
        }
        return flag;
    }

    public static boolean hasViewed(SQLiteDatabase db, int news_id)
    {
        try
        {
            String sql = "select * from view_history where news_id=" + news_id;
            return db.rawQuery(sql, null).getCount() > 0;
        }
        catch (Exception e)
        {
            log.error(news_id, e);
        }
        return false;
    }

    public static List<String> getLatelyWords(SQLiteDatabase db)
    {
        List<String> list = new ArrayList<String>();
        try
        {
            Cursor c = db
                    .rawQuery(
                            "SELECT DISTINCT word FROM SELECT_WORD_SAVE ORDER BY CREATE_TIME DESC LIMIT 0, 200",
                            null);
            c.moveToFirst();
            while (!c.isAfterLast())
            {
                list.add(c.getString(c.getColumnIndex("word")));
                c.moveToNext();
            }
        }
        catch (Exception e)
        {
            log.error("findall", e);
        }
        return list;
    }
}
