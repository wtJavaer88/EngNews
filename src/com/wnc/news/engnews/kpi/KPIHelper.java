package com.wnc.news.engnews.kpi;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.wnc.basic.BasicDateUtil;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.dao.KPIDao;
import com.wnc.news.db.DatabaseManager;

public class KPIHelper
{
    private final String today = BasicDateUtil.getCurrentDateString();
    SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

    private static KPIHelper kPIHelper = new KPIHelper();

    private KPIHelper()
    {

    }

    public void closeDb()
    {
        DatabaseManager.getInstance().closeDatabase();
    }

    public static KPIHelper getInstance()
    {
        return kPIHelper;
    }

    public List<KPIData> getHistory()
    {
        return KPIDao.getAllHistory(db);
    }

    public KPIData findTodayData()
    {
        return KPIDao.findByDay(db, BasicDateUtil.getCurrentDateString());
    }

    public KPIData getHeadData(String data)
    {
        return new KPIData();
    }

    public boolean increaseLoved(int news_id)
    {
        return KPIDao.addLovedNews(db, news_id, today);
    }

    // 在退出某个新闻页面的时候调用,或者在前一页后一页的时候
    public void increaseViewed(int times, int selected_count, int topic_counts,
            NewsInfo newsInfo)
    {
        // TODO 如果不存在date记录,insert
        // 否则update, viewed+1, highlights=getHLCounts,
        // times+view_time,selected_words = selected_count
        KPIDao.insertViewHistory(db, newsInfo.getDb_id(), topic_counts, times);
        if (!KPIDao.isExistDay(db, today))
        {
            KPIDao.insertRecordByday(db, today);
        }
        KPIDao.increaseViewed(db, today, topic_counts, times, selected_count);
    }

    public boolean addSelectedWord(int db_id, String base_word)
    {
        return KPIDao.addSelectedWord(db, db_id, base_word);
    }
}
