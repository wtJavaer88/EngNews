package com.wnc.news.api.common;

import java.util.List;

public interface TeamApi
{
    public List<NewsInfo> getAllNewsWithContent();

    public void setMaxPages(int max);

}
