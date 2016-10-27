package com.wnc.news.engnews.helper;

import java.util.List;

import com.wnc.news.api.common.NewsInfo;

public class ViewNewsHolder
{
    static List<NewsInfo> newsInfos;
    static int index = 0;

    public static void refrehList(List<NewsInfo> list)
    {
        newsInfos = list;
        index = 0;
    }

    public static List<NewsInfo> getCurList()
    {
        return newsInfos;
    }

    public static void refreh(NewsInfo info)
    {
        if (newsInfos != null && info != null || info.getUrl() != null)
        {
            NewsInfo newsInfo;
            for (int i = 0; i < newsInfos.size(); i++)
            {
                newsInfo = newsInfos.get(i);
                if (newsInfo.getUrl().equalsIgnoreCase(info.getUrl()))
                {
                    index = i;
                    break;
                }
            }
        }
    }

    public static NewsInfo getNext()
    {
        if (index < newsInfos.size() - 1)
        {
            index++;
        }
        return newsInfos.get(index);
    }

    public static NewsInfo getPre()
    {
        if (index > 0)
        {
            index--;
        }
        return newsInfos.get(index);
    }
}
