package com.wnc.news.engnews.helper;

public class AppConfig
{
	private static boolean isNight = false;

	public static void openNightModel()
	{
		isNight = true;
	}

	public static void closeNightModel()
	{
		isNight = false;
	}

	public static boolean isNightModel()
	{
		return isNight;
	}

}
