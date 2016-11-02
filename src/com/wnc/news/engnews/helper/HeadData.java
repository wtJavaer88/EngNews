package com.wnc.news.engnews.helper;

public class HeadData
{
    /**
     * 浏览日期
     */
    private String date;
    /**
     * 浏览新闻数
     */
    private int viewed_news;
    /**
     * 手动选择的单词数
     */
    private int selectedWords;
    /**
     * 四六级高亮单词数
     */
    private int highlightWords;
    /**
     * 收藏的新闻数
     */
    private int loved_news;
    /**
     * 浏览新闻所用的总时间
     */
    private int times;

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public int getViewed_news()
    {
        return viewed_news;
    }

    public void setViewed_news(int viewed_news)
    {
        this.viewed_news = viewed_news;
    }

    public int getSelectedWords()
    {
        return selectedWords;
    }

    public void setSelectedWords(int selectedWords)
    {
        this.selectedWords = selectedWords;
    }

    public int getHighlightWords()
    {
        return highlightWords;
    }

    public void setHighlightWords(int highlightWords)
    {
        this.highlightWords = highlightWords;
    }

    public int getLoved_news()
    {
        return loved_news;
    }

    public void setLoved_news(int loved_news)
    {
        this.loved_news = loved_news;
    }

    public int getTimes()
    {
        return times;
    }

    public void setTimes(int times)
    {
        this.times = times;
    }
}
