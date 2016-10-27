package com.wnc.news.api.voa;

import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wnc.news.api.common.AbstractForumsHtmlPicker;
import com.wnc.news.api.common.NewsInfo;
import com.wnc.news.api.common.TeamApi;
import com.wnc.news.website.WebSite;
import com.wnc.news.website.WebSiteUtil;
import com.wnc.string.PatternUtil;
import common.utils.JsoupHelper;

public class IyubaApi extends AbstractForumsHtmlPicker implements TeamApi
{
    WebSite webSite = WebSiteUtil.getIyuba();

    public IyubaApi()
    {

    }

    @Override
    protected int getFromPage()
    {
        return 1;
    }

    @Override
    protected NewsInfo getBaseNewsInfo(Element mainDiv)
    {
        NewsInfo newsInfo = null;
        try
        {
            newsInfo = new NewsInfo();

            newsInfo.setDate(mainDiv.select(".date").first().text()
                    .replace("-", ""));
            newsInfo.setUrl(mainDiv.select("a").first().absUrl("href"));
            newsInfo.setHead_pic(mainDiv.select("img").first().absUrl("href"));
            newsInfo.setTitle(mainDiv.select(".desc_en").first().text());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            newsInfo = null;
        }
        return newsInfo;
    }

    @Override
    protected NewsInfo getNewsFromUrl(WebSite webSite, String url)
            throws Exception
    {
        NewsInfo newsInfo = new NewsInfo();
        Document documentResult = JsoupHelper.getDocumentResult(url);
        Elements select = documentResult.select("#HidedivItems table tr");
        String content = "";
        for (Element element : select)
        {
            final Element obh = element.nextElementSibling()
                    .nextElementSibling();
            content += element.text()
                    + PatternUtil.getFirstPatternGroup(obh.toString(),
                            "obj\\[3\\]\\=('.*?')") + "\n";
        }
        System.out.println(content);
        newsInfo.setHtml_content(content);

        return newsInfo;
    }

    @Override
    public String getPage(int i)
    {
        return String.format(webSite.getFormat(), i * 25);
    }

    @Override
    public List<NewsInfo> getAllNewsWithContent()
    {
        return getAllNews(webSite);
    }

    public NewsInfo getNewsFromUrl(String url) throws Exception
    {
        return getNewsFromUrl(webSite, url);
    }
}
