package com.wnc.news.dao;

import org.apache.log4j.Logger;

import word.DicWord;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import common.uihelper.MyAppParams;

public class ITDictDao
{
	static SQLiteDatabase database;
	static Logger log = Logger.getLogger(ITDictDao.class);

	public static synchronized void openDatabase()
	{
		try
		{
			String databaseFilename = MyAppParams.IT_DICT_DB;
			database = SQLiteDatabase.openOrCreateDatabase(databaseFilename,
					null);
		}
		catch (Exception e)
		{
			log.error(e);

		}
	}

	public static synchronized void closeDatabase()
	{
		if (database != null)
		{
			try
			{
				database.close();
				database = null;
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static synchronized boolean isConnect()
	{
		return database != null && database.isOpen();
	}

	public synchronized static DicWord findWord(String word)
	{
		DicWord dicWord = null;
		word = word.toLowerCase().toString();
		try
		{
			openDatabase();

			String sql = "select e.word_done,e.word_er,e.word_est,e.word_ing,e.word_pl,e.word_past,e.word_third,d.id,d.topic_id,d.topic_word,d.mean_cn FROM  dictionary D LEFT JOIN word_exchange E  ON E.dict_id=D.id where LOWER(topic_word)='"
					+ word
					+ "' or  word_third='"
					+ word
					+ "' or  word_done='"
					+ word
					+ "' or  word_er='"
					+ word
					+ "' or  word_est='"
					+ word
					+ "' or  word_ing='"
					+ word
					+ "' or  word_pl='"
					+ word + "' or  word_past='" + word + "'";
			Cursor c = database.rawQuery(sql, null);
			c.moveToFirst();
			while (!c.isAfterLast())
			{
				dicWord = new DicWord();
				dicWord.setBase_word(c.getString(c.getColumnIndex("topic_word")));
				dicWord.setId(c.getInt(c.getColumnIndex("id")));
				dicWord.setWord_third(c.getString(c
						.getColumnIndex("word_third")));
				dicWord.setWord_done(c.getString(c.getColumnIndex("word_done")));
				dicWord.setWord_er(c.getString(c.getColumnIndex("word_er")));
				dicWord.setWord_est(c.getString(c.getColumnIndex("word_est")));
				dicWord.setWord_ing(c.getString(c.getColumnIndex("word_ing")));
				dicWord.setWord_pl(c.getString(c.getColumnIndex("word_pl")));
				dicWord.setWord_past(c.getString(c.getColumnIndex("word_past")));
				dicWord.setCn_mean(c.getString(c.getColumnIndex("mean_cn")));
				return dicWord;
			}
		}
		catch (Exception e)
		{
			log.error(word, e);
		}
		finally
		{
			closeDatabase();
		}
		return dicWord;
	}

}
