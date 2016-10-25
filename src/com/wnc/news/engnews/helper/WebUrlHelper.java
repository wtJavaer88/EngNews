package com.wnc.news.engnews.helper;

public class WebUrlHelper
{
    public static String getTranslateUrl(String str)
    {
        return "http://fanyi.baidu.com/translate?aldtype=16047&query=&keyfrom=baidu&smartresult=dict&lang=auto2zh#en/zh/"
                + str;
    }

    public static String getWordUrl(String word)
    {
        return "http://m.iciba.com/" + word;
    }
}
