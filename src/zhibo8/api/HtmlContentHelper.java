package zhibo8.api;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import common.utils.JsoupHelper;

public class HtmlContentHelper
{
    public void initHtmlContent(Zb8News news)
    {
        news.setChs_content(extractNewsContent(news.getUrl()));
        news.setEng_content(extractNewsContent(news.getFrom_url()));
    }

    public String extractNewsContent(String url)
    {
        try
        {
            String html_class = WebSiteClassFactory.getHtmlClass(url);
            if (html_class == null)
            {
                return null;
            }
            Document documentResult = JsoupHelper.getDocumentResult(url);
            Elements select = documentResult.select(html_class);
            return select.html();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
