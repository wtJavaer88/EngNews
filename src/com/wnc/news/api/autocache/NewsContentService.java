package com.wnc.news.api.autocache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.wnc.news.api.common.NewsInfo;
import common.utils.JsoupHelper;

public class NewsContentService
{
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
            .newFixedThreadPool(5);
    List<NewsInfo> allNews;
    List<NewsInfo> allNews2 = new ArrayList<NewsInfo>();

    public NewsContentService(List<NewsInfo> allNews)
    {
        this.allNews = allNews;
    }

    public void execute()
    {
        for (final NewsInfo info : allNews)
        {
            executor.execute(new Runnable()
            {

                @Override
                public void run()
                {
                    System.out.println(info.getUrl());
                    Document doc;
                    try
                    {
                        doc = JsoupHelper.getDocumentResult(info.getUrl());
                        final Elements contents = doc.select(info.getWebsite()
                                .getNews_class());
                        if (contents != null)
                        {
                            info.setHtml_content(contents.toString());
                            allNews2.add(info);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            });
        }
        executor.shutdown();
    }

    public List<NewsInfo> getResult()
    {
        waiting();
        System.out.println("allNews.size():" + allNews.size());
        return allNews2;
    }

    private void waiting()
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