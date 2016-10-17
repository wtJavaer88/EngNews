package com.wnc.news.api.test;

import java.util.List;

import com.wnc.news.api.common.Club;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.api.soccer.LeagueApi;
import com.wnc.news.api.soccer.SquawkaTeamApi;

public class Pagetest
{
    public static void main(String[] args) throws Exception
    {

        List<Club> allClubs = new LeagueApi(641).getAllClubs();
        for (Club club : allClubs)
        {
            List<NewsInfo> allNews = new SquawkaTeamApi(club.getFull_name())
                    .getAllNews();
            // List<NewsInfo> allNews = new TeamApi("arsenal").getAllNews();
            for (NewsInfo newsInfo : allNews)
            {
                System.out.println(newsInfo.getDate() + newsInfo.getTitle());
            }
        }
    }
}
