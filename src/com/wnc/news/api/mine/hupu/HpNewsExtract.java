package com.wnc.news.api.mine.hupu;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.wnc.news.api.mine.zhibo8.SportType;
import com.wnc.news.api.mine.zhibo8.Zb8News;

public class HpNewsExtract
{
	ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors
			.newFixedThreadPool(10);

	/**
	 * 
	 * @param datetime
	 *            格式 "2017-04-29 00:00:00"
	 * @param count
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public List<HpNews> getHpNewsAfterDateTime(String datetime, SportType type)
			throws Exception
	{
		ArrayList<HpNews> list = new ArrayList<HpNews>();
		new HpDayNewsThread(datetime, list, SportType.Soccer).run();
		return list;
	}

	public List<HpNews> getZb8NewsAfterDateTime(String datetime, SportType type)
			throws Exception
	{
		ArrayList<HpNews> list = new ArrayList<HpNews>();
		new HpDayNewsThread(datetime, list, SportType.Soccer).run();
		ArrayList<Zb8News> listZb8 = new ArrayList<Zb8News>();

		for (HpNews hpNews : list)
		{
			listZb8.add((Zb8News) hpNews);
		}
		return list;
	}

}
