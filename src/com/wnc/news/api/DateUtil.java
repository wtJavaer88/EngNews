package com.wnc.news.api;

import com.wnc.basic.BasicStringUtil;
import com.wnc.string.PatternUtil;

public class DateUtil
{
	static String[] engMonthes = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };

	public static String getDateFromEngMonth(String engDate)
	{
		String ret = "";
		String d = PatternUtil.getFirstPatternGroup(engDate, "(\\d+)");
		String y = PatternUtil.getLastPatternGroup(engDate, "(\\d+)");
		String month = PatternUtil.getLastPatternGroup(engDate, " (\\w+) ");
		// if(){
		// }
		int findMonth = -1;
		for (int i = 0; i < engMonthes.length; i++)
		{
			if (engMonthes[i].toLowerCase().startsWith(month.toLowerCase()))
			{
				findMonth = i + 1;
				break;
			}
		}
		if (findMonth > -1)
		{
			month = BasicStringUtil.fillLeftString(findMonth + "", 2, "0");
			if (d.length() > y.length())
			{
				ret = d + month + y;
			}
			else
			{
				ret = y + month + d;
			}
		}
		return ret;
	}
}
