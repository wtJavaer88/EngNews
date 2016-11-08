package com.wnc.news.engnews.kpi;

public class AssortKPI
{
    private int count;
    private String date;
    private CharSequence content;
    private KPI_TYPE kpi_type;

    public AssortKPI(int count, String date, CharSequence content,
            KPI_TYPE kpi_type)
    {
        super();
        this.count = count;
        this.date = date;
        this.content = content;
        this.kpi_type = kpi_type;
    }

    public CharSequence getContent()
    {
        return content;
    }

    public void setContent(CharSequence content)
    {
        this.content = content;
    }

    public int getCount()
    {
        return count;
    }

    public void setCount(int count)
    {
        this.count = count;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public KPI_TYPE getkPI_TYPE()
    {
        return kpi_type;
    }

    public void setkPI_TYPE(KPI_TYPE kpi_type)
    {
        this.kpi_type = kpi_type;
    }
}
