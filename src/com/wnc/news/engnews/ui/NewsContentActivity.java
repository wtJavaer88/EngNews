package com.wnc.news.engnews.ui;

import org.apache.log4j.Logger;
import org.jsoup.select.Elements;

import word.Topic;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.engnews.R;
import com.wnc.basic.BasicStringUtil;
import com.wnc.news.api.autocache.CETTopicCache;
import com.wnc.news.dao.NewsDao;
import com.wnc.news.db.DatabaseManager;
import com.wnc.news.engnews.helper.KPIHelper;
import com.wnc.news.richtext.WebImgText;
import com.wnc.string.PatternUtil;

public class NewsContentActivity extends BaseNewsActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        log = Logger.getLogger(NewsContentActivity.class);
        if (news_info != null)
        {
            setNewsTitle(news_info.getTitle());
            initData();
        }
    }

    Thread dataThread1;
    Thread dataThread2;

    @Override
    public void initData()
    {
        hideCursor();
        hideWordZone();
        if (dataThread1 != null)
        {
            dataThread1.interrupt();
        }
        if (dataThread2 != null)
        {
            dataThread2.interrupt();
        }
        dataThread1 = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Message msg2 = new Message();
                msg2.what = MESSAGE_ON_IMG_TEXT;
                msg2.obj = new WebImgText("<img src=\""
                        + news_info.getHead_pic() + "\"/>").getCharSequence();
                if (Thread.currentThread().isInterrupted())
                {
                    System.out.println("should stop thread1 no send msg");
                    return;
                }
                handler.sendMessage(msg2);
            }
        });
        dataThread1.setDaemon(true);
        dataThread1.start();

        dataThread2 = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Elements contents = null;
                    Message msg = new Message();
                    if (news_info != null
                            && news_info.getHtml_content() != null)
                    {
                        totalWords = PatternUtil.getAllPatternGroup(
                                news_info.getHtml_content(), "['\\w]+").size();
                        log.info("该缓存新闻单词数:" + totalWords);
                        String str = news_info.getCet_topics();
                        if (BasicStringUtil.isNotNullString(str))
                        {
                            allFind = JSONObject.parseArray(JSONObject
                                    .parseObject(str).getString("data"),
                                    Topic.class);
                            log.info("该缓存新闻关键词数:" + allFind.size());
                        }
                        else
                        {
                            log.error(news_info.getUrl() + " 没找到任何Topic,将重新查找!");
                            final String splitArticle = new CETTopicCache()
                                    .splitArticle(news_info.getHtml_content(),
                                            allFind);
                            if (allFind.size() > 0)
                            {
                                news_info.setHtml_content(splitArticle);
                                JSONObject jobj = new JSONObject();
                                jobj.put("data", allFind);
                                news_info.setCet_topics(jobj.toString());
                                news_info.setTopic_counts(allFind.size());
                                SQLiteDatabase db = DatabaseManager
                                        .getInstance().openDatabase();
                                if (!NewsDao.isExistUrl(db, news_info.getUrl()))
                                {
                                    NewsDao.insertSingleNews(db, news_info);
                                }
                                else
                                {
                                    NewsDao.updateContentAndTopic(
                                            news_info.getUrl(),
                                            news_info.getHtml_content(),
                                            news_info.getCet_topics(),
                                            news_info.getTopic_counts(),
                                            news_info.getComment_counts());
                                }
                                DatabaseManager.getInstance().closeDatabase();
                            }
                        }
                        KPIHelper.addHighLights(allFind);
                        msg.obj = news_info.getHtml_content();
                        msg.what = MESSAGE_ON_CONTEXT_TEXT;
                    }
                    if (Thread.currentThread().isInterrupted())
                    {
                        return;
                    }
                    handler.sendMessage(msg);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        dataThread2.setDaemon(true);
        dataThread2.start();
    }

    @Override
    public void dispatchMsg(Message msg)
    {
        switch (msg.what)
        {
        case MESSAGE_ON_CONTEXT_TEXT:
            mTextView.setText(new com.wnc.news.richtext.HtmlRichText(msg.obj
                    .toString()).getCharSequence());
            if (hasTopics())
            {
                topicListBt.setVisibility(View.VISIBLE);
                topicListBt.setText("" + allFind.size());
            }

            break;
        case MESSAGE_ON_IMG_TEXT:
            newsImgTv.setVisibility(View.VISIBLE);
            newsImgTv.setText((CharSequence) msg.obj);
            break;
        case MESSAGE_ON_WORD_DISPOSS_CODE:
            System.out.println("自动清空!");
            hideVirtualBts();
            hideWordZone();
            break;
        case MESSAGE_ON_RUNTIME_TEXT:
            ((TextView) findViewById(R.id.bt_activity_runtime))
                    .setText(activityTimeUtil.getTranedRunTime());
            break;
        default:
            break;
        }
    }
}
