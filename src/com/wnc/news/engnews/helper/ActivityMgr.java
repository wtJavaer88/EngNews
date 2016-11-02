package com.wnc.news.engnews.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.alibaba.fastjson.JSONObject;
import com.wnc.basic.BasicDateUtil;
import com.wnc.basic.BasicFileUtil;
import com.wnc.news.api.common.NewsInfo;
import common.uihelper.MyAppParams;
import common.utils.TimeUtil;

public class ActivityMgr
{
    public static void gotoIE(Context activity, String page)
    {
        try
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(page));
            activity.startActivity(intent);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void loveNewsRecord(Context activity, NewsInfo news_info)
    {
        String url = news_info.getUrl();
        JSONObject jobj = new JSONObject();
        jobj.put("date", BasicDateUtil.getCurrentDateTimeString());
        jobj.put("url", url);
        jobj.put("title", news_info.getTitle());
        BasicFileUtil.writeFileString(MyAppParams.LOVE_NEWS_TXT,
                jobj.toString() + "\r\n", "UTF-8", true);
        common.app.ToastUtil.showShortToast(activity, "操作成功!");
        // TODO 插入数据库
    }

    public static void viewedNewsRecord(Context activity, NewsInfo news_info,
            int viewtime)
    {
        String url = news_info.getUrl();
        JSONObject jobj = new JSONObject();
        jobj.put("date", BasicDateUtil.getCurrentDateTimeString());
        jobj.put("url", url);
        jobj.put("title", news_info.getTitle());
        jobj.put("viewtime", TimeUtil.timeToText(viewtime));
        BasicFileUtil.writeFileString(MyAppParams.VIEWED_NEWS_TXT,
                jobj.toString() + "\r\n", "UTF-8", true);
        // TODO 插入数据库
    }

    public static void selectedWordRecord(Context activity, NewsInfo news_info,
            String selectWord)
    {
        JSONObject jobj = new JSONObject();
        jobj.put("date", BasicDateUtil.getCurrentDateTimeString());
        jobj.put("news_id", news_info.getDb_id());
        jobj.put("selectWord", selectWord);
        BasicFileUtil.writeFileString(MyAppParams.SELECTED_WORDS_TXT,
                jobj.toString() + "\r\n", "UTF-8", true);
        // TODO 插入数据库
    }
}
