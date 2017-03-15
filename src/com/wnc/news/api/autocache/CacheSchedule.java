package com.wnc.news.api.autocache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import voa.VoaNewsInfo;
import android.database.sqlite.SQLiteDatabase;

import com.wnc.news.api.common.ForumsApi;
import com.wnc.news.api.common.NewsApi;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.api.common.StaticsHelper;
import com.wnc.news.api.forums.RedditApi;
import com.wnc.news.api.nba.NbaTeamApi;
import com.wnc.news.api.soccer.SkySportsTeamApi;
import com.wnc.news.api.soccer.SquawkaTeamApi;
import com.wnc.news.api.voa.IyubaApi;
import com.wnc.news.dao.NewsDao;
import com.wnc.news.dao.VoaDao;
import com.wnc.news.db.DatabaseManager_Main;
import common.uihelper.MyAppParams;

public class CacheSchedule
{
	public void clearCache()
	{
		NewsDao.deleteTestNews();
	}

	Logger log = Logger.getLogger(CacheSchedule.class);

	Set<String> teams = new HashSet<String>();
	List<String> list = new ArrayList<String>();

	public void addTeam(String team)
	{
		teams.add(team);
	}

	public List<String> getMap()
	{
		return list;
	}

	public void teamCache()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				allCached1 = false;
				SQLiteDatabase db = DatabaseManager_Main.getInstance().openDatabase();
				CETTopicCache cetTopicCache = new CETTopicCache();

				for (String team : teams)
				{
					try
					{
						int newsCount = 0;
						if (StaticsHelper.isSoccerTeam(team))
						{

							newsCount += cache(new SkySportsTeamApi(team), 2, cetTopicCache, "SkySportsTeamApi-" + team);
							newsCount += cache(new SquawkaTeamApi(team), 2, cetTopicCache, "SquawkaTeamApi-" + team);
						}
						else
						{
							newsCount += cache(new NbaTeamApi(team), 2, cetTopicCache, "NbaTeamApi-" + team);
						}
						if (newsCount > 0)
						{
							list.add(team + " " + newsCount);
						}
					}
					catch (Exception e)
					{
						log.error(team, e);
						e.printStackTrace();
					}
				}
				DatabaseManager_Main.getInstance().closeDatabase();
				cetTopicCache.shutdown();
				cetTopicCache.ifOver();
				allCached1 = true;
			}

		}).start();

	}

	private int cache(NewsApi teamApi, int pages, CETTopicCache cetTopicCache, String webtip)
	{
		int ret = 0;
		List<NewsInfo> allNews;
		log.info(webtip + "开始:");
		teamApi.setMaxPages(pages);
		allNews = teamApi.getAllNewsWithContent();
		for (NewsInfo newsInfo : allNews)
		{
			if (!NewsDao.isExistUrl(newsInfo.getUrl()))
			{
				NewsDao.insertSingleNews(db_forums, newsInfo);
				ret++;
			}
		}
		cetTopicCache.executeTasks(allNews);
		// ret = allNews.size();
		log.info(webtip + "结束  缓存数:" + ret);
		if (teamApi instanceof ForumsApi && ret > 0)
		{
			list.add(webtip + " " + ret);
		}
		return ret;
	}

	private boolean allCached1 = true;
	private boolean allCached2 = true;

	public boolean isAllCached()
	{
		return allCached1 && allCached2;
	}

	SQLiteDatabase db_forums = DatabaseManager_Main.getInstance().openDatabase();

	// 论坛需要考虑插入和更新操作
	public void formusCache()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				allCached2 = false;
				try
				{
					CETTopicCache cetTopicCache = new CETTopicCache();
					// cache(new RealGmApi(), 1, cetTopicCache, "RealGm论坛");

					cache(new RedditApi("arsenal"), 1, cetTopicCache, "Reddit/阿森纳");
					cache(new RedditApi("san-antonio-spurs"), 1, cetTopicCache, "Reddit/马刺");
					cache(new RedditApi("golden-state-warriors"), 1, cetTopicCache, "Reddit/勇士");
					cache(new RedditApi(MyAppParams.getInstance().getSoccModelName()), 1, cetTopicCache, "Reddit/Soccer论坛");
					cache(new RedditApi(MyAppParams.getInstance().getBaskModelName()), 1, cetTopicCache, "Reddit/NBA论坛");

					DatabaseManager_Main.getInstance().closeDatabase();
					cetTopicCache.shutdown();
					cetTopicCache.ifOver();
				}
				catch (Exception e)
				{
					log.error("RealGm", e);
					e.printStackTrace();
				}
				allCached2 = true;
			}

		}).start();
	}

	public void voaCache()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				final IyubaApi iyubaApi = new IyubaApi();
				iyubaApi.setMaxPages(1);
				List<VoaNewsInfo> allNewsWithContent = iyubaApi.getAllNewsWithContent();
				// for (VoaNewsInfo voaNewsInfo : allNewsWithContent)
				// {
				// System.out.println(voaNewsInfo.getDate() + ""
				// + voaNewsInfo.getTitle());
				// }
				VoaDao.insertVOANews(allNewsWithContent);
			}
		}).start();
	}
}
