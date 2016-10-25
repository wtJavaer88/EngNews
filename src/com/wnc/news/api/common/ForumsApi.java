package com.wnc.news.api.common;

import java.util.List;

public interface ForumsApi
{
    public List<NewsInfo> getAll();

    public void setMaxPages(int max);

}
