package com.wnc.news.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
			database = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
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

	public synchronized static NewsInfo findFirstNews()
	{
		NewsInfo info = new NewsInfo();
		try
		{
			openDatabase();
			String sql = "select * from news";
			Cursor c = database.rawQuery(sql, null);
			c.moveToFirst();
			while (!c.isAfterLast())
			{
				info = new NewsInfo();
				info.setHtml_content(c.getString(c.getColumnIndex("html_content")));
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
		return info;
	}

	public synchronized static void deleteNews()
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

	public synchronized static void insertNews(List<NewsInfo> news)
	{
		try
		{
			openDatabase();
			for (NewsInfo newsInfo : news)
			{
				try
				{
					database.execSQL("INSERT INTO news(title,url,sub_text,date,head_pic,keywords,html_content) VALUES (?,?,?,?,?,?,?)",
							new Object[] { newsInfo.getTitle(), newsInfo.getUrl(), newsInfo.getSub_text(), newsInfo.getDate(), newsInfo.getHead_pic(), newsInfo.getKeywords(), newsInfo.getHtml_content() });
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

	public static List<NewsInfo> findAllNews()
	{
		List<NewsInfo> list = new ArrayList<NewsInfo>();
		try
		{
			openDatabase();
			String sql = "select * from news order by date desc";
			Cursor c = database.rawQuery(sql, null);
			c.moveToFirst();
			NewsInfo info = new NewsInfo();
			while (!c.isAfterLast())
			{
				info = new NewsInfo();
				info.setHtml_content(c.getString(c.getColumnIndex("html_content")));
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
