package com.wnc.news.api.common;

import java.util.List;

import org.jsoup.nodes.Element;

public interface TeamApi
{
    public List<NewsInfo> getAllNews();

    public boolean hasReachOldLine();

    public NewsInfo getNewsInfo(Element mainDiv);

    public List<NewsInfo> getAllNewsWithContent();
}
