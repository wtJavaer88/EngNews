package com.wnc.news.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import common.uihelper.MyAppParams;
import common.utils.WordSplit;

public class DictionaryDao
{
	static SQLiteDatabase database;
	private static List<String> wordAndChars;

	public static void openDatabase()
	{
		try
		{
			String databaseFilename = MyAppParams.DICTIONARY_DB;
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

	static Set<String> topics = new HashSet<String>();

	public synchronized static Set<String> initTopics()
	{
		try
		{
			openDatabase();

			String sql = "select e.*,d.topic_word,d.mean_cn FROM word_exchange E LEFT JOIN dictionary D ON E.topic_id=D.topic_id";
			Cursor c = database.rawQuery(sql, null);
			c.moveToFirst();
			while (!c.isAfterLast())
			{
				// word_third word_done word_pl word_ing word_past word_er
				// word_est
				topics.add(c.getString(c.getColumnIndex("word_third")));
				topics.add(c.getString(c.getColumnIndex("word_done")));
				topics.add(c.getString(c.getColumnIndex("word_pl")));
				topics.add(c.getString(c.getColumnIndex("word_ing")));
				topics.add(c.getString(c.getColumnIndex("word_past")));
				topics.add(c.getString(c.getColumnIndex("word_er")));
				topics.add(c.getString(c.getColumnIndex("word_est")));
				topics.add(c.getString(c.getColumnIndex("topic_word")));
				c.moveToNext();
			}
			// topics.remove("target");
			// topics.remove("guy");
			// topics.remove("blank");
			// topics.remove("span");
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			closeDatabase();
		}
		return topics;
	}

	public static Set<String> findCETWords(String dialog)
	{
		Set<String> finds = new HashSet<String>();
		wordAndChars = WordSplit.getWordAndChars(dialog.replaceAll("<.*?>", ""));
		for (String string : wordAndChars)
		{
			if (string.trim().length() > 3)
			{
				for (String t : topics)
				{
					if (t.equalsIgnoreCase(string.trim()))
					{
						finds.add(t);
					}
				}
			}
		}
		return finds;
	}
}
