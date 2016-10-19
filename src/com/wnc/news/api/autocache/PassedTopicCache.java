package com.wnc.news.api.autocache;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.wnc.tools.FileOp;
import common.uihelper.MyAppParams;

public class PassedTopicCache
{
    private static Set<String> passedTopics = new HashSet<String>();
    static Logger log = Logger.getLogger(NewsContentService.class);

    static
    {
        List<String> list = FileOp.readFrom(MyAppParams.PASS_TXT, "UTF-8");
        for (String s : list)
        {
            passedTopics.add(s);
        }
        if (list.size() > 0)
        {
            log.info("Pass的单词数:" + passedTopics.size() + " 最后一个是:"
                    + list.get(list.size() - 1));
        }
    }

    public static Set<String> getPassedTopics()
    {
        return passedTopics;
    }

    public static void init()
    {

    }
}
