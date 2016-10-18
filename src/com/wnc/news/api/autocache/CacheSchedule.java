package com.wnc.news.api.autocache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.api.nba.NbaTeamApi;
import com.wnc.news.api.soccer.SkySportsTeamApi;
import com.wnc.news.api.soccer.SquawkaTeamApi;
import com.wnc.news.dao.NewsDao;

public class CacheSchedule
{
    public void clearCache()
    {
        NewsDao.deleteAllNews();
    }

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

    public void teamCache()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                CETTopicCache cetTopicCache = new CETTopicCache();
                for (String team : teams)
                {
                    List<NewsInfo> allNews;
                    int count = 0;
                    if (isSoccerTeam(team))
                    {
                        System.out.println("SkySportsTeamApi开始:" + team);
                        final SkySportsTeamApi skySportsTeamApi = new SkySportsTeamApi(
                                team);
                        skySportsTeamApi.setMaxPages(5);
                        allNews = skySportsTeamApi.getAllNewsWithContent();
                        NewsDao.insertNews(allNews);
                        cetTopicCache.executeTasks(allNews);
                        count += allNews.size();
                        System.out.println("SkySportsTeamApi结束:" + team);

                        System.out.println("SquawkaTeamApi开始:" + team);
                        final SquawkaTeamApi squawkaTeamApi = new SquawkaTeamApi(
                                team);
                        squawkaTeamApi.setMaxPages(3);
                        allNews = squawkaTeamApi.getAllNewsWithContent();
                        NewsDao.insertNews(allNews);
                        count += allNews.size();

                        System.out.println("SquawkaTeamApi结束:" + team);
                        cetTopicCache.executeTasks(allNews);
                    }
                    else
                    {
                        System.out.println("NbaTeamApi开始:" + team);
                        final NbaTeamApi squawkaTeamApi = new NbaTeamApi(team);
                        squawkaTeamApi.setMaxPages(3);
                        allNews = squawkaTeamApi.getAllNewsWithContent();
                        NewsDao.insertNews(allNews);
                        count += allNews.size();
                        System.out.println("NbaTeamApi结束:" + team);
                        cetTopicCache.executeTasks(allNews);
                    }
                    if (count > 0)
                    {
                        map.put(team, count);
                    }
                }
            }
        }).start();

    }

    protected boolean isSoccerTeam(String team)
    {
        return NewsDao.isSoccerTeam(team);
    }
}
