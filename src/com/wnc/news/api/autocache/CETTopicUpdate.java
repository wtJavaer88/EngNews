package com.wnc.news.api.autocache;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import word.Topic;

import com.alibaba.fastjson.JSONObject;
import com.wnc.basic.BasicStringUtil;
import com.wnc.news.api.common.AbstractForumsHtmlPicker;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.dao.NewsDao;
import com.wnc.news.engnews.helper.WebUrlHelper;
import com.wnc.string.PatternUtil;
import common.utils.JsoupHelper;

public class CETTopicUpdate
{
    boolean isShutdown = false;
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
            .newFixedThreadPool(10);
    static Logger log = Logger.getLogger(CETTopicUpdate.class);

    int x = 0;

    /**
     * 清理原有的已PASS的单词的链接
     */
    public void update()
    {
        List<NewsInfo> findAllNews = NewsDao.findAllNewsWithUrlFilter("");
        System.out.println("所有新闻数目:" + findAllNews.size());
        executeTasks(findAllNews);
        shutdown();
        ifOver();
    }

    /**
     * 清理原有的已PASS的单词的链接
     */
    public void updateCounts()
    {
        final List<NewsInfo> findAllNews = NewsDao.findAllNewsWithUrlFilter("");
        System.out.println("所有新闻数目:" + findAllNews.size());
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                for (NewsInfo info : findAllNews)
                {
                    List<Topic> topics = new ArrayList<Topic>();
                    int tCounts = 0;
                    int cCounts = 0;
                    if (info.getCet_topics() != null)
                    {
                        topics = JSONObject.parseArray(
                                JSONObject.parseObject(info.getCet_topics())
                                        .getString("data"), Topic.class);
                        tCounts = topics.size();
                    }

                    final String html_content = info.getHtml_content();
                    final String splitLine = AbstractForumsHtmlPicker.SPlIT_LINE;
                    if (html_content != null
                            && info.getHtml_content().contains(splitLine))
                    {
                        cCounts = PatternUtil.getAllPatternGroup(html_content,
                                splitLine).size();
                    }
                    if (tCounts > 0 || cCounts > 0)
                    {
                        System.out.println("更新:" + tCounts + "  " + cCounts);
                        NewsDao.updateContent(info.getUrl(), tCounts, cCounts);
                    }
                }
            }
        }).start();
    }

    /**
     * 清理原有的已PASS的单词的链接
     */
    public void updateBasketBallDate()
    {
        final List<NewsInfo> findAllNews = NewsDao.findAllNBANews();
        System.out.println("所有NBA新闻数目:" + findAllNews.size());
        executor.execute(new Runnable()
        {

            @Override
            public void run()
            {
                for (NewsInfo info : findAllNews)
                {
                    Document doc;
                    try
                    {
                        doc = JsoupHelper.getDocumentResult(info.getUrl());
                        if (doc != null)
                        {
                            NewsDao.updateDate(info.getUrl(),
                                    (doc.select(".date-contain .post-date")
                                            .attr("datetime").replace("-", "")));
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public synchronized void executeTasks(List<NewsInfo> allNews)
    {

        if (!isShutdown && allNews != null)
        {
            for (final NewsInfo info : allNews)
            {
                executor.execute(new Runnable()
                {
                    private List<Topic> oldTopics;

                    @Override
                    public void run()
                    {
                        if (BasicStringUtil.isNotNullString(info
                                .getHtml_content()))
                        {
                            Set<String> passedTopics = PassedTopicCache
                                    .getPassedTopics();

                            List<Topic> oldTopics = new ArrayList<Topic>();
                            if (info.getCet_topics() != null)
                            {
                                oldTopics = JSONObject.parseArray(JSONObject
                                        .parseObject(info.getCet_topics())
                                        .getString("data"), Topic.class);
                            }
                            else
                            {
                                x++;
                                log.info(info.getUrl() + " 没有Topics");
                                return;
                            }
                            List<Topic> needRemoveTopics = new ArrayList<Topic>();
                            for (Topic topic : oldTopics)
                            {
                                if (passedTopics.contains(topic
                                        .getTopic_base_word()))
                                {
                                    needRemoveTopics.add(topic);
                                }
                            }
                            if (needRemoveTopics.size() > 0)
                            {
                                log.info(info.getUrl() + " needRemoveTopics:"
                                        + needRemoveTopics.size());
                                String newContent = removeTopicsFromArticle(
                                        info.getHtml_content(),
                                        needRemoveTopics);
                                List<Topic> newTopics = new ArrayList<Topic>();
                                for (Topic topic : oldTopics)
                                {
                                    if (!passedTopics.contains(topic
                                            .getTopic_base_word()))
                                    {
                                        newTopics.add(topic);
                                    }
                                }
                                final String splitLine = AbstractForumsHtmlPicker.SPlIT_LINE;
                                int cCounts = PatternUtil.getAllPatternGroup(
                                        newContent, splitLine).size();

                                JSONObject jobj = new JSONObject();
                                jobj.put("data", newTopics);
                                NewsDao.updateContentAndTopic(info.getUrl(),
                                        newContent, jobj.toString(),
                                        newTopics.size(), cCounts);
                            }
                            else
                            {
                                String s = info.getHtml_content();
                                int i = s.indexOf("<a href=\">");
                                if (i == -1)
                                {
                                    return;
                                }

                                int j = 0;
                                String newstr = "";
                                while (i > -1)
                                {
                                    j = s.substring(i).indexOf("</a>") + i;
                                    if (j > -1)
                                    {
                                        newstr += s.substring(0, i)
                                                + s.substring(i + 10, j);
                                        s = s.substring(j + 4);
                                    }
                                    else
                                    {
                                        s = s.substring(i + 10);
                                    }
                                    i = s.indexOf("<a href=\">");
                                }
                                newstr += s;
                                System.out.println(newstr.length());
                                NewsDao.updateContent(info.getUrl(), newstr);
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

    public String removeTopicsFromArticle(String article,
            List<Topic> needRemoveTopics)
    {
        article = removeHtmlAttribute(article);
        return deal(article, needRemoveTopics);
    }

    private String removeHtmlAttribute(String article)
    {
        return article.replaceAll("<a.*?(href=\".*?\").*?>", "<a $1>")
                .replaceAll("<p.*?>", "<p>").replaceAll("<img.*?>", "");
    }

    protected String deal(String s, List<Topic> keys)
    {
        for (Topic key : keys)
        {
            final String matched_word = key.getMatched_word();
            s = s.replace("<a href=\"" + WebUrlHelper.getWordUrl(matched_word)
                    + "\">" + matched_word + "</a>", matched_word);
        }
        return s;
    }

    public void ifOver()
    {
        waiting();
        log.info("全部Topic清理完成!");
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
