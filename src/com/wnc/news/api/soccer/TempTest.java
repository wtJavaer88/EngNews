package com.wnc.news.api.soccer;

import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class TempTest
{
	public static void main(String[] args) throws Exception
	{
		System.out.println(DateUtil.getDateFromEngMonth("29 Sep 2016"));
		Document doc = JsoupHelper.getDocumentResult("http://www.squawka.com/news/arsenals-granit-xhaka-admits-being-a-football-freak-hell-even-watch-league-one/797163");
		final Elements select = doc.select(".entry-content p");
		for (Element element : select)
		{
			List<Node> childNodes = element.childNodes();
			for (Node node : childNodes)
			{
				System.out.println(node);
			}
		}
	}
}
