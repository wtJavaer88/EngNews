package com.wnc.news.api.autocache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import android.database.sqlite.SQLiteDatabase;

import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.api.nba.NbaTeamApi;
import com.wnc.news.api.soccer.SkySportsTeamApi;
import com.wnc.news.api.soccer.SquawkaTeamApi;
import com.wnc.news.dao.NewsDao;
import com.wnc.news.db.DatabaseManager;

public class CacheSchedule
{
    public void clearCache()
    {
        NewsDao.deleteAllNews();
    }

    Logger log = Logger.getLogger(CacheSchedule.class);

    Set<String> teams = new HashSet<String>();
    Map<String, Integer> map = new HashMap<String, Integer>();

    public void addTeam(String team)
    {
        teams.add(team);
    }

    public Map<String, Integer> getMap()
    {
        return map;
    }

    SQLiteDatabase db;

    public void teamCache()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                db = DatabaseManager.getInstance().openDatabase();
                CETTopicCache cetTopicCache = new CETTopicCache();
                for (String team : teams)
                {
                    List<NewsInfo> allNews;
                    int newsCount = 0;
                    if (isSoccerTeam(team))
                    {
                        log.info("SkySportsTeamApi开始:" + team);
                        final SkySportsTeamApi skySportsTeamApi = new SkySportsTeamApi(
                                team);
                        skySportsTeamApi.setMaxPages(5);
                        allNews = skySportsTeamApi.getAllNewsWithContent();
                        NewsDao.insertNews(allNews);
                        cetTopicCache.executeTasks(allNews);
                        newsCount += allNews.size();
                        log.info("SkySportsTeamApi结束:" + team + " 缓存数:"
                                + allNews.size());

                        log.info("SquawkaTeamApi开始:" + team);
                        final SquawkaTeamApi squawkaTeamApi = new SquawkaTeamApi(
                                team);
                        squawkaTeamApi.setMaxPages(3);
                        allNews = squawkaTeamApi.getAllNewsWithContent();
                        NewsDao.insertNews(allNews);
                        newsCount += allNews.size();

                        log.info("SquawkaTeamApi结束:" + team + " 缓存数:"
                                + allNews.size());
                        cetTopicCache.executeTasks(allNews);
                    }
                    else
                    {
                        log.info("NbaTeamApi开始:" + team);
                        final NbaTeamApi squawkaTeamApi = new NbaTeamApi(team);
                        squawkaTeamApi.setMaxPages(3);
                        allNews = squawkaTeamApi.getAllNewsWithContent();
                        NewsDao.insertNews(allNews);
                        newsCount += allNews.size();
                        log.info("NbaTeamApi结束:" + team + " 缓存数:"
                                + allNews.size());
                        cetTopicCache.executeTasks(allNews);
                    }
                    if (newsCount > 0)
                    {
                        map.put(team, newsCount);
                    }
                }
                DatabaseManager.getInstance().closeDatabase();
                allCached = true;
                cetTopicCache.shutdown();
                cetTopicCache.ifOver();
            }

        }).start();

    }

    private boolean allCached = false;

    public boolean isAllCached()
    {
        return allCached;
    }

    protected boolean isSoccerTeam(String team)
    {
        return NewsDao.isSoccerTeam(db, team);
    }
}
