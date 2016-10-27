package com.wnc.news.api.autocache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import android.database.sqlite.SQLiteDatabase;

import com.wnc.news.api.common.ForumsApi;
import com.wnc.news.api.common.NewsApi;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.api.common.StaticsHelper;
import com.wnc.news.api.forums.RealGmApi;
import com.wnc.news.api.forums.RedditApi;
import com.wnc.news.api.nba.NbaTeamApi;
import com.wnc.news.api.soccer.SkySportsTeamApi;
import com.wnc.news.api.soccer.SquawkaTeamApi;
import com.wnc.news.dao.NewsDao;
import com.wnc.news.db.DatabaseManager;
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
                SQLiteDatabase db = DatabaseManager.getInstance()
                        .openDatabase();
                CETTopicCache cetTopicCache = new CETTopicCache();

                for (String team : teams)
                {
                    try
                    {
                        int newsCount = 0;
                        if (StaticsHelper.isSoccerTeam(db, team))
                        {

                            newsCount += cache(new SkySportsTeamApi(team), 5,
                                    cetTopicCache, "SkySportsTeamApi-" + team);
                            newsCount += cache(new SquawkaTeamApi(team), 3,
                                    cetTopicCache, "SquawkaTeamApi-" + team);
                        }
                        else
                        {
                            newsCount += cache(new NbaTeamApi(team), 2,
                                    cetTopicCache, "NbaTeamApi-" + team);
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
                DatabaseManager.getInstance().closeDatabase();
                cetTopicCache.shutdown();
                cetTopicCache.ifOver();
                allCached1 = true;
            }

        }).start();

    }

    private int cache(NewsApi teamApi, int pages, CETTopicCache cetTopicCache,
            String webtip)
    {
        int ret = 0;
        List<NewsInfo> allNews;
        log.info(webtip + "开始:");
        teamApi.setMaxPages(pages);
        allNews = teamApi.getAllNewsWithContent();
        for (NewsInfo newsInfo : allNews)
        {
            if (!NewsDao.isExistUrl(db_forums, newsInfo.getUrl()))
            {
                NewsDao.insertSingleNews(db_forums, newsInfo);
            }
        }
        cetTopicCache.executeTasks(allNews);
        ret = allNews.size();
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

    SQLiteDatabase db_forums = DatabaseManager.getInstance().openDatabase();

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
                    cache(new RealGmApi(), 1, cetTopicCache, "RealGm论坛");

                    cache(new RedditApi(MyAppParams.getInstance()
                            .getSoccModelName()), 1, cetTopicCache,
                            "Reddit/Soccer论坛");

                    cache(new RedditApi(MyAppParams.getInstance()
                            .getBaskModelName()), 1, cetTopicCache,
                            "Reddit/NBA论坛");

                    DatabaseManager.getInstance().closeDatabase();
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
}
