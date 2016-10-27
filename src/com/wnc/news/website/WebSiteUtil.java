package com.wnc.news.website;

public class WebSiteUtil
{
    // 足球
    private static WebSite squawka;
    private static WebSite skysports;
    // 篮球
    private static WebSite basketballinsiders;
    // 论坛
    private static WebSite realgm;

    private static WebSite redditB;
    private static WebSite redditS;
    private static WebSite iyuba;

    private WebSiteUtil()
    {

    }

    public static WebSite getRedditSoccer()
    {
        if (redditS == null)
        {
            redditS = getReddit("soccer");
        }
        return redditS;
    }

    private static WebSite getReddit(String type)
    {
        WebSite site;
        site = new WebSite();
        site.setName("reddit");
        site.setDb_id("5");
        site.setNews_class(".content .md");
        site.setMain_div(".siteTable .entry");
        site.setFormat("https://www.reddit.com/r/" + type + "/?page=%d");
        return site;
    }

    public static WebSite getRedditNBA()
    {
        if (redditB == null)
        {
            redditB = getReddit("nba");
        }
        return redditB;
    }

    public static WebSite getIyuba()
    {
        if (iyuba == null)
        {
            iyuba = new WebSite();
            iyuba.setName("iyuba");
            iyuba.setDb_id("100");
            iyuba.setMain_div(".list-contents li");
            iyuba.setFormat("http://voa.iyuba.com/voachangsu_0_%d.html");
        }
        return realgm;
    }

    public static WebSite getRealGm()
    {
        if (realgm == null)
        {
            realgm = new WebSite();
            realgm.setName("realgm");
            realgm.setDb_id("4");
            realgm.setNews_class(".postbody .content");
            realgm.setMain_div(".inner .row");
            realgm.setFormat("http://forums.realgm.com/boards/viewforum.php?f=6&start=%d");
        }
        return realgm;
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
