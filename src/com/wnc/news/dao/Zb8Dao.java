package com.wnc.news.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wnc.news.api.mine.zhibo8.Zb8News;
import com.wnc.news.db.DatabaseManager_ZB8;

public class Zb8Dao
{
	static Logger log = Logger.getLogger(Zb8Dao.class);
	static SQLiteDatabase database;

	public static void openDatabase()
	{
		database = DatabaseManager_ZB8.getInstance().openDatabase();
	}

	public static void closeDatabase()
	{
		DatabaseManager_ZB8.getInstance().closeDatabase();
	}

	public static boolean isExistUrl(SQLiteDatabase db, String url)
	{
		boolean flag = false;
		try
		{
			String sql = "select * from article where url='" + StringEscapeUtils.escapeSql(url) + "'";
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

	public synchronized static void insertZb8News(List<Zb8News> news)
	{
		try
		{
			openDatabase();
			for (Zb8News newsInfo : news)
			{
				insertSingleZb8News(database, newsInfo);
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

	public synchronized static void insertSingleZb8News(SQLiteDatabase db, Zb8News newsInfo)
	{
		try
		{
			db.execSQL(
					"INSERT INTO Article(title,url,day,sub_text,news_time,chs_content,eng_content,keyword,thumbnail,from_url,from_website,from_name,create_time,sport_type," + "comments,hot_comments,update_time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					new Object[] { newsInfo.getTitle(), newsInfo.getUrl(), newsInfo.getDay(), newsInfo.getSub_text(), newsInfo.getNews_time(), newsInfo.getChs_content(), newsInfo.getEng_content(), newsInfo.getKeyword(), newsInfo.getThumbnail(), newsInfo.getFrom_url(),
							newsInfo.getFrom_website(), newsInfo.getFrom_name(), newsInfo.getCreate_time(), newsInfo.getSport_type(), newsInfo.getComments(), newsInfo.getHotComments(), newsInfo.getUpdate_time() });
		}
		catch (Exception e)
		{
			log.error(newsInfo.getUrl(), e);
		}
	}

	private synchronized static List<Zb8News> findAllNewsBySql(String sql)
	{
		List<Zb8News> list = new ArrayList<Zb8News>();
		try
		{
			openDatabase();
			Cursor c = database.rawQuery(sql, null);
			c.moveToFirst();
			Zb8News info;
			while (!c.isAfterLast())
			{
				info = new Zb8News();
				info.setId(c.getLong(c.getColumnIndex("id")));
				info.setUrl(c.getString(c.getColumnIndex("url")));
				info.setTitle(c.getString(c.getColumnIndex("title")));
				info.setDay(c.getString(c.getColumnIndex("day")));
				info.setSub_text(c.getString(c.getColumnIndex("sub_text")));
				info.setNews_time(c.getString(c.getColumnIndex("news_time")));
				info.setChs_content(c.getString(c.getColumnIndex("chs_content")));
				info.setEng_content(c.getString(c.getColumnIndex("eng_content")));
				info.setKeyword(c.getString(c.getColumnIndex("keyword")));
				info.setThumbnail(c.getString(c.getColumnIndex("thumbnail")));
				info.setFrom_url(c.getString(c.getColumnIndex("from_url")));
				info.setFrom_website(c.getInt(c.getColumnIndex("from_website")));
				info.setFrom_name(c.getString(c.getColumnIndex("from_name")));
				info.setCreate_time(c.getString(c.getColumnIndex("create_time")));
				info.setSport_type(c.getInt(c.getColumnIndex("sport_type")));
				info.setComments(c.getInt(c.getColumnIndex("comments")));
				info.setHotComments(c.getInt(c.getColumnIndex("hot_comments")));
				info.setUpdate_time(c.getString(c.getColumnIndex("update_time")));
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

	public static List<Zb8News> findAllNewsInfos(int from, int counts)
	{
		return findAllNewsBySql("SELECT * FROM ARTICLE ORDER BY NEWS_TIME DESC LIMIT " + from + "," + counts);
	}

	public static boolean hasViewed(SQLiteDatabase db, int news_id)
	{
		try
		{
			String sql = "select * from article where id=" + news_id;
			return db.rawQuery(sql, null).getCount() > 0;
		}
		catch (Exception e)
		{
			log.error(news_id, e);
		}
		return false;
	}

}
