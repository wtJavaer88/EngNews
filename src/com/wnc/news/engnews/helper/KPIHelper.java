package com.wnc.news.engnews.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import word.Topic;

import com.wnc.basic.BasicDateUtil;

public class KPIHelper
{
    private static List<HeadData> list = new ArrayList<HeadData>();
    private static Set<Topic> highLightWords = new HashSet<Topic>();
    private static Set<String> loved_news = new HashSet<String>();
    private static final String date = BasicDateUtil.getCurrentDateString();
    private static int selected_count = 0;

    public static void computeAll()
    {
    }

    public static HeadData getHeadData(String data)
    {
        return new HeadData();
    }

    // 新闻页面计算好了,调用
    public static void addHighLights(List<Topic> topics)
    {
        highLightWords.addAll(topics);
    }

    private static int getHLCounts()
    {
        return highLightWords.size();
    }

    public static void increaseSlectedCounts()
    {
        selected_count++;
    }

    public static void increaseLoved(String url)
    {
        if (loved_news.add(url))
        {
            // TODO 更新数据库,时间date, 收藏新闻数+1 同时插入收藏表

        }
    }

    // 在退出某个新闻页面的时候调用,或者在前一页后一页的时候
    public static void increaseViewed(int times)
    {
        // TODO 如果不存在date记录,insert
        // 否则update, viewed+1, highlights=getHLCounts,
        // times+view_time,selected_words = selected_count

    }
}
