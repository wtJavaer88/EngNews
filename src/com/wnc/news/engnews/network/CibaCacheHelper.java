package com.wnc.news.engnews.network;

import java.util.HashMap;
import java.util.Map;

import translate.site.iciba.CibaWordTranslate;

public class CibaCacheHelper
{
    static Map<String, CibaWordTranslate> map = new HashMap<String, CibaWordTranslate>();

    public static CibaWordTranslate getCibaTranslate(String word)
    {
        if (map.containsKey(word))
        {
            return map.get(word);
        }
        final CibaWordTranslate cibaWordTranslate = new CibaWordTranslate(word);
        map.put(word, cibaWordTranslate);
        return cibaWordTranslate;
    }
}
