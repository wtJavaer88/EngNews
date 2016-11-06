package com.wnc.news.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicStringUtil;
import com.wnc.news.engnews.kpi.KPIData;
import com.wnc.news.engnews.kpi.SelectedWord;
import com.wnc.news.engnews.kpi.ViewedNews;

public class KPIDao
{
	static Logger log = Logger.getLogger(KPIDao.class);

	public static boolean isExistDay(SQLiteDatabase db, String today)
	{
		boolean flag = false;
		try
		{
			String sql = "select * from news_kpi where date='" + StringEscapeUtils.escapeSql(today) + "'";
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
			db.execSQL("INSERT INTO NEWS_KPI(date,update_time) VALUES ('" + today + "','" + BasicDateUtil.getCurrentDateTimeString() + "')");
		}
		catch (Exception e)
		{
			log.error(today, e);
		}
	}

	public static void insertViewHistory(SQLiteDatabase db, int news_id, int topic_counts, int times)
	{
		try
		{
			db.execSQL("INSERT INTO VIEW_HISTORY(news_id,topic_counts,view_duration,create_time) VALUES (" + news_id + "," + topic_counts + "," + times + ",'" + BasicDateUtil.getCurrentDateTimeString() + "')");
		}
		catch (Exception e)
		{
			log.error(news_id, e);
		}
	}

