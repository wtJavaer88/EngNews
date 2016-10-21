package com.wnc.news.api.autocache;

import java.util.ArrayList;
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
import com.wnc.news.dao.NewsDao;

public class CETTopicUpdate
{
    boolean isShutdown = false;
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
            .newFixedThreadPool(3);
    static Logger log = Logger.getLogger(CETTopicUpdate.class);

    int x = 0;

    /**
     * 清理原有的已PASS的单词的链接
     */
    public void update()
    {
        List<NewsInfo> findAllNews = NewsDao.findAllNewsWithUrlFilter("");
        // System.out.println("findErrContentNews数目:" + findAllNews.size());
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
                                log.info(info.getUrl()
                                        + " 没有Topics.................总数:"
                                        + (++x));
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
                            System.out.println(info.getUrl()
                                    + " needRemoveTopics:"
                                    + needRemoveTopics.size());
                            if (needRemoveTopics.size() > 0)
                            {
                                String newContent = splitArticle(
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
                                JSONObject jobj = new JSONObject();
                                jobj.put("data", newTopics);
                                NewsDao.updateContentAndTopic(info.getUrl(),
                                        newContent, jobj.toString());
                                // System.out.println(newContent);
                                // System.out.println(jobj);
                            }
                            else
                            {
                                String s = info.getHtml_content();
                                System.out.println(info.getTitle() + " "
                                        + s.length());
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

    public String splitArticle(String article, List<Topic> needRemoveTopics)
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
            s = s.replace("<a href=\"http://m.iciba.com/" + matched_word
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
