package com.wnc.news.engnews.kpi;

import java.util.List;
import java.util.Set;

import word.DicWord;
import android.database.sqlite.SQLiteDatabase;

import com.wnc.basic.BasicDateUtil;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.dao.DictionaryDao;
import com.wnc.news.dao.KPIDao;
import com.wnc.news.db.DatabaseManager_Main;
import com.wnc.news.engnews.helper.OptedDictData;

public class KPIService
{
	private String today = BasicDateUtil.getCurrentDateString();
	SQLiteDatabase db = DatabaseManager_Main.getInstance().openDatabase();
	boolean todayExist = false;
	private static KPIService kPIHelper = new KPIService();

	private KPIService()
	{

	}

	public void closeDb()
	{
		DatabaseManager_Main.getInstance().closeDatabase();
	}

	public static KPIService getInstance()
	{
		return kPIHelper;
	}

	public List<KPIData> getHistory()
	{
		return KPIDao.getAllHistory(db);
	}

	public KPIData findTodayData()
	{
		return KPIDao.findKPIDataByDay(db, BasicDateUtil.getCurrentDateString());
	}

	public KPIData findDataByDay(String day)
	{
		return KPIDao.findKPIDataByDay(db, day);
	}

	public boolean increaseLoved(int news_id)
	{
		return KPIDao.addLovedNews(db, news_id, today);
	}

	public void refreshCurDay()
	{
		if (!BasicDateUtil.getCurrentDateString().equals(today))
		{
			today = BasicDateUtil.getCurrentDateString();
		}
	}

	// 在退出某个新闻页面的时候调用,或者在前一页后一页的时候
	public void increaseViewed(int times, int topic_counts, NewsInfo newsInfo)
	{
		// 如果不存在date记录,insert
		// 否则update, viewed+1, highlights=getHLCounts,
		// times+view_time,selected_words = selected_count
		KPIDao.insertViewHistory(db, newsInfo.getDb_id(), topic_counts, times);
		if (!todayExist)
		{
			if (!KPIDao.isExistDay(db, today))
			{
				KPIDao.insertRecordByday(db, today);
			}
			todayExist = true;
		}
		KPIDao.increaseViewed(db, today, topic_counts, times);
	}

	public boolean addSelectedWord(int news_id, String base_word, int topic_id)
	{
		return KPIDao.addSelectedWord(db, news_id, base_word, topic_id);
	}

	public boolean hasViewed(int news_id)
	{
		return KPIDao.hasViewed(db, news_id);
	}

	/**
	 * 获取最近的单词
	 * 
	 * @return
	 */
	public Set<SelectedWord> getLatelyWords()
	{
		Set<SelectedWord> latelyWords = OptedDictData.getLatelyWords();
		if (latelyWords.size() == 0)
		{
			OptedDictData.setLatelyWords(KPIDao.getLatelyWords(db));
			latelyWords = OptedDictData.getLatelyWords();
		}
		return latelyWords;
	}

	public void addToLatelyWords(String selectedText, int topic_id)
	{
		Set<SelectedWord> latelyWords = OptedDictData.getLatelyWords();
		if (latelyWords.size() == 0)
		{
			OptedDictData.setLatelyWords(KPIDao.getLatelyWords(db));
		}
		SelectedWord sword = new SelectedWord();
		sword.setTopic_id(topic_id);
		sword.setWord(selectedText);
		OptedDictData.getLatelyWords().add(sword);
	}

	public Set<SelectedWord> findSelectedWords(String day)
	{
		Set<SelectedWord> selectedWords = KPIDao.findSelectedWordsByDay(db, day);
		for (SelectedWord sword : selectedWords)
		{
			if (sword.getTopic_id() <= 0)
			{
				// System.out.println("findSelectedWords根据单词查");
				DicWord findWord = DictionaryDao.findWord(sword.getWord());
				if (findWord != null)
				{
					sword.setCn_mean(findWord.getCn_mean());
				}
			}
			else
			{
				// System.out.println("findSelectedWords根据ID查:"
				// + sword.getTopic_id());
				DicWord findWord = DictionaryDao.findWordById(sword.getTopic_id());
				if (findWord != null)
				{
					sword.setCn_mean(findWord.getCn_mean());
				}
			}
		}
		return selectedWords;
	}

	public List<ViewedNews> findViewHistory(String day)
	{
		return KPIDao.findViewedNewsByDay(db, day);
	}

	public List<ViewedNews> findLoveHistory(String day)
	{
		return KPIDao.findLovedNewsByDay(db, day);
	}

	public Set<SelectedWord> findSelectedWordsToday()
	{
		return findSelectedWords(today);
	}

	public void updateKPISelected(int count)
	{
		if (!todayExist)
		{
			if (!KPIDao.isExistDay(db, today))
			{
				KPIDao.insertRecordByday(db, today);
			}
			todayExist = true;
		}
		KPIDao.updateKPISelected(db, count, today);
	}

	public List<ViewedNews> findViewHistoryToday()
	{
		return findViewHistory(today);
	}
}
