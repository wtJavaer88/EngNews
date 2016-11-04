package com.wnc.news.api.autocache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import word.Topic;

import com.alibaba.fastjson.JSONObject;
import com.wnc.basic.BasicStringUtil;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.dao.DictionaryDao;
import com.wnc.news.dao.NewsDao;
import com.wnc.news.engnews.network.WebUrlHelper;
import com.wnc.string.PatternUtil;

public class CETTopicCache
{
    Logger log = Logger.getLogger(CETTopicCache.class);

    boolean isShutdown = false;
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
            .newFixedThreadPool(3);

    /**
     * 在找词算法改变的时候,重新算,万不得已不用
     */
    public void update()
    {
        List<NewsInfo> findAllNews = NewsDao.findAllNewsWithUrlFilter(" ");
        log.info("待更新新闻数目:" + findAllNews.size());
        executeTasks(findAllNews);
        shutdown();
        ifOver();
    }

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
                        if (info.getCet_topics() != null
                                && info.getCet_topics().length() > 10)
                        {
                            return;
                        }
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
                                final String splitLine = "----------------------------------------";
                                int cCounts = PatternUtil.getAllPatternGroup(
                                        newContent, splitLine).size();

                                NewsDao.updateContentAndTopic(info.getUrl(),
                                        newContent, jobj.toString(),
                                        allFind.size(), cCounts);
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
        executor.shutdown();
    }

    /**
     * allFind要返回给调用方,要保证顺序
     * 
     * @param article
     * @param allFind
     * @return
     */
    public String splitArticle(String article, List<Topic> allFind)
    {
        String ret = "";
        List<Topic> list = DictionaryDao.findCETWords(article.replaceAll(
                "<.*?>", ""));
        article = removeHtmlAttribute(article);
        if (list.size() == 0)
        {
            ret = article;
        }
        else
        {
            Set<String> passedTopics = PassedTopicCache.getPassedTopics();
            // 去掉已经过关的单词
            for (Topic topic : list)
            {
                if (!passedTopics.contains(topic.getTopic_base_word())
                        && !allFind.contains(topic))
                {
                    allFind.add(topic);
                }
            }
            ret = getDealResult(article, allFind);
        }
        return ret;
    }

    private String removeHtmlAttribute(String article)
    {
        return article.replaceAll("<a.*?(href=\".*?\").*?>", "<a $1>")
                .replaceAll("<p.*?>", "<p>").replaceAll("<img.*?>", "");
    }

    /**
     * 新闻内容处理时用到的需要进行单词长度排序
     * 
     * @param aString
     * @param keys
     * @return
     */
    private String getDealResult(String aString, List<Topic> keys)
    {
        List<Topic> sortedKeys = new ArrayList<Topic>(keys);
        Collections.sort(sortedKeys, new Comparator<Topic>()
        {
            @Override
            public int compare(Topic arg0, Topic arg1)
            {

                return arg0.getMatched_word().length()
                        - arg1.getMatched_word().length();
            }
        });
        StringBuilder result = new StringBuilder();
        int openTag = aString.indexOf("<a ");
        int closeTag;
        while (openTag > -1)
        {
            closeTag = aString.indexOf("</a>") + 4;
            String left = aString.substring(0, openTag);
            result.append(deal(left, keys));
            if (closeTag > openTag)
            {
                result.append(aString.substring(openTag, closeTag));
                aString = aString.substring(closeTag);
            }
            else
            {
                // 如果出现异常情况,先有</a>后有<a>
                aString = aString.substring(openTag);
            }

            openTag = aString.indexOf("<a ");
        }
        result.append(deal(aString, keys));
        return result.toString();
    }

    protected String deal(String s, List<Topic> keys)
    {
        for (Topic key : keys)
        {
            final String matched_word = key.getMatched_word();
            s = replace(s, matched_word);

            // 将匹配的单词进行首字母大小写匹配替换
            String matched_word2 = "";
            if (matched_word.substring(0, 1).matches("[a-z]{1}"))
            {
                matched_word2 = matched_word.substring(0, 1).toUpperCase()
                        + matched_word.substring(1);
            }
            else if (matched_word.substring(0, 1).matches("[A-Z]{1}"))
            {
                matched_word2 = matched_word.substring(0, 1).toLowerCase()
                        + matched_word.substring(1);
            }
            s = replace(s, matched_word2);
        }
        return s;
    }

    private String replace(String s, final String matched_word)
    {
        s = s.replaceAll("([^a-zA-Z]|^)" + matched_word + "([^a-zA-Z]|$)",
                "$1<a href=\"" + WebUrlHelper.getWordUrl(matched_word) + "\">"
                        + matched_word + "</a>$2");
        return s;
    }

    public void ifOver()
    {
        waiting();
        log.info("全部Topic缓存结束!");
    }

    protected void waiting()
    {
        try
        {
            boolean loop = true;
            do
            { // 等待所有任务完成
                loop = !executor.awaitTermination(2, TimeUnit.SECONDS); // 阻塞，直到线程池里所有任务结束
            }
            while (loop);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
