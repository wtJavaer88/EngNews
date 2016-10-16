package com.wnc.news.api.soccer;

import java.util.List;

public class Pagetest
{
	public static void main(String[] args) throws Exception
	{

		List<Club> allClubs = new LeagueApi(641).getAllClubs();
		for (Club club : allClubs)
		{
			List<NewsInfo> allNews = new SoccerTeamApi(club.getFull_name()).getAllNews();
			// List<NewsInfo> allNews = new TeamApi("arsenal").getAllNews();
			for (NewsInfo newsInfo : allNews)
			{
				System.out.println(newsInfo.getDate() + newsInfo.getTitle());
			}
		}
	}
}
