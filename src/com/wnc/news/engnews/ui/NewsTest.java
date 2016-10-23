package com.wnc.news.engnews.ui;

import java.util.List;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import word.Topic;
import android.database.sqlite.SQLiteDatabase;

import com.wnc.news.api.autocache.CETTopicCache;
import com.wnc.news.api.autocache.CETTopicUpdate;
import com.wnc.news.api.autocache.CacheSchedule;
import com.wnc.news.api.common.Club;
import com.wnc.news.api.nba.RealGmApi;
import com.wnc.news.api.soccer.SquawkaLeagueApi;
import com.wnc.news.dao.DictionaryDao;
import com.wnc.news.dao.NewsDao;
import com.wnc.news.db.DatabaseManager;
import common.utils.JsoupHelper;

public class NewsTest
{
	static Logger log = Logger.getLogger(NewsTest.class);

	public void getClubs()
	{
		SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

		List<Club> allClubs = new SquawkaLeagueApi(712).getAllClubs();
		log.info("allClubs数量:" + allClubs.size());
		NewsDao.insertClubs(db, allClubs);
		allClubs = new SquawkaLeagueApi(682).getAllClubs();
		log.info("allClubs数量:" + allClubs.size());
		NewsDao.insertClubs(db, allClubs);
		allClubs = new SquawkaLeagueApi(717).getAllClubs();
		log.info("allClubs数量:" + allClubs.size());
		NewsDao.insertClubs(db, allClubs);
		DatabaseManager.getInstance().closeDatabase();

	}

	public void testRealGm()
	{
		new RealGmApi().getAll();
	}

	public void topicUpdate()
	{
		new CETTopicUpdate().update();
		new CETTopicCache().update();
	}

	public void ifSoccerTeam()
	{
		log.info(NewsDao.isSoccerTeam(DatabaseManager.getInstance().openDatabase(), "barcelona"));
		DatabaseManager.getInstance().closeDatabase();
	}

	public void cacheArsenal(CacheSchedule cacheSchedule)
	{
		cacheSchedule.addTeam("arsenal");
		cacheSchedule.teamCache();
	}

	public void cacheFormus(CacheSchedule cacheSchedule)
	{
		cacheSchedule.formusCache();
	}

	public void cacheTeams(CacheSchedule cacheSchedule)
	{
		cacheSchedule.addTeam("arsenal");
		cacheSchedule.addTeam("barcelona");
		cacheSchedule.addTeam("manchester-united");
		cacheSchedule.addTeam("manchester-city");
		cacheSchedule.addTeam("liverpool");
		cacheSchedule.addTeam("tottenham-hotspur");
		cacheSchedule.addTeam("real-madrid");
		cacheSchedule.addTeam("san-antonio-spurs");
		cacheSchedule.addTeam("golden-state-warriors");
		cacheSchedule.teamCache();
	}

	public void testSingleNews()
	{
		// http://www.basketballinsiders.com/nba-am-the-quadruple-double/
		Document doc;
		try
		{
			doc = JsoupHelper.getDocumentResult("http://www.basketballinsiders.com/nba-am-the-quadruple-double/");
			final Elements contents = doc.select("#content-area p");
			if (contents != null && contents.text().length() > 200)
			{
				log.info(contents.text());
				List<Topic> findCETWords = DictionaryDao.findCETWords(contents.text());
				log.info(findCETWords.size());
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
