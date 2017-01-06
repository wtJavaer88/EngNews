package zhibo8.api;

import java.util.HashMap;
import java.util.Map;

public class WebSiteClassFactory {
	static Map<String, String> websites = new HashMap<String, String>();
	static {
		websites.put("zb8", "#signals p");
		websites.put("ss", ".article__body p");
		websites.put("sw", ".entry-content p");
		websites.put("442", ".node-content p");// 442
		websites.put("goal", ".article-text p");// 进球网
		websites.put("bbc", "#story-body p");// BBC
		websites.put("dm", "div [itemprop=articleBody] p[class=mol-para-with-font]");// 每日邮报
		websites.put("arsenal", "section[class=article-text] p");
	}

	public static String getHtmlClass(String url) {
		if (url == null) {
			return null;
		}
		if (url.contains(".skysports.")) {
			return websites.get("ss");
		} else if (url.contains(".squawka.")) {
			return websites.get("sw");
		} else if (url.contains(".fourfourtwo.")) {
			return websites.get("442");
		} else if (url.contains(".zhibo8.")) {
			return websites.get("zb8");
		} else if (url.contains(".goal.")) {
			return websites.get("goal");
		} else if (url.contains(".dailymail.")) {
			return websites.get("dm");
		} else if (url.contains(".arsenal.")) {
			return websites.get("arsenal");
		}
		return null;
	}
}