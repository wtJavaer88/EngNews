package com.wnc.news.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wnc.basic.BasicDateUtil;
import com.wnc.news.api.common.Club;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.api.common.StaticsHelper;
import com.wnc.news.db.DatabaseManager;
import common.uihelper.MyAppParams;

public class NewsDao
{
    static Logger log = Logger.getLogger(NewsDao.class);
    static SQLiteDatabase database;

    public static void openDatabase()
    {
        database = DatabaseManager.getInstance().openDatabase();
    }

    public static void closeDatabase()
    {
        DatabaseManager.getInstance().closeDatabase();
    }

    public synchronized static void deleteTestNews()
    {
        try
        {
            openDatabase();
            int delete = database.delete("news", "date = ?", new String[]
            { "" });
            log.info("删除条数:" + delete);
        }
        catch (Exception e)
        {
            log.error("", e);

        }
        finally
        {
            closeDatabase();
        }
    }

    public synchronized static void deleteAllNews()
    {
        try
        {
            openDatabase();
            int delete = database.delete("news", null, null);
            log.info("删除条数:" + delete);
        }
        catch (Exception e)
        {
            log.error("", e);

        }
        finally
        {
            closeDatabase();
        }
    }

    public synchronized static void updateContentAndTopic(String url,
            String newContent, String cetTopics, int topic_counts,
            int comment_counts)
    {
        try
        {
            openDatabase();
            ContentValues cv = new ContentValues();
            cv.put("html_content", newContent);
            cv.put("cet_topics", cetTopics);
            cv.put("topic_counts", topic_counts);
            cv.put("comment_counts", comment_counts);
            final int updateCounts = database.update("news", cv, "url = ?",
                    new String[]
                    { url });
            if (updateCounts == 1)
            {
                log.info("成功更新");
            }
            else
            {
                log.info("成功失败,更新条数:" + updateCounts);
            }
        }
        catch (Exception e)
        {
            log.error(url, e);

        }
        finally
        {
            closeDatabase();
        }
    }

    public synchronized static void updateContent(String url, String newContent)
    {
        try
        {
            openDatabase();
            ContentValues cv = new ContentValues();
            cv.put("html_content", newContent);
            final int updateCounts = database.update("news", cv, "url = ?",
                    new String[]
                    { url });
            if (updateCounts == 1)
            {
                log.info("成功更新");
            }
            else
            {
                log.info("成功失败,更新条数:" + updateCounts);
            }
        }
        catch (Exception e)
        {
            log.error(url, e);
        }
        finally
        {
            closeDatabase();
        }
    }

    public synchronized static void updateDate(String url, String date)
    {
        try
        {
            openDatabase();
            ContentValues cv = new ContentValues();
            cv.put("date", date);
            final int updateCounts = database.update("news", cv, "url = ?",
                    new String[]
                    { url });
            if (updateCounts == 1)
            {
                log.info("成功更新");
            }
            else
            {
                log.info("成功失败,更新条数:" + updateCounts);
            }
        }
        catch (Exception e)
        {
            log.error(url, e);
        }
        finally
        {
            closeDatabase();
        }
    }

    public synchronized static void updateContent(String url, int tCounts,
            int cCounts)
    {
        try
        {
            openDatabase();
            ContentValues cv = new ContentValues();
            cv.put("topic_counts", tCounts);
            cv.put("comment_counts", cCounts);
            final int updateCounts = database.update("news", cv, "url = ?",
                    new String[]
                    { url });
            if (updateCounts == 1)
            {
                log.info("成功更新");
            }
            else
            {
                log.info("更新失败,更新条数:" + updateCounts);
            }
        }
        catch (Exception e)
        {
            log.error(url, e);
        }
        finally
        {
            closeDatabase();
        }
    }

    public synchronized static void updateContent(NewsInfo info)
    {
        updateContent(info.getUrl(), info.getHtml_content());
    }

