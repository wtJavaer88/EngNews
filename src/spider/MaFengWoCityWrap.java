package spider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONObject;
import com.wnc.basic.BasicFileUtil;
import com.wnc.basic.BasicNumberUtil;
import com.wnc.string.PatternUtil;
import com.wnc.tools.FileOp;
import common.uihelper.MyAppParams;
import common.utils.SslUtils;

public class MaFengWoCityWrap
{
	public static String JING_DIAN = "jd";// 景点
	public static String MEI_SHI = "cy";// 美食
	public static String GOU_WU = "gw";// 购物
	static
	{
		try
		{
			SslUtils.trustAllHttpsCertificates();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	final long poi_time = System.currentTimeMillis();
	String type = JING_DIAN;

	public MaFengWoCityWrap(String type)
	{
		this.type = type;
	}

	public void grabData()
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				int city = 0;
				int midCity = -1;
				try
				{
					boolean flag = midCity == -1;
					for (String s : FileOp.readFrom(MyAppParams.getInstance()
							.getWorkPath() + "cityall.txt"))
					{
						city = BasicNumberUtil.getNumber(PatternUtil
								.getFirstPattern(s, "\\d+"));
						if (!flag && city == midCity)
						{
							flag = true;
						}

						if (!flag)
						{
							continue;
						}
						downloadCityWrap(city);

						Thread.sleep(300);
					}
				}
				catch (Exception e)
				{

					e.printStackTrace();
				}
			}

			private void downloadCityWrap(int city) throws Exception
			{
				try
				{
					int page = 1;
					while (true)
					{
						if (city == 10444 && page < 455)
						{
							page++;
							continue;
						}

						String ret = Jsoup
								.connect(
										"https://m.mafengwo.cn/" + type + "/"
												+ city + "/gonglve.html?page="
												+ page + "&is_ajax=1")
								.timeout(15000).ignoreContentType(true)
								.execute().body();

						JSONObject obj = JSONObject.parseObject(ret.replace(
								"\\n", ""));
						parsePointOfDoc(city, obj.getString("html"));

						Integer integer = obj.getInteger("has_more");
						if (integer == 0)
						{
							break;
						}
						page++;
					}
				}
				catch (Exception e)
				{
					BasicFileUtil.writeFileString(MyAppParams.getInstance()
							.getWorkPath() + "city-" + type + "-err.txt", city
							+ "/" + e.getMessage() + "\r\n", null, true);
				}
			}

		}).start();
	}

	/**
	 * 景点解析
	 * 
	 * @param city
	 * @param html
	 * @throws Exception
	 */
	private void parsePointOfDoc(int city, String html) throws Exception
	{
		Document parse = Jsoup.parse(html);
		Elements select = parse.select(".poi-list > div > a");
		List<JSONObject> poiList = new ArrayList<JSONObject>();
		for (Element element : select)
		{
			JSONObject jobj = new JSONObject();
			jobj.put("u", element.attr("href").trim());
			jobj.put("t", element.select(".hd").text().replace(" ", ""));

			Element bd = element.select(".bd").first();
			if (bd != null)
			{
				if (bd.select("dt img").first() != null)
					jobj.put(
							"i",
							PatternUtil.getFirstPatternGroup(
									bd.select("dt img").first().attr("src"),
									"(.*?)\\?.*$").trim());
				if (bd.select(".star span").first() != null)
					jobj.put("s", PatternUtil.getFirstPatternGroup(
							bd.select(".star span").attr("style"),
							"width:(\\d+)"));
				String num = bd.select("p span.num").text();
				List<String> patternStrings = PatternUtil.getPatternStrings(
						num, "\\d+");
				if (patternStrings.size() > 0)
				{
					jobj.put("n1", patternStrings.get(0));
					if (patternStrings.size() > 1)
					{
						jobj.put("n2", patternStrings.get(1));
					}
				}
				jobj.put(
						"mt",
						Arrays.asList(bd.select(".m-t").text().replace(" ", "")
								.split(" ")));
				if (bd.select("p").last() != null)
					jobj.put("l", bd.select("p").last().text());

				if (bd.select(".comment").size() > 0)
				{
					jobj.put("r", bd.select(".comment").first().ownText()
							.replace(" ", ""));
				}
			}
			poiList.add(jobj);
		}
		JSONObject cityJson = new JSONObject();
		cityJson.put("c", city);
		cityJson.put("list", poiList);
		// System.out.println(cityJson);
		BasicFileUtil.writeFileString(MyAppParams.getInstance().getWorkPath()
				+ "city-" + this.type + "lsit-" + poi_time + ".txt", cityJson
				+ "\r\n", null, true);
	}
}
