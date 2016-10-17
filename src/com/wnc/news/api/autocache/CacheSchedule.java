package com.wnc.news.api.autocache;

import java.util.List;

import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.api.soccer.SkySportsTeamApi;
import com.wnc.news.api.soccer.SquawkaTeamApi;
import com.wnc.news.dao.NewsDao;

public class CacheSchedule
{
    public void clearCache(final String team)
    {
        NewsDao.deleteNews();
    }

    public void teamCache(final String team)
    {

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final SkySportsTeamApi skySportsTeamApi = new SkySportsTeamApi(
                        team);
                skySportsTeamApi.setMaxPages(5);
                List<NewsInfo> allNews = skySportsTeamApi
                        .getAllNewsWithContent();
                NewsDao.insertNews(allNews);
                System.out.println("SkySportsTeamApi结束");

                final SquawkaTeamApi squawkaTeamApi = new SquawkaTeamApi(team);
                squawkaTeamApi.setMaxPages(3);
                allNews = squawkaTeamApi.getAllNews();
                NewsDao.insertNews(allNews);
                System.out.println("squawkaTeamApi结束");
            }
        }).start();

    }
}