	public static boolean increaseViewed(SQLiteDatabase db, String today, int topic_counts, int times)
	{
		try
		{
			db.execSQL("UPDATE NEWS_KPI SET view_news=view_news+1,topic_counts=topic_counts+" + topic_counts + ",durations=durations+" + times + ",update_time='" + BasicDateUtil.getCurrentDateTimeString() + "' WHERE DATE='" + today + "'");
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

	public static KPIData findKPIDataByDay(SQLiteDatabase db, String day)
	{
		final List<KPIData> findBysql = findBysql(db, "SELECT * FROM NEWS_KPI WHERE DATE='" + day + "'");
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
				info.setSelectedWords(c.getInt(c.getColumnIndex("selected_words")));
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

	public static boolean addLovedNews(SQLiteDatabase db, int news_id, String today)
	{
		try
		{
			db.execSQL("INSERT INTO fav_news(news_id,create_time) VALUES (" + news_id + ",'" + BasicDateUtil.getCurrentDateTimeString() + "')");
			db.execSQL("UPDATE NEWS_KPI SET fav_news=fav_news+1 WHERE DATE='" + today + "'");

		}
		catch (Exception e)
		{
			log.error(news_id, e);
			return false;
		}
		return true;
	}

	public static boolean addSelectedWord(SQLiteDatabase db, int news_id, String word, int topic_id)
	{
		if (isExistSelected(db, news_id, word))
		{
			return true;
		}
		try
		{
			final String sql = "INSERT INTO select_word_save(news_id,topic_id,word,create_time) VALUES (" + news_id + "," + topic_id + ",'" + word + "','" + BasicDateUtil.getCurrentDateTimeString() + "')";
			System.out.println(sql);
			db.execSQL(sql);
		}
		catch (Exception e)
		{
			log.error(news_id, e);
			return false;
		}
		return true;
	}

	public static boolean updateKPISelected(SQLiteDatabase db, int count, String today)
	{
		try
		{
			final String sql = "UPDATE NEWS_KPI SET selected_words=selected_words+" + count + " WHERE date='" + today + "'";
			System.out.println(sql);
			db.execSQL(sql);
		}
		catch (Exception e)
		{
			log.error(today, e);
			return false;
		}
		return true;
	}

	private static boolean isExistSelected(SQLiteDatabase db, int news_id, String word)
	{
		boolean flag = false;
		try
		{
			String sql = "select * from select_word_save where news_id=" + news_id + " and word='" + word + "'";
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

	public static Set<SelectedWord> getLatelyWords(SQLiteDatabase db)
	{
		return findSelectedWordsBySQL(db, "SELECT * FROM SELECT_WORD_SAVE ORDER BY CREATE_TIME DESC LIMIT 0, 200");
	}

	public static Set<SelectedWord> findSelectedWordsByDay(SQLiteDatabase db, String day)
	{
		if (day.length() == 8)
		{
			day = day.substring(0, 4) + "-" + day.substring(4, 6) + "-" + day.substring(6, 8);
		}
		return findSelectedWordsBySQL(db, "SELECT  * FROM SELECT_WORD_SAVE WHERE create_time like '" + day + "%' ORDER BY CREATE_TIME ASC");
	}

	public static Set<SelectedWord> findSelectedWordsBySQL(SQLiteDatabase db, String sql)
	{
		Set<SelectedWord> list = new HashSet<SelectedWord>();
		try
		{
			Cursor c = db.rawQuery(sql, null);
			c.moveToFirst();
			SelectedWord sword;
			while (!c.isAfterLast())
			{
				sword = new SelectedWord();
				sword.setTopic_id(c.getInt(c.getColumnIndex("topic_id")));
				sword.setWord(c.getString(c.getColumnIndex("word")));
				list.add(sword);
				c.moveToNext();
			}
		}
		catch (Exception e)
		{
			log.error(sql, e);
		}
		return list;
	}

	public static List<ViewedNews> findViewedNewsByDay(SQLiteDatabase db, String day)
	{
		if (day.length() == 8)
		{
			day = day.substring(0, 4) + "-" + day.substring(4, 6) + "-" + day.substring(6, 8);
		}

		return findAllNewsBySql(db, "SELECT n.*,h.view_duration,h.create_time view_time FROM VIEW_HISTORY H,NEWS N where h.news_id=n.id and h.create_time like '" + day + "%'", 1);
	}

	public static List<ViewedNews> findLovedNewsByDay(SQLiteDatabase db, String day)
	{
		if (day.length() == 8)
		{
			day = day.substring(0, 4) + "-" + day.substring(4, 6) + "-" + day.substring(6, 8);
		}

		return findAllNewsBySql(db, "SELECT n.*,f.create_time view_time FROM FAV_NEWS F,NEWS N where f.news_id=n.id and f.create_time like '" + day + "%'", 2);
	}

	public synchronized static List<ViewedNews> findAllNewsBySql(SQLiteDatabase db, String sql, int kinda)
	{
		List<ViewedNews> list = new ArrayList<ViewedNews>();
		try
		{
			Cursor c = db.rawQuery(sql, null);
			c.moveToFirst();
			ViewedNews info;
			while (!c.isAfterLast())
			{
				info = new ViewedNews();
				info.setHtml_content(c.getString(c.getColumnIndex("html_content")));
				info.setCet_topics(c.getString(c.getColumnIndex("cet_topics")));
				info.setHead_pic(c.getString(c.getColumnIndex("head_pic")));
				info.setSub_text(c.getString(c.getColumnIndex("sub_text")));
				info.setTitle(c.getString(c.getColumnIndex("title")));
				info.setDate(c.getString(c.getColumnIndex("date")));
				info.setDb_id(c.getInt(c.getColumnIndex("id")));
				info.setUrl(c.getString(c.getColumnIndex("url")));
				info.setCreate_time(c.getString(c.getColumnIndex("create_time")));
				info.setTopic_counts(c.getInt(c.getColumnIndex("topic_counts")));
				info.setComment_counts(c.getInt(c.getColumnIndex("comment_counts")));
				info.setView_time(c.getString(c.getColumnIndex("view_time")));
				if (kinda == 1)
				{
					info.setView_duration(c.getInt(c.getColumnIndex("view_duration")));
				}
				String kw = c.getString(c.getColumnIndex("keywords"));
				if (BasicStringUtil.isNotNullString(kw))
				{
					info.addKeyWord(kw);
				}
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
}
