package com.wnc.news.api.autocache;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import word.Topic;

import com.alibaba.fastjson.JSONObject;
import com.wnc.basic.BasicStringUtil;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.dao.DictionaryDao;
import com.wnc.news.dao.NewsDao;

public class CETTopicCache
{
    boolean isShutdown = false;
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
            .newFixedThreadPool(3);

    public synchronized void executeTasks(List<NewsInfo> allNews)
    {
        if (!isShutdown && allNews != null)
        {
            for (final NewsInfo info : allNews)
            {
                executor.execute(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        if (BasicStringUtil.isNotNullString(info
                                .getHtml_content()))
                        {
                            List<Topic> allFind = new ArrayList<Topic>();
                            String newContent = splitArticle(
                                    info.getHtml_content(), allFind);
                            if (allFind.size() > 0)
                            {
                                JSONObject jobj = new JSONObject();
                                jobj.put("data", allFind);
                                // jobj放入数据库做cet_topics,
                                // newContent更新原有的html_content
                                NewsDao.updateContentAndTopic(info.getUrl(),
                                        newContent, jobj.toString());
                            }
                        }
                    }
                });
            }
        }
    }

    public void shutdown()
    {
        isShutdown = true;
    }

    public String splitArticle(String article, List<Topic> allFind)
    {
        String ret = "";
        Set<Topic> set = DictionaryDao.findCETWords(article.replace(
                "target=\"_blank\"", ""));
        if (set.size() == 0)
        {
            ret = article;
        }
        else
        {
            for (Topic topic : set)
            {
                if (!allFind.contains(topic))
                {
                    allFind.add(topic);
                }
            }
            ret = getDealResult(article, set);

        }
        return ret;
    }

    private String getDealResult(String aString, Set<Topic> keys)
    {
        StringBuilder result = new StringBuilder();
        int openTag = aString.indexOf("<a ");
        int closeTag;
        while (openTag > -1)
        {
            closeTag = aString.indexOf("</a>") + 4;
            String left = aString.substring(0, openTag);
            result.append(deal(left, keys));
            result.append(aString.substring(openTag, closeTag));
            aString = aString.substring(closeTag);

            openTag = aString.indexOf("<a ");
        }
        result.append(deal(aString, keys));
        return result.toString();
    }

    private String deal(String s, Set<Topic> keys)
    {
        for (Topic key : keys)
        {
            final String matched_word = key.getMatched_word();
            s = s.replace(matched_word, "<a href=\"http://m.iciba.com/"
                    + matched_word + "\">" + matched_word + "</a>");
        }
        return s;
    }
}