    public synchronized static void insertNews(List<NewsInfo> news)
    {
        try
        {
            openDatabase();
            for (NewsInfo newsInfo : news)
            {
                insertSingleNews(database, newsInfo);
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

    public synchronized static void insertSingleNews(SQLiteDatabase db,
            NewsInfo newsInfo)
    {
        try
        {
            db.execSQL(
                    "INSERT INTO NEWS(title,url,sub_text,date,head_pic,keywords,html_content,website_id,create_time,topic_counts,comment_counts) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                    new Object[]
                    { newsInfo.getTitle(), newsInfo.getUrl(),
                            newsInfo.getSub_text(), newsInfo.getDate(),
                            newsInfo.getHead_pic(),
                            newsInfo.getKeywords().toString(),
                            newsInfo.getHtml_content(),
                            newsInfo.getWebsite().getDb_id(),
                            BasicDateUtil.getCurrentDateTimeString(),
                            newsInfo.getTopic_counts(),
                            newsInfo.getComment_counts() });
        }
        catch (Exception e)
        {
            log.error(newsInfo.getUrl(), e);
        }
    }

    public static void insertClubs(SQLiteDatabase db, List<Club> clubs)
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
                    log.error("俱乐部名:" + club.getFull_name(), e);

                }
            }
        }
        catch (Exception e)
        {
            log.error("俱乐部总条数:" + clubs.size(), e);

        }
        finally
        {
            closeDatabase();
        }
    }

    public static boolean isExistUrl(SQLiteDatabase db, String url)
    {
        boolean flag = false;
        try
        {
            String sql = "select * from news where url='"
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

    public static boolean isSoccerTeam(SQLiteDatabase db, String team)
    {
        boolean flag = false;
        try
        {
            String sql = "select * from club where full_name like '%"
                    + StringEscapeUtils.escapeSql(team)
                    + "%' and league in(641,682,712,717)";
            Cursor c = db.rawQuery(sql, null);
            c.moveToFirst();
            while (!c.isAfterLast())
            {
                flag = true;
                break;
            }
        }
        catch (Exception e)
        {
            log.error(team, e);

        }
        return flag;
    }

    public static boolean isTeam(SQLiteDatabase db, String team)
    {
        boolean flag = false;
        try
        {
            String sql = "select * from club where full_name like '%"
                    + StringEscapeUtils.escapeSql(team) + "%'";
            Cursor c = db.rawQuery(sql, null);
            c.moveToFirst();
            while (!c.isAfterLast())
            {
                flag = true;
                break;
            }
        }
        catch (Exception e)
        {
            log.error(team, e);
        }
        return flag;
    }

    public static List<NewsInfo> findAllForumInfos()
    {
        return findAllNewsWithUrlFilter(".realgm.,.reddit.");
    }

    public static List<NewsInfo> findAllNBANews()
    {
        return findAllNewsWithUrlFilter(".basketballinsiders.");
    }

    public static List<NewsInfo> findAllSoccerNews()
    {
        return findAllNewsWithUrlFilter(".squawka.,.skysports.");
    }

    public static List<NewsInfo> findAllNewsWithUrlFilter(String url_filter)
    {
        String[] keys = url_filter.split("[, ]");
        String f = "";
        if (url_filter.trim().length() == 0)
        {
            f = "or url is not null ";
        }
        else
        {
            for (String s : keys)
            {
                f += " or url like '%" + StringEscapeUtils.escapeSql(s.trim())
                        + "%'";
            }
        }

        String sql = "select * from news where 1=2 " + f
                + " order by replace(date,'-','') desc,topic_counts desc ";
        return findAllNewsBySql(sql);

    }

    /**
     * 有的有点错误
     * 
     * @return
     */
    public static List<NewsInfo> findErrContentNews()
    {
        String sql = "select * from news where  html_content like '%<a href=\">%' order by date desc";
        return findAllNewsBySql(sql);
    }

    public static List<NewsInfo> search(String keyword)
    {
        String team = "";
        String webSiteId = "";
        String[] keys = keyword.split("[ +]");
        SQLiteDatabase tmp_db = DatabaseManager.getInstance().openDatabase();
        String episodeSql = "";
        List<String> otherKeys = new ArrayList<String>();
        for (String key : keys)
        {
            if (webSiteId.length() == 0 && StaticsHelper.isWebSite(tmp_db, key))
            {
                webSiteId = StaticsHelper.getWebSiteId(tmp_db, key);
                episodeSql += " and website_id=" + webSiteId;
                continue;
            }
            if (team.length() == 0 && StaticsHelper.isTeam(tmp_db, key))
            {
                team = key;
                episodeSql += " and keywords like '%" + team + "%'";
                continue;
            }
            if (key.equals(MyAppParams.getInstance().getBaskModelName()))
            {
                episodeSql += " and website_id in ( 1 ) ";
                continue;
            }
            if (key.equals(MyAppParams.getInstance().getSoccModelName()))
            {
                episodeSql += " and website_id in (2,3) ";
                continue;
            }
            if (key.equals(MyAppParams.getInstance().getForuModelName()))
            {
                episodeSql += " and website_id in (4,5) ";
                continue;
            }

            otherKeys.add(key);
        }
        episodeSql += " and (";
        if (otherKeys.size() > 0)
        {
            for (int i = 0; i < otherKeys.size(); i++)
            {
                String s = "html_content like '%" + otherKeys.get(i)
                        + "%' or title like '%" + otherKeys.get(i) + "%'";
                if (i == 0)
                {
                    episodeSql += "( " + s + ") ";
                }
                else
                {
                    if (keyword.contains("+"))
                    {
                        episodeSql += " and ( " + s + " )";
                    }
                    else
                    {
                        episodeSql += " or ( " + s + " )";
                    }

                }
            }
        }
        else
        {
            episodeSql += "1=1";
        }
        episodeSql += ")";
        DatabaseManager.getInstance().closeDatabase();

        String sql = "select * from news where 1=1 " + episodeSql
                + " order by replace(date,'-','') desc,topic_counts desc";
        log.info(sql);
        return findAllNewsBySql(sql);
    }

    private synchronized static List<NewsInfo> findAllNewsBySql(String sql)
    {
        List<NewsInfo> list = new ArrayList<NewsInfo>();
        try
        {
            openDatabase();
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
                info.setUrl(c.getString(c.getColumnIndex("url")));
                info.setCreate_time(c.getString(c.getColumnIndex("create_time")));
                info.setTopic_counts(c.getInt(c.getColumnIndex("topic_counts")));
                info.setComment_counts(c.getInt(c
                        .getColumnIndex("comment_counts")));
                String kw = c.getString(c.getColumnIndex("keywords"));
                if (kw != null)
                {
                    Set<String> set = new HashSet<String>();
                    set.add(kw);
                    info.setKeywords(set);
                }
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

    public static void test()
    {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        Cursor c = db.rawQuery("select * from news", null);
        c.moveToFirst();
        log.info("test测试结果:" + c.getCount());
        DatabaseManager.getInstance().closeDatabase();
    }

}
