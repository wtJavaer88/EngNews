package com.wnc.news.engnews.ui;

import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import word.Topic;

import com.wnc.news.api.autocache.CETTopicUpdate;
import com.wnc.news.api.autocache.CacheSchedule;
import com.wnc.news.api.common.Club;
import com.wnc.news.api.soccer.LeagueApi;
import com.wnc.news.dao.DictionaryDao;
import com.wnc.news.dao.NewsDao;
import common.utils.JsoupHelper;

public class NewsTest
{
    public void getClubs()
    {
        List<Club> allClubs = new LeagueApi(712).getAllClubs();
        System.out.println("allClubs数量:" + allClubs.size());
        NewsDao.insertClubs(allClubs);
        allClubs = new LeagueApi(682).getAllClubs();
        System.out.println("allClubs数量:" + allClubs.size());
        NewsDao.insertClubs(allClubs);
        allClubs = new LeagueApi(717).getAllClubs();
        System.out.println("allClubs数量:" + allClubs.size());
        NewsDao.insertClubs(allClubs);

    }

    public void topicUpdate()
    {
        new CETTopicUpdate().update();
        // new CETTopicCache().update();
    }

    public void ifSoccerTeam()
    {
        System.out.println(NewsDao.isSoccerTeam("barcelona"));
    }

    public void cacheArsenal(CacheSchedule cacheSchedule)
    {
        cacheSchedule.addTeam("arsenal");
        cacheSchedule.teamCache();
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
        cacheSchedule.teamCache();
    }

    public void test()
    {
        // http://www.basketballinsiders.com/nba-am-the-quadruple-double/
        Document doc;
        try
        {
            doc = JsoupHelper
                    .getDocumentResult("http://www.basketballinsiders.com/nba-am-the-quadruple-double/");
            final Elements contents = doc.select("#content-area p");
            if (contents != null && contents.text().length() > 200)
            {
                System.out.println(contents.text());
                List<Topic> findCETWords = DictionaryDao.findCETWords(contents
                        .text());
                System.out.println(findCETWords.size());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
