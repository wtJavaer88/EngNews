package com.wnc.news.website;

public class WebSite
{
    String name;
    String news_class;
    String main_div;
    String format;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getNews_class()
    {
        return news_class;
    }

    public void setNews_class(String news_class)
    {
        this.news_class = news_class;
    }

    @Override
    public String toString()
    {
        return "WebSite [name=" + name + ", news_class=" + news_class
                + ", main_div=" + main_div + "]";
    }

    public String getMain_div()
    {
        return main_div;
    }

    public void setMain_div(String main_div)
    {
        this.main_div = main_div;
    }

    public String getFormat()
    {
        return format;
    }

    public void setFormat(String format)
    {
        this.format = format;
    }
}
