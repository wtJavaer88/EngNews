package com.wnc.news.website;

public class WebSiteUtil
{
    private static WebSite squawka;
    private static WebSite skysports;
    private static WebSite basketballinsiders;

    private WebSiteUtil()
    {

    }

    public static WebSite getSquawka()
    {
        if (squawka == null)
        {
            squawka = new WebSite();
            squawka.setName("squawka");
            squawka.setDb_id("3");
            squawka.setNews_class(".entry-content p");
            squawka.setMain_div(".news-sub");
            squawka.setFormat("http://www.squawka.com/teams/%s/news?getitems=true&ajax=true&pg=%d");
        }
        return squawka;
    }

    public static WebSite getSkySports()
    {
        if (skysports == null)
        {
            skysports = new WebSite();
            skysports.setName("skysports");
            skysports.setDb_id("2");
            skysports.setNews_class(".article__body p");
            skysports.setMain_div(".news-list__body");
            skysports.setFormat("http://www.skysports.com/%s-news/more/%d");
            // skysports
            // .setFormat("http://www.skysports.com/football/ajax/digrevMoreNewsByBasketId/11670/20/%d");
        }
        return skysports;
    }

    public static WebSite getBasketballInsiders()
    {
        if (basketballinsiders == null)
        {
            basketballinsiders = new WebSite();
            basketballinsiders.setDb_id("1");
            basketballinsiders.setName("basketballinsiders");
            basketballinsiders.setNews_class("#content-area p");
            basketballinsiders.setMain_div(".infinite-post");
            basketballinsiders
                    .setFormat("http://www.basketballinsiders.com/tag/%s/page/%d/");
        }
        return basketballinsiders;
    }
}
